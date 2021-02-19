@rem perform all tests repeatedly until failure
@rem first arg (if any) is used for build output directory name for allowing multiple testloops in parallel

@if "%1"=="" (
	set BUILD_DIR=build
) else (
	set BUILD_DIR=%1
)

call gradlew clean -PbuildDir=%BUILD_DIR%
set jadex_timeout=90000
:test_loop
CMD /C gradlew -Pdist=publishdists test -x javadoc --continue -PbuildDir=%BUILD_DIR%

@set builderror=%ERRORLEVEL%
@if "%builderror%" NEQ "0" (
    echo Generating Test Report... & call gradlew testReport -PbuildDir=%BUILD_DIR% >NUL & start "" %BUILD_DIR%"\testreport\index.html"
) else (
    goto test_loop
)

@pause
