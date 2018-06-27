@echo off

:pathname
FOR %%F IN ("%CD%") DO SET PATHNAME=%%~sF


IF "%PROCESSOR_ARCHITECTURE%" == "AMD64" (
	goto amd64
) ELSE IF "%PROCESSOR_ARCHITEW6432%" == "AMD64" (
	goto amd64
) ELSE IF "%PROCESSOR_ARCHITECTURE%" == "IA64" (
	goto ia64
) ELSE IF "%PROCESSOR_ARCHITEW6432%" == "IA64" (
	goto ia64
) ELSE (
    goto x86
)

:amd64
	set DIR=%PATHNAME%\amd64
	goto hostname
	
:ia64
	set DIR=%PATHNAME%\ia64
	goto hostname
	
:x86
	set DIR=%PATHNAME%\x86
	goto hostname

:hostname
FOR /F "usebackq" %%i IN (`hostname`) DO SET HOSTNAME=%%i

:install
	echo Installing JadexPro service...
	%DIR%\JadexPro install --Install %DIR%\JadexPro.exe ^
	  --Description "Jadex Production Edition" ^
	  --Jvm auto --Startup auto ^
	  --LogPath %PATHNAME%\logs --StdOutput %PATHNAME%\logs\svc$out.txt --StdError %PATHNAME%\logs\svc$log.txt ^
	  --Classpath %PATHNAME%\..\lib\jadex-platform-standalone-launch-${jadex_build_version}.jar ^
	  --StartPath %PATHNAME%\..\ ^
	  --StartMode jvm --StartClass jadex.platform.ServiceStarter --StartMethod start ^
	  --StopMode jvm --StopClass jadex.platform.ServiceStarter --StopMethod stop ^
	  ++StartParams -platformname;%HOSTNAME%_svc$* ^
	  ++StartParams -gui;false ^
	  ++StartParams -ssltcptransport;true ^
	  ++StartParams -logging;true

:start
	echo Starting JadexPro service...
	%DIR%\JadexPro start
	echo success
	Pause
