@REM perform a clean build and test
@set jadex_timeout=90000

CMD /C gradlew -Pdist=publishdists clean test -x javadoc

@set builderror=%ERRORLEVEL%
@echo Generating Test Report...
@CMD /C gradlew testReport >NUL
@IF /I "%builderror%" NEQ "0" (START "" "testreport\index.html")

@pause