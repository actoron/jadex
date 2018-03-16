@REM perform a clean build and test
CMD /C gradlew --parallel  --continue clean :jadex-applications-micro:test :jadex-integration-test:test test testReport -x :jadex-integration-performance-test:test -x javadoc -x lint -x generatereleaseJavadoc -x lintVitalRelease -x transformClassesWithDexForRelease -x processSchemas
@set builderror=%ERRORLEVEL%
@echo Generating Test Report...
@CMD /C gradlew testReport >NUL
@IF /I "%builderror%" NEQ "0" (START "" "testreport\index.html")