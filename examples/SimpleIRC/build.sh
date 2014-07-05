#!/bin/bash

cryptoerasec -sigsourcepath sig-src -sourcepath .:sig-src -classpath ../../rt-classes SimpleIRCClient.jl
if [ $? -eq "0" ]
  then javac -cp ../../rt-classes *.java
  else exit 1
fi
