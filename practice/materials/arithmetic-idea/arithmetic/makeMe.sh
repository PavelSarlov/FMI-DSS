#!/bin/bash

_CMD_JAVAC=`which javac`

mkdir -p ./out/production/arithmetic

$_CMD_JAVAC -cp ./out/production/arithmetic -d ./out/production/arithmetic src/*.java
