@REM perform all tests repeatedly until failure

set jadex_timeout=90000
:test_loop
CMD /C gradlew -Pdist=publishdists test -x javadoc --continue -PbuildDir=build2

@set builderror=%ERRORLEVEL%
@IF "%builderror%" NEQ "0" (
    echo Generating Test Report... & CMD /C gradlew testReport -PbuildDir=build2 >NUL & START "" "build2\testreport\index.html"
) ELSE (
    goto test_loop
)

@pause
