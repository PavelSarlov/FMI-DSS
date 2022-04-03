#!/bin/bash

_CMD_JAVAC=`which javac`

$_CMD_JAVAC -cp ./out/production/ -d ./out/production/ src/*.java

