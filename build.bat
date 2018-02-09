@REM perform a clean distribution build
gradlew --profile -Pdist=publishdists clean test distZips -x javadoc
@rem  --parallel --continue