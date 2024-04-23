#!/bin/bash

COMMAND=$1
PACKR=$2
JDK=$3
OUTDIR=$4

print-help() {
    echo "Usage: distribute.sh [COMMAND] ...
    mac <packr jar> <jdk archive> <outdir>        build an executable for macOS
    windows <packr jar> <jdk archive> <outdir>    build an executable for windows
    linux <packr jar> <jdk archive> <outdir>      build an executable for linux
    help                                          print this help text

    <packr jar> is a jar containing packr, you can get one from https://github.com/libgdx/packr/releases
    <jdk archive> is a path to a java 17 jdk binary from https://adoptium.net/temurin/archive/?version=17
    <outdir> is an empty directory which packr will populate with the executable\
    "
    exit 0
}


mac() {
  java -jar $PACKR \
       --platform mac \
       --jdk $JDK \
       --useZgcIfSupportedOs \
       --executable seasthethrone-mac \
       --classpath lwjgl3/build/lib/seasthethrone-1.0.0.jar \
       --mainclass edu.cornell.jade.seasthethrone.lwjgl3.Lwjgl3Launcher \
       --resources assets \
       --output $OUTDIR
} 

windows() {
  java -jar $PACKR \
       --platform windows64 \
       --jdk $JDK \
       --useZgcIfSupportedOs \
       --executable seasthethrone-windows \
       --classpath lwjgl3/build/lib/seasthethrone-1.0.0.jar \
       --mainclass edu.cornell.jade.seasthethrone.lwjgl3.Lwjgl3Launcher \
       --resources assets \
       --output $OUTDIR
}

linux() {
  java -jar $PACKR \
       --platform linux64 \
       --jdk $JDK \
       --useZgcIfSupportedOs \
       --executable seasthethrone-linux \
       --classpath lwjgl3/build/lib/seasthethrone-1.0.0.jar \
       --mainclass edu.cornell.jade.seasthethrone.lwjgl3.Lwjgl3Launcher \
       --resources assets \
       --output $OUTDIR
}

case $COMMAND in 
    mac) mac ;;
    windows) windows ;;
    linux) linux ;;
    help) print-help;;
    *) print-help ;;
esac
