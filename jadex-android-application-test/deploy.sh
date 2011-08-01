#!/bin/bash
##########################################
# Android Deployment Script		 #
# v3 					 #
# 8kalinow@informatik.uni-hamburg.de	 #
##########################################
ADB_PATH=$ANDROID_HOME/tools/adb
MVN_PATH=$(which mvn)
AWK_PATH=$(which awk)
PROJECT_PATH=$(pwd)
APPLICATION_PACKAGE="de.unihamburg.vsis.jadexAndroid_test"
MAIN_ACTIVITY="MainMenu"
skip_build=false
adb_device=""
remove_app=false

function echo_green {
	echo -en '\E[1;32m'"\033[1m$1\033[0m"
	tput sgr0
}

function echo_red {
	echo -e '\E[1;31m'"\033[1m$1\033[0m"
	tput sgr0
}

function echo_bold {
	echo -en "\033[1m$1\033[0m"
}

function display_help () {
	echo 	"Android build and deployment script"
	display_usage
	echo	"Builds $APPLICATION_PACKAGE using maven and deploys it to all connected android devices."
	echo	""
	echo	"Arguments:"
	echo	"  -s, --skip-build 	skips build, just deploys"
	echo 	"  -d, --device=DEVICE	deploys/removes only on DEVICE, see 'adb devices' for device ID"
	echo 	"  -r, --remove		remove app from devices"
	echo	""
	echo	"Need ANDROID_HOME to be set and 'awk' to be in PATH"

}

function display_usage () {
	echo "USAGE: $0 [-d=DEVICE | --device=DEVICE] [-s | --skip-build] [-r | --remove]"
}

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
	DEVICES=$($ADB_PATH devices | $AWK_PATH '/device$/ {$2=""; print}')
	#DEVICES=$($ADB_PATH devices | $AWK_PATH 'NR>=2 {print;exit}')
	if [ -z "$DEVICES" ]; then
		echo "No device attached. Attach a device and press enter"
		read
		checkAdbPermissions
	fi
}

function num_devices () {
	echo $DEVICES | awk '{print NF;}'
}

while [ "${1+isset}" ]; do
  case "$1" in
    -s|--skip-build)
      skip_build=true
      shift
      ;;
    -d|--device)
      adb_device="$2"
      shift 2
      ;;
    -h|--help)
      display_help # a function ;-)
      # no shifting needed here, we'll quit!
      exit
      ;;
    -r|--remove)
      remove_app=true
      shift
      ;;
    *)
      echo "Error: Unknown option: $1" >&2
      display_usage
      exit 1
      ;;
  esac
done

if [ -z $ANDROID_HOME ]; then 
	echo "ANDROID_HOME not set!"
	exit 1
fi

if [ -z $MVN_PATH ]; then
	echo "mvn Befehl not set!"
	exit 1
fi

if [ -z $AWK_PATH ]; then
	echo "awk Befehl not set!"
	exit 1
fi

if [ ! -e $ADB_PATH ]; then
	ADB_PATH=$ANDROID_HOME/platform-tools/adb
	if [ ! -e $ADB_PATH ]; then
		ADB_PATH=$(which adb)
	fi
fi

if [ ! -e $ADB_PATH ]; then
	echo_red "$ANDROID_HOME/tools/adb does not exist!"
	exit 1
fi

if [ $remove_app == "true" ]; then
	if [ ! -z $adb_device ]; then
		DEVICES=$adb_device
	fi
	echo -n "Removing app from " 
	echo_bold $(num_devices)
	echo " devices"
	for d in $DEVICES; do
		echo -n "Removing app from device: "
		echo_bold "$d\n"
		$ADB_PATH -s $d shell pm uninstall -k $APPLICATION_PACKAGE
		if [ ! "$?" -eq "0" ]; then
			echo_red "Removing app from device $d failed."
		fi
	done
	exit
fi

if [ $skip_build == false ]; then
	echo_bold "Building...\n"
	cd ..
	$MVN_PATH install -P jadex-android
	if [ ! "$?" -eq "0" ]; then
		echo_red "Maven Build failed. Not deploying."
		exit 1
	fi
fi
cd $PROJECT_PATH
checkAdbPermissions
if [ ! -z $adb_device ]; then
	DEVICES=$adb_device
fi

echo_bold "Deploying to "
echo_green $(echo $DEVICES | awk '{print NF;}') 
echo_bold " devices.\n"
for d in $DEVICES; do
	echo -n "Transferring file to device: "
	echo_bold "$d\n"
	$ADB_PATH -s $d install -r target/jadex-android-0.0.1-SNAPSHOT.apk
	if [ ! "$?" -eq "0" ]; then
		echo_red "Transfer to device $d failed."
	else 
		echo "Starting Application on device $d"
		$ADB_PATH -s $d shell am start -n $APPLICATION_PACKAGE/$APPLICATION_PACKAGE.$MAIN_ACTIVITY
	fi
done

