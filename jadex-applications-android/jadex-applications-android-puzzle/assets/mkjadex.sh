#!/bin/bash
BASEDIR="/home/julakali/workspace_bac/jadex/"
TARGET="$BASEDIR/jadex-applications-android-classloading/assets"
MODULES="jadex-android-parent jadex-android-commons jadex-android-antlr jadex-android-xmlpull jadex-runtimetools-android 
jadex-platform-extension-webservice-android jadex-parent jadex-commons jadex-tools-base jadex-javaparser jadex-xml jadex-rules 
jadex-rules-eca jadex-kernel-base jadex-kernel-micro jadex-kernel-component jadex-kernel-bdi jadex-kernel-bpmn jadex-model-bpmn 
jadex-kernel-bdibpmn jadex-platform jadex-platform-extension-webservice jadex-bridge"

rm *.jar

cd $BASEDIR
./mvn.sh -P dist -f pom-android-dist.xml -DskipTests clean package install

for m in $MODULES; do
	echo $m
	cp $m/target/$m-*SNAPSHOT.jar $TARGET/
done;

cd $TARGET
~/android/android-sdk-linux_86/platform-tools/dx --dex --output=jadex.jar *.jar
