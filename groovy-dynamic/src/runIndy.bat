REM
REM To make groovy use invokedynamic see http://groovy.codehaus.org/InvokeDynamic+support
REM
REM Change dirs as applicable for your setup
REM

set GROOVY_HOME=C:\dev\groovy\groovy-binary-2.1.1-indy\groovy-2.1.1
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_06
set PATH=%PATH%;%JAVA_HOME%\bin;%GROOVY_HOME%\bin;
SET JAVA_OPTS=-server -Xss15500k -Xmx1G

groovy -indy LoopTesterApp.groovy