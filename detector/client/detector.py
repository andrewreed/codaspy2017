import sys
import time
from socket import *
from collections import deque
from collections import OrderedDict

class Tracker:
  def __init__(self, ipPair, timestamp):
    self.addresses = ipPair
    self.startTime = int(timestamp)
    self.lastTime = timestamp
    self.window = deque([],30)
    self.identified = False
    self.videoTitle = ""
    self.firstID = -1
    
  def addADU(self, timestamp, size, clientSocket):
    self.lastTime = timestamp
    self.window.append(size)

    #if (len(self.window) == 30):
    if (len(self.window) == 30) and (not self.identified):
      output = str(self.window[0])
      for x in range(1,30):
        output += "," + str(self.window[x])
      clientSocket.send(output + "\n")

      sys.stderr.write("DEBUG_LOOKUP\n")

      result = clientSocket.recv(1024)
      if result != "none\n":
        #sys.stderr.write(result)
        if not self.identified:
          tokens = result.split("\t")
          self.identified = True
          self.videoTitle = tokens[0]
          self.firstID = int(self.lastTime - self.startTime)
    
  def getLastTime(self):
    return self.lastTime

  def printVideo(self):
    if self.identified:
      sys.stdout.write("ID:\t" + self.addresses + "\t" + str(self.startTime) + "\t" + str(int(self.lastTime)) + "\t" + str(self.firstID) + "\t" + self.videoTitle + "\n")

### Main Program ###
start_time = time.time()

serverName = sys.argv[1]
serverPort = int(sys.argv[2])

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((serverName,serverPort))

cnxTracker = OrderedDict()

for adu in sys.stdin:
  adu = adu.rsplit('\n') # remove trailing newline
  fields = adu[0].split()

  try:
    currentTime = float(fields[0])
    cnxKey = fields[1]
    size = int(fields[2])
  except ValueError:
    continue

  if cnxKey in cnxTracker:
    cnx = cnxTracker[cnxKey]
    del cnxTracker[cnxKey]
  else:
    cnx = Tracker(cnxKey, currentTime)

  cnx.addADU(currentTime, size, clientSocket)    

  cnxTracker[cnxKey] = cnx

  deleteList = []
  for k,v in cnxTracker.iteritems():
    if ((currentTime - v.getLastTime()) > 120.0):
      deleteList.append(k)
    else:
      break
  for key in deleteList:
    cnxTracker[key].printVideo()
    del cnxTracker[key]
  
clientSocket.send("complete\n")
clientSocket.close()

for k,v in cnxTracker.iteritems():
  v.printVideo()

print("--- %s seconds ---" % (time.time() - start_time))
