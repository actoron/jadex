#!/bin/bash
##########################################
# Android Deployment Script              #
# v5                                     #
# Julian Kalinowski			 #
# 8kalinow@informatik.uni-hamburg.de     #
##########################################
ADB_PATH=$ANDROID_HOME/tools/adb
MVN_PATH=$(which mvn)
AWK_PATH=$(which awk)
PROJECT_PATH=$(pwd)
APKFILE="target/jadex-android-application-test-2.1.1-SNAPSHOT.apk"
MANIFEST="AndroidManifest.xml"
skip_build=false
adb_device=""
remove_app=false
clean=false
clearlog=true

function echo_green {
	echo -en '\E[1;32m'"\033[1m$1\033[0m"
	tput sgr0
}

function echo_red {
	echo -en '\E[1;31m'"\033[1m$1\033[0m"
	tput sgr0
}

function echo_bold {
	echo -en "\033[1m$1\033[0m"
}

function e {
    echo_green "[deploy]   "
    params=$1
    shift
    echo $params "$@"
}

function f {
    echo_red "[deploy]   "
    params=$1
    shift
    echo $params "$@"
}

function display_help () {
	echo 	"Android build and deployment script"
	display_usage
	echo	"Builds your android application using maven and deploys it to all connected android devices."
	echo	"Requirements: \$ANDROID_HOME must be set, 'awk' must be installed and AndroidManifest.xml should be in the current directory, as well as your pom.xml"
	echo	""
	echo	"Arguments:"
	echo	"  -s, --skip-build     skips build, just deploys"
	echo 	"  -d, --device=DEVICE  deploys/removes only on DEVICE, see 'adb devices' for device ID"
	echo 	"  -r, --remove         remove app from devices"
    	echo    "  -c, --clean          clean before build"
	echo	"  -kl, --keeplog	keep logcat log while deploying instead of clearing it"
	echo	""
}

function display_usage () {
	echo "USAGE: $0 [-d=DEVICE | --device=DEVICE] [-s | --skip-build] [-r | --remove] [-c | --clean]"
}

function checkAdbPermissions () {
$ADB_PATH devices | grep -q "no permissions"
	if [ $? -eq "0" ]; then
		e "No Permissions to access usb device."
		e "Restarting adb daemon as root..."
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
      if [ -z "$2" ]; then
	f "No device given."
	exit 1
      fi
      adb_device="$2"
      shift 2
      ;;
    -h|--help)
      display_help
      # no shifting needed here, we'll quit!
      exit
      ;;
    -r|--remove)
      remove_app=true
      shift
      ;;
    -c|--clean)
      clean=true
      shift
      ;;
    -kl|--keeplog)
      clearlog=false
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
	f "ANDROID_HOME not set!"
	exit 1
fi

if [ -z $MVN_PATH ]; then
	f "mvn Befehl not set!"
	exit 1
fi

if [ -z $AWK_PATH ]; then
	f "awk Befehl not set!"
	exit 1
fi

if [ ! -e $ADB_PATH ]; then
	ADB_PATH=$ANDROID_HOME/platform-tools/adb
	if [ ! -e $ADB_PATH ]; then
		ADB_PATH=$(which adb)
	fi
fi

if [ ! -e $ADB_PATH ]; then
	f "$ANDROID_HOME/tools/adb does not exist!"
	exit 1
fi

if [ ! -e $MANIFEST ] ; then
	f "Manifest $MANIFEST not found!"
	exit 1
fi

# get package and main activity name from Manifest:

e $(echo_bold "Parsing $MANIFEST...")
APPLICATION_PACKAGE=$(grep -Eo package="\S*" $MANIFEST | sed -rn 's/package="(\S*)"(>|)/\1/p')
while read line; 
do
	temp=$(echo "$line" | sed -rn 's/<activity.*name.*"(\S*).*">.*/\1/p')
	if [ ! -z $temp ] ; then
		activity_name=$temp
	fi
	temp=$(echo $line | grep -r '<action.*android.intent.action.MAIN')
	if [ ! -z  "$temp" ] ; then
		MAIN_ACTIVITY=$activity_name
		break
	fi	
done < $MANIFEST

if [ -z $APPLICATION_PACKAGE ] ; then
	f "Application Package could not be read from $MANIFEST!"
	exit 1
fi

if [ -z $MAIN_ACTIVITY ] ; then
	f "Application Package could not be read from $MANIFEST!"
	exit 1
fi

e "Application Package is: $(echo_bold $APPLICATION_PACKAGE)"
e "Application Main Activity is: $(echo_bold $MAIN_ACTIVITY)"

if [ $remove_app == "true" ]; then
    checkAdbPermissions	
    if [ ! -z $adb_device ]; then
		DEVICES=$adb_device
	fi
	e "Removing app from $(echo_bold $(num_devices)) devices"
	for d in $DEVICES; do
		e "Removing app from device: $(echo_bold $d)"
		$ADB_PATH -s $d shell pm uninstall -k $APPLICATION_PACKAGE
		if [ ! "$?" -eq "0" ]; then
			f "Removing app from device $d failed."
		else
            e "App removed."    
        fi
	done
	exit
fi

if $clean && $skip_build ; then
    f "--clean does not work in combination with --skip-build!"
    exit 1
fi

if [ $clean == true ]; then
    e $(echo_bold "Cleaning...")
    #cd .. 
    $MVN_PATH clean #-P jadex-android
    if [ ! "$?" -eq 0 ]; then
        f "Cleaning failed."
        exit 1
    fi
    cd $PROJECT_PATH
fi

if [ $skip_build == false ]; then
	e $(echo_bold "Building...")
	#cd ..
	$MVN_PATH package
	if [ ! "$?" -eq "0" ]; then
		f "Maven Build failed. Not deploying."
		exit 1
	fi
fi
cd $PROJECT_PATH
checkAdbPermissions
if [ ! -z $adb_device ]; then
	DEVICES=$adb_device
fi

	
e $(echo_bold "Deploying...")
e "Deploying to $(echo_bold $(echo $DEVICES | awk '{print NF;}')) devices (parallel deploy)"
for d in $DEVICES; do
	if [ $clearlog == true ]; then
		e "Clearing logcat of device $(echo_bold $d)"
		$ADB_PATH -s $d logcat -c
	fi

	#e "Transferring file to device: $(echo_bold $d)"
	$ADB_PATH -s $d install -r $APKFILE &
done
wait

for d in $DEVICES; do
	#if [ ! "$?" -eq "0" ]; then
	#	f "Transfer to device $d failed."
	#else 
		e "Starting Application on device $d"
		$ADB_PATH -s $d shell am start -n $APPLICATION_PACKAGE/$APPLICATION_PACKAGE.$MAIN_ACTIVITY
        if [ ! "$?" -eq "0" ]; then
            f "Failed to start Apllication on device $d."
        else
            e "Application started on device $d."
        fi
	#fi
done

