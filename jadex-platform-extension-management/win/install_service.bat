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
	goto start
	
:ia64
	set DIR=%CD%\ia64
	goto start
	
:x86
	set DIR=%CD%\x86
	goto start

:start
	%DIR%\JadexPro install --Install %DIR%\JadexPro.exe ^
	  --Description "Jadex Production Edition" ^
	  --Jvm auto --Startup auto ^
	  --LogPath %CD% --StdOutput %CD%\out.txt --StdError %CD%\err.txt ^
	  --Classpath %CD%\..\lib\jadex-platform-standalone-launch-2.3-SNAPSHOT.jar ^
	  --StartPath %CD%\..\ ^
	  --StartMode jvm --StartClass jadex.platform.ServiceStarter --StartMethod start ^
	  --StopMode jvm --StopClass jadex.platform.ServiceStarter --StopMethod stop ^
	  ++StartParams -gui;false ^
	  ++StartParams -logging;true

	%DIR%\JadexPro start