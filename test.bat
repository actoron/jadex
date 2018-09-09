@REM perform a clean build and test
@rem set jadex_timeout=90000

CMD /C gradlew -Pdist=publishdists :applications:micro:test test -x javadoc -x processSchemas

@set builderror=%ERRORLEVEL%
@IF /I "%builderror%" NEQ "0" (@echo Generating Test Report... & @CMD /C gradlew testReport >NUL & START "" "testreport\index.html")

@pause