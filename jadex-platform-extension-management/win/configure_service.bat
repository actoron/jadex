@echo off

:hostname
FOR /F "usebackq" %%i IN (`hostname`) DO SET HOSTNAME=%%i

:start
	cd ..
	java -jar %CD%\lib\jadex-platform-standalone-launch-2.3-SNAPSHOT.jar -jccplatforms "\"%HOSTNAME%_svc$\""