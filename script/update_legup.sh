#!/bin/bash

# Script for pulling updates and rebuilding and restarting legup

cd /home/ubuntu/git/legup
echo "pulling from git"
git pull
PID=`ps aux | grep legup | grep -v grep | sed 's/[a-z]*[ ]*\([0-9]*\).*/\1/'`
echo "killing $PID"
kill $PID
echo "recompiling"
mvn compile
echo "restarting"
nohup ./run.sh &
