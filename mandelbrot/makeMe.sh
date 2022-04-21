#!/bin/bash

OUT="target/$(pwd | xargs basename).exe"
CC="$(which g++.exe)"
FLAGS="-O2\
    -g\
    -Wall\
    -Wno-narrowing\
    -Werror\
    -Wshadow\
    -pedantic\
    -Wextra\
    --std=c++17\
    -I $(pwd)/include\
    "

SRC="$(find "source/" -name "*.cpp")"
GLUT="-lglu32 -mwindows glut64.lib -lopengl32"

if [ ! -d target/ ]; then
    mkdir target
fi

$CC $FLAGS -o $OUT $SRC

chmod +x $OUT
