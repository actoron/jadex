@REM perform a clean compile
@REM gradlew -Pdist=publishdists cleanTest test -x javadoc -x lint -x generatereleaseJavadoc -x generatedebugJavadoc -x lintVitalRelease -x transformClassesWithDexForRelease -x testDebugUnitTest -x jar
gradlew clean compileJava compileReleaseJavaWithJavac