#!/bin/bash

_CMD_JAVA=`which java`;

$_CMD_JAVA \
  -cp ./out/production/arithmetic \
  -Djava.rmi.server.codebase=http://rmi.yaht.net/repo/arith.bin/ \
  -Djava.rmi.server.useCodebaseOnly=false \
  ArithClient
