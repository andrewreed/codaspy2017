import sys

### Main Program
for adu in sys.stdin:
  adu = adu.rsplit('\n') # remove trailing newline
  fields = adu[0].split()

  try:
    timestamp = fields[0]
    hostA = fields[1].rsplit('.',1)
    direction = fields[2][0]
    hostB = fields[3].rsplit('.',1)
    size = fields[4]
  except ValueError:
    continue

  if (direction == ">"):
    src = hostA
    dst = hostB
  else:
    src = hostB
    dst = hostA

  if (src[1] != "443"):
    continue

  print(timestamp +"\t" + src[0] + ":" + src[1] + ">" + dst[0] + ":" + dst[1] + "\t" + size)
