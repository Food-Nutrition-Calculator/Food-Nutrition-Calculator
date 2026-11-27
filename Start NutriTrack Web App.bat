@echo off
setlocal
cd /d "%~dp0"

start "NutriTrack Web Server" cmd /k java -cp "bin;lib\mysql-connector-j-26.10.0.jar" nutritrack.NutriTrackServer
timeout /t 3 /nobreak >nul
start "" "http://localhost:8081/"
