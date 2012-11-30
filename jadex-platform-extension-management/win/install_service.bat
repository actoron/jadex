amd64\JadexPro install --Install %CD%\amd64\JadexPro.exe ^
  --Description "Jadex Production Edition" ^
  --Jvm auto --Startup auto ^
  --LogPath %CD% --StdOutput %CD%\out.txt --StdError %CD%\err.txt ^
  --Classpath %CD%\..\lib\jadex-platform-standalone-launch-2.3-SNAPSHOT.jar ^
  --StartMode jvm --StartClass jadex.platform.ServiceStarter --StartMethod start ^
  --StopMode jvm --StopClass jadex.platform.ServiceStarter --StopMethod stop ^
  ++StartParams -gui;false ^
  ++StartParams -logging;true

amd64\JadexPro start