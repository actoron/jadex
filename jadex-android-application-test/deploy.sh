#!/bin/bash
ADB_PATH=$ANDROID_HOME/tools/adb
MVN_PATH=$(which mvn)

if [ -z $ANDROID_HOME ]; then 
	echo "ANDROID_HOME nicht gesetzt!"
	exit 1
fi

if [ -z $MVN_PATH ]; then
	echo "mvn Befehl nicht gefunden!"
	exit 1
fi

if [ ! -e $ADB_PATH ]; then
	ADB_PATH=$ANDROID_HOME/platform-tools/adb
	if [ ! -e $ADB_PATH ]; then
		ADB_PATH=$(which adb)
	fi
fi

if [ ! -e $ADB_PATH ]; then
	echo "$ANDROID_HOME/tools/adb existiert nicht!"
	exit 1
fi

cd ..
$MVN_PATH install -P jadex-android
if [ "$?" -eq "0" ]
then
cd jadex-android-application-test
$ADB_PATH install -r target/jadex-android-0.0.1-SNAPSHOT.apk
else
	echo "Maven Build failed. Not deploying."
fi
