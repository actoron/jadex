@REM perform a clean build and test, but skip android projects/tasks

set ANDROID_HOME=
gradlew --parallel clean :jadex-applications-micro:test :jadex-integration-test:test test -x :jadex-integration-performance-test:test -x javadoc -x processSchemas