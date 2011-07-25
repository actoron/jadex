#!/bin/bash
ADB_PATH=$ANDROID_HOME/tools/adb
MVN_PATH=$(which mvn)
AWK_PATH=$(which awk)
PROJECT_PATH=$(pwd)
APPLICATION_PACKAGE="de.unihamburg.vsis.jadexAndroid_test"
MAIN_ACTIVITY="MainMenu"


function checkAdbPermissions () {
$ADB_PATH devices | grep -q "no permissions"
	if [ $? -eq "0" ]; then
		echo "No Permissions to access usb device."
		echo "Restarting adb daemon as root..."
		sudo $ADB_PATH kill-server
		sudo $ADB_PATH start-server
		sleep 1
		checkAdbPermissions
	fi

	DEVICES=$($ADB_PATH devices | $AWK_PATH 'NR==2 {print;exit}')
	if [ -z "$DEVICES" ]; then
		echo "No device attached. Attach a device and press enter"
		read
		checkAdbPermissions
	fi
}

if [ -z $ANDROID_HOME ]; then 
	echo "ANDROID_HOME nicht gesetzt!"
	exit 1
fi

if [ -z $MVN_PATH ]; then
	echo "mvn Befehl nicht gefunden!"
	exit 1
fi

if [ -z $AWK_PATH ]; then
	echo "awk Befehl nicht gefunden!"
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
if [ ! "$?" -eq "0" ]; then
	echo "Maven Build failed. Not deploying."
	exit 1
else
	cd $PROJECT_PATH
	checkAdbPermissions
	echo "Transferring file to device..."
	$ADB_PATH install -r target/jadex-android-0.0.1-SNAPSHOT.apk
	echo "Starting Application on device"
	$ADB_PATH shell am start -n $APPLICATION_PACKAGE/$APPLICATION_PACKAGE.$MAIN_ACTIVITY
fi
