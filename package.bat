:: cd ���ű�����Ŀ¼
cd /d %~dp0

:: ���������ļ���
xcopy src target\ /s /f /h

:: ɾ������Ҫ��java�ļ�
rmdir /s/q target\nicelee\test\

:: ��ȡjava�ļ��б�
cd target
dir /s /B *.java > ../sources.txt
cd ..

:: ��ȡ��������,��ѹlib��
cd libs
dir /s /B *.jar > ../libs.txt
cd ../target
setlocal enabledelayedexpansion
set classpath=.
for /f "tokens=*" %%i in (../libs.txt) do (
set classpath=!classpath!;%%i
jar xvf %%i
)
cd ..

:: ����java
javac -cp !classpath! -encoding UTF-8 @sources.txt

:: ɾ������.java�ļ�
cd target
del /a /f /s /q  "*.java"
cd ..

:: ���
jar cvfe ILikeAcFun.jar nicelee.ui.FrameMain -C ./target .

echo �������ɾ����ʱ�ļ�
pause

rmdir /s/q  target\
del sources.txt
del libs.txt
