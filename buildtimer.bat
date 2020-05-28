@echo "build;zips;javadocs;artifacts" >buildtimer.csv
@set TASKS[0]=publishdists build testClasses -x test -x javadoc
@set TASKS[1]=publishdists distZip
@set TASKS[2]=addonjavadoc javadocZip
@set TASKS[3]=publishdists signMavenJavaPublication
@FOR /L %%i IN (1, 1, 10) DO @(
	@FOR /F "tokens=2 delims==" %%T IN ('set TASKS[') DO (
		gradlew clean
		powershell -command "(Measure-Command {Invoke-Expression '.\gradlew.bat --max-workers 16 -Production -Pdist=%%T' | Out-Default}).TotalSeconds | Add-Content -NoNewLine buildtimer.csv"
		@echo | set /p dummy=";" >>buildtimer.csv
	)
	@echo .>>buildtimer.csv
)
