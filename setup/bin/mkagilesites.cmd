@echo off
if "%~1"=="" goto err
set JAVA_HOME=%~1
set BASE=%~dp0
echo @set PATH="%JAVA_HOME%\bin";"%BASE%\bin";"%BASE%\home\bin";%%PATH%% >"%BASE%\sbt.cmd"  
echo @"%JAVA_HOME%\bin\java" -Xms128m -Xmx512m -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar "%BASE%\bin\sbt-launch.jar" %%1 %%2 %%3 %%4 %%5 %%6 %%7 %%8 %%9>>"%BASE%\agilesites.cmd"
goto end
:err
echo usage: mksbt JAVA_HOME
goto end
:end
