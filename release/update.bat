@echo off
:: �л�����ǰ�ļ�Ŀ¼
cd /d %~dp0
echo �ȴ�����ر�
timeout 3

:: �������pid���ҽ�����Ȼ���ڣ���ôǿ����ֹ�ý���
set pid=%2
if "%pid%"=="" goto :copy
echo ����pid%pid%
tasklist|findstr /i "%pid% " || goto :copy
echo ǿ����ֹ����
taskkill /F /PID %pid%

:copy
:: �����ļ�(����ʾֱ�Ӹ���)
copy /Y "update\INeedBiliAV.update.jar" "ILikeAcFun.jar"

if "%1"=="1" (echo ���º����� &goto :runApp) else (echo ������ &goto :end)

:runApp
:: ���г���
start javaw -Dfile.encoding=utf-8 -jar ILikeAcFun.jar

:end
::pause
exit