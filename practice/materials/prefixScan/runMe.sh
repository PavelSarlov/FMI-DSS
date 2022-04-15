#!/bin/bash

_CMD_JAVA=`which java`;

$_CMD_JAVA -Xmx8G -cp ./out/production/ TaskRunner $1 $2 $3


