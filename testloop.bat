@rem perform all tests repeatedly until failure
@rem first arg (if any) is used for build output directory name for allowing multiple testloops in parallel

@if "%1"=="" (
	set BUILD_DIR=build
) else (
	set BUILD_DIR=%1
)

call gradlew clean -PbuildDir=%BUILD_DIR%
@rem set jadex_timeout=60000
:test_loop
CMD /C gradlew -Pdist=publishdists test -x javadoc --continue -PbuildDir=%BUILD_DIR%

@set builderror=%ERRORLEVEL%
@if "%builderror%" NEQ "0" (
    @rem on error create a report and copy it to a new folder
    echo Generating Test Report... & call gradlew testReport -PbuildDir=%BUILD_DIR% >NUL
    set /a num=1
    :dirloop
    if exist "reports\report-%num%" (
        set /a num=%num%+1
        goto dirloop
    )
    mkdir "reports\report-%num%"
    robocopy "%BUILD_DIR%\testreport" "reports\report-%num%" /e
    start "" "reports\report-%num%\index.html"
)
goto test_loop
