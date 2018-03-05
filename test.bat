@REM perform a clean build and test

@REM --- with recompile ---
gradlew --parallel  --continue clean :jadex-applications-micro:test :jadex-integration-test:test test testReport -x :jadex-integration-performance-test:test -x javadoc -x lint -x generatereleaseJavadoc -x generatedebugJavadoc -x lintVitalRelease -x transformClassesWithDexForRelease -x testDebugUnitTest -x processSchemas