@REM perform a clean build and test

@REM --- with recompile ---
CMD /C gradlew --parallel  --continue clean :jadex-applications-micro:test :jadex-integration-test:test test testReport -x :jadex-integration-performance-test:test -x javadoc -x lint -x generatereleaseJavadoc -x lintVitalRelease -x transformClassesWithDexForRelease -x processSchemas

echo %ERROR_LEVEL%

gradlew testReport

IF /I "%ERRORLEVEL%" NEQ "0" (
    START "" "testreport\index.html"
)