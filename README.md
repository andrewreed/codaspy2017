# codaspy2017
This is the GitHub repo for the code used in the CODASPY 2017 paper. You can obtain our (i) Netflix fingerprint database and our (ii) test capture from:

[Google Drive](https://drive.google.com/drive/folders/0B3cMrOhPT_6zOUNYNUdEN0ZFXzA?resourcekey=0-uh6TIA_pwOrRz9HlvEht9g&usp=sharing)

This will allow you to run the experiment that we conducted in Section 5.3.1 of the paper.

Notes:

1. The Netflix fingerprint database was created in March-May 2016.
2. Our traffic capture was created with adudump. For access to adudump, please contact Dr. Jeff Terrell at info@altometrics.com. If you have the source for adudump, feel free to contact us if you have issues compiling it or using it.
3. Throughout this repo, we will assume that you have saved the provided files to your home directory (e.g. ~/db and ~/traffic_capture).

## Usage

The easiest way to get started is to run the program on our traffic capture:

1. Open a terminal, navigate to the server subdirectory, and start the server with the provided script. Make sure to specify a fingerprint dataset, like so: `./runServer.bash ~/db/codaspy_full.txt`
2. On our machine, it takes ~20 minutes to load the dataset. Please wait until you see the message `Server started` before moving to the next step. Note that the server will require ~30GB of memory to run.
3. Open a second terminal, navigate to the client subdirectory, and run the client on a capture file, like so: `cat ~/traffic_capture/2016_09_02_traffic_capture.txt | grep ADU | awk '$6 > 200000' | cut -d" " -f2,3,4,5,6 | python preprocessor.py | python detector.py 127.0.0.1 10007 2> debug.txt 1> stdout.txt`

Further information on the server and client can be obtained in their respective READMEs.
