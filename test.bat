@REM perform all tests
@rem set jadex_timeout=90000

CMD /C gradlew -Pdist=publishdists cleanTest test -x javadoc --continue

@set builderror=%ERRORLEVEL%
@IF "%builderror%" NEQ "0" (
    echo Generating Test Report... & CMD /C gradlew testReport >NUL & START "" "testreport\index.html"
) ELSE (
    echo Running performance tests... & CMD /C gradlew performanceTest -Dorg.gradle.parallel=false
)

@pause