@REM perform a clean distribution build
CMD /C gradlew --profile -Pdist=publishdists clean test distZips -x javadoc -x lint -x lintVitalRelease
@rem  --parallel --continue
@pause