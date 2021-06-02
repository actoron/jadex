@REM perform a clean distribution build
CMD /C gradlew -Pdist=publishdists clean test distZips -x javadoc
@REM -x lint -x lintVitalRelease
@rem  --parallel --continue
@pause