@REM perform a clean distribution build
gradlew --profile -Pdist=publishdists clean test distZips -x javadoc -x lint -x generatereleaseJavadoc -x generatedebugJavadoc -x lintVitalRelease  -x testDebugUnitTest -x transformClassesWithDexForDebug
@rem  --parallel --continue