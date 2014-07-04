#!/bin/bash

cryptoerasec -classpath ../../rt-classes SimpleIRCClient.jl
if [ $? ]
  then javac -cp ../../rt-classes *.java
fi
