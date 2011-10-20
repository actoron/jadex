#!/bin/bash
mvn clean package install -f ../pom.xml -P jadex-android-artifacts
if [ "$?" == 0 ]; then
	mvn -DskipTests clean package install -f ../jadex-android-bluetooth/pom.xml
fi

if [ "$?" == 0 ]; then
	./deploy.sh
fi
