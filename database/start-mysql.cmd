@echo off
REM ------------------------------------------------------------------
REM Starts the project-local MySQL 8 server in its own window.
REM Run database\init-local-mysql.ps1 once before using this.
REM Close the window (or run stop-mysql.cmd) to stop the server.
REM ------------------------------------------------------------------
setlocal
set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0"
set "MYINI=%~dp0..\.mysql\my.ini"

if not exist "%MYINI%" (
    echo [ERROR] %MYINI% not found. Run database\init-local-mysql.ps1 first.
    exit /b 1
)

echo Starting project-local MySQL on port 3306 ...
start "Academic Platform - MySQL" "%MYSQL_HOME%\bin\mysqld.exe" --defaults-file="%MYINI%" --console
