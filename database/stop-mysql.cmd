@echo off
REM ------------------------------------------------------------------
REM Gracefully stops the project-local MySQL 8 server.
REM ------------------------------------------------------------------
setlocal
set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0"

"%MYSQL_HOME%\bin\mysqladmin.exe" --defaults-file="%~dp0..\.mysql\my.ini" -u root shutdown
if errorlevel 1 (
    echo [WARN] Could not shut down MySQL (it may already be stopped).
) else (
    echo MySQL stopped.
)
