#!/bin/bash
cd ..
mvn install -P jadex-android
if [ "$?" -eq "0" ]
then
cd jadex-android-application-test
adb install -r target/jadex-android-0.0.1-SNAPSHOT.apk
else
echo "failed."
fi
