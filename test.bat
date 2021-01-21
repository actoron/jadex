@REM perform all tests

set jadex_timeout=90000
:test_loop
CMD /C gradlew -Pdist=publishdists cleanTest test -x javadoc --continue

@set builderror=%ERRORLEVEL%
@IF "%builderror%" NEQ "0" (
    echo Generating Test Report... & CMD /C gradlew testReport >NUL & START "" "build\testreport\index.html"
) ELSE (
    rem echo Running performance tests... & CMD /C gradlew performanceTest -Dorg.gradle.parallel=false
    goto test_loop
)

@pause
