@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use a solution taken from http://stackoverflow.com/a/9826988
set CMD_LINE_ARGS=%*

set MAVEN_PROJECTBASEDIR=%~dp0
if not "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if exist %WRAPPER_JAR% goto run

if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"

@REM Downloading maven-wrapper.jar
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"

:run
set JVM_CONFIG_FILE="%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config"
if exist %JVM_CONFIG_FILE% (
    for /f "usebackq delims=" %%a in (%JVM_CONFIG_FILE%) do set JVM_CONFIG_FILE_CONTENT=%%a
)

set MAVEN_OPTS=%MAVEN_OPTS% %JVM_CONFIG_FILE_CONTENT%

java "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %CMD_LINE_ARGS%

if ERRORLEVEL 1 set ERROR_CODE=1

cmd /C exit /B %ERROR_CODE%
