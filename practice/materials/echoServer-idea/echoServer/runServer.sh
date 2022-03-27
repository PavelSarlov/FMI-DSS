#!/bin/bash

_CMD_JAVA=`which java`;

_N=5

if [[ -n $1 ]] ; then
	_N=$1 ;
fi

$_CMD_JAVA -cp ./out/production/ EchoServer


