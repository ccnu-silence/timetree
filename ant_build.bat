@ECHO OFF

set WORK_DIR=%~dp0

:: ==============================================================
:: 以下为需要修改的配置参数
:: 1. APP_NAME可修改为您需要生成的App名称
set APP_NAME=timetree
:: 2. REWRITER_PATH修改为maa_sdk_android_rewriter.jar的绝对路径，默认是当前脚本所在的目录
::set REWRITER_PATH=%WORK_DIR%/maa_sdk_android_rewriter.jar
:: ==============================================================

call android update project -p %WORK_DIR% -n %APP_NAME%
call set ANT_OPTS="-javaagent:D:\MAA_SDK_Android_Ant_4.2.0.301.1\maa_sdk_android_rewriter.jar"
call ant clean release -f %WORK_DIR%

pause
