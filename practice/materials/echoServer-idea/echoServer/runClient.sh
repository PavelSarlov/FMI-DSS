#!/bin/bash

_CMD_JAVA=`which java`;

_N=127.0.0.1

if [[ -n $1 ]] ; then
	_N=$1 ;
fi

$_CMD_JAVA -cp ./out/production/ EchoClient $_N



