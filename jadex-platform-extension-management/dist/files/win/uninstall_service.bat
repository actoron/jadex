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
	goto uninstall
	
:ia64
	set DIR=%PATHNAME%\ia64
	goto uninstall
	
:x86
	set DIR=%PATHNAME%\x86
	goto uninstall

:uninstall
	echo Uninstalling JadexPro service...
	%DIR%\JadexPro delete
	echo success
	pause
