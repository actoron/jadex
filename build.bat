@REM perform a clean distribution build
gradlew -Pdist=publishdists clean test distZips -x javadoc