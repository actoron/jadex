#!/bin/bash
cd ..
mvn install -P jadex-android
if [ "$?" -eq "0" ]
then
cd jadex-android-application-test
mvn package android:deploy
else
echo "failed."
fi
