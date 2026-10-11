@echo off
REM ------------------------------------------------------------------
REM Creates the application database + dedicated dev user, then seeds
REM sample data (seeding is added in a later stage and is safe to run
REM repeatedly because all statements are idempotent).
REM Run database\start-mysql.cmd first.
REM ------------------------------------------------------------------
setlocal
set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0"
set "MYINI=%~dp0..\.mysql\my.ini"

"%MYSQL_HOME%\bin\mysql.exe" --defaults-file="%MYINI%" -u root < "%~dp0sql\01-create-database.sql"
if errorlevel 1 ( echo [ERROR] Failed to create database. & exit /b 1 )

if exist "%~dp0sql\02-seed-sample-data.sql" (
    "%MYSQL_HOME%\bin\mysql.exe" --defaults-file="%MYINI%" -u root academic_analytics < "%~dp0sql\02-seed-sample-data.sql"
    if errorlevel 1 ( echo [ERROR] Failed to seed sample data. & exit /b 1 )
)

echo Database 'academic_analytics' and dev user 'academic_user' are ready.
