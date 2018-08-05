#!/bin/bash
set +x 
#获取基础目录
bashpath=$(cd "$(dirname "$0")"; pwd)

export JAVA_HOME=$bashpath/middleware/JDK/jdk1.8.0_121-linux;
export PATH=$JAVA_HOME/bin:$PATH

#初始化配置文件
chmod -R 755 $JAVA_HOME/bin/*;

#java -jar $bashpath/dobatch.jar


export classpath=%classpath%;.\lib\chardet-1.0.jar;.\lib\gson-2.7.jar;.\lib\log4j-1.2.14.jar;.\lib\iuap_i18n-1.0.jar;

export JAVA_OPTIONS=-Xms512m -Xmx512m

export workbench=%bashpath%\workbench\

%JAVA_HOME%/bin/java %JAVA_OPTIONS% com.yonyou.i18n.main.I18nMain -DparseProjectPath=%workbench%
