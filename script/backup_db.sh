#!/bin/bash

# Script for taking a back-up of the legup database
# Use psql to restore

if [ "$1" = aws ]; 
  then
    SUDO="sudo postgres"
fi

DATE=`date +%Y%m%d`
FILEPATH=/var/legup/db_dumps/$DATE.sql

echo "Making legup backup for $DATE to $FILEPATH"

$SUDO pg_dump legup > $FILEPATH
