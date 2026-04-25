@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.17
set MAVEN_HOME=C:\Program Files\Apache Maven\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%
echo PATH: %PATH%

mvn spring-boot:run