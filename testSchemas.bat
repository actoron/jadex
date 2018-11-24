setlocal enabledelayedexpansion true
for /l %%x in (1, 1, 999) do (
	cmd /c gradlew -Pdist=publishdists clean processSchemas
	if !errorlevel! neq 0 pause
)
