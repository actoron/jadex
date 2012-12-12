@echo off

IF PROCESSOR_ARCHITECTURE == amd64 OR PROCESSOR_ARCHITEW6432 == amd64 
(
	goto amd64
)
ELSE IF PROCESSOR_ARCHITECTURE == ia64 OR PROCESSOR_ARCHITEW6432 == ia64 
(
	goto ia64
)
ELSE
(
    goto x86
)

:amd64
	set DIR=%CD%\amd64
	goto hostname
	
:ia64
	set DIR=%CD%\ia64
	goto hostname
	
:x86
	set DIR=%CD%\x86
	goto hostname

:hostname
FOR /F "usebackq" %%i IN (`hostname`) DO SET HOSTNAME=%%i

:install
	echo Installing JadexPro service...
	%DIR%\JadexPro install --Install %DIR%\JadexPro.exe ^
	  --Description "Jadex Production Edition" ^
	  --Jvm auto --Startup auto ^
	  --LogPath %CD%\logs --StdOutput %CD%\logs\svc$out.txt --StdError %CD%\logs\svc$err.txt ^
	  --Classpath %CD%\..\lib\jadex-platform-standalone-launch-2.3-SNAPSHOT.jar ^
	  --StartPath %CD%\..\ ^
	  --StartMode jvm --StartClass jadex.platform.ServiceStarter --StartMethod start ^
	  --StopMode jvm --StopClass jadex.platform.ServiceStarter --StopMethod stop ^
	  ++StartParams -platformname;%HOSTNAME%_svc$* ^
	  ++StartParams -gui;false ^
	  ++StartParams -ssltransport;true ^
	  ++StartParams -logging;true

:start
	echo Starting JadexPro service...
	%DIR%\JadexPro start
	echo success
	Pause
