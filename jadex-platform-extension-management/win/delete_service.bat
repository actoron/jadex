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
	goto delete
	
:ia64
	set DIR=%CD%\ia64
	goto delete
	
:x86
	set DIR=%CD%\x86
	goto delete

:delete
	amd64\JadexPro delete