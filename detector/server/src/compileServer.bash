#!/bin/bash
javac -cp .:../lib/Apache/commons-math3-3.3.jar Netflid.java
jar cvfm Netflid.jar Manifest.txt *.class
mv Netflid.jar ../
rm *.class
