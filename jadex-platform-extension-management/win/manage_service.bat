@echo off

:hostname
FOR /F "usebackq" %%i IN (`hostname`) DO SET HOSTNAME=%%i

:start
	echo Please wait while connecting to service...
	cd ..
	java -jar "%CD%\lib\jadex-platform-standalone-launch-3.0-SNAPSHOT.jar" ^
		-ssltcptransport true ^
		-printpass false ^
		-welcome false ^
		-cli false ^
		-logging true ^
		-jccplatforms "\"%HOSTNAME%_svc$\"" ^
		>win\logs\mgr$out.txt 2>win\logs\mgr$log.txt