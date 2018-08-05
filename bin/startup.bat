setlocal

cd /d %~dp0
set bashpath=%cd%

set JAVA_HOME=%bashpath%\jre\jre1.8.0_151-win

set PATH=%JAVA_HOME%\bin;%PATH%

set classpath=%classpath%;.\lib\chardet-1.0.jar;.\lib\gson-2.7.jar;.\lib\log4j-1.2.14.jar;.\lib\iuap_i18n-1.0.jar;

set JAVA_OPTIONS=-Xms512m -Xmx512m

set workbench=%bashpath%\workbench\

%JAVA_HOME%/bin/java %JAVA_OPTIONS% com.yonyou.i18n.main.I18nMain -DparseProjectPath=%workbench%

#java -jar %bashpath%\iuap_i18n-1.0.jar

:end

endlocal 