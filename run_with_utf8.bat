@echo off
echo Starting Quiz Management System with UTF-8 encoding...

REM Set UTF-8 encoding for console
chcp 65001

REM Set JVM arguments for UTF-8
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -Duser.timezone=Asia/Ho_Chi_Minh

REM Run the application
mvn exec:java -Dexec.mainClass="com.quiz.Main" -Dexec.args="%JAVA_OPTS%"

pause
