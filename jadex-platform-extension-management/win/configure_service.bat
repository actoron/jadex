@echo off

:hostname
FOR /F "usebackq" %%i IN (`hostname`) DO SET HOSTNAME=%%i

:start
	echo Please wait while connecting to service...
	cd ..
	java -jar %CD%\lib\jadex-platform-standalone-launch-2.3-SNAPSHOT.jar ^
		-slltransport true ^
		-jccplatforms "\"%HOSTNAME%_svc$\""