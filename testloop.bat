@REM perform all tests repeatedly until failure

set jadex_timeout=300000
:test_loop
CMD /C gradlew -Pdist=publishdists test -x javadoc --continue

@set builderror=%ERRORLEVEL%
@IF "%builderror%" NEQ "0" (
    echo Generating Test Report... & CMD /C gradlew testReport >NUL & START "" "build\testreport\index.html"
) ELSE (
    goto test_loop
)

@pause
