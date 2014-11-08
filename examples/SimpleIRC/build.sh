#!/bin/bash

../../bin/cryptoerasec -sigsourcepath sig-src -sourcepath .:sig-src -classpath ../../rt-classes -heapabstraction "cs(0)" SimpleIRCClient.jl
if [ $? -eq "0" ]
  then javac -cp ../../rt-classes *.java
  else exit 1
fi
