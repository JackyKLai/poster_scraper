#!/bin/bash
BASEDIR=$(dirname "$0")
file="$BASEDIR/poster_scraper.jar"
if [ ! -x $file ]
then
    chmod a+x $file
fi
java -jar $file