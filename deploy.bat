@echo off

set "root=%~dp0"
set "bin=%root%\bin"
set "lib=%root%\lib"
set "web=%root%\web"
set "temp=%root%\temp"
set "src=%root%\src"
set "target_dir=D:\ITU\L3\S5\Nouveau dossier\wildfly-10.0.0.Final\standalone\deployments"
set "war_name=ticketing"

:: copy all java files to temp directory
for /r "%src%" %%f in (*.java) do (
    xcopy "%%f" "%temp%"
)

:: move to temp to compile all java file
cd "%temp%"
javac -d "%bin%" -cp "%lib%\*" *.java -parameters

pause

:: move back to root
cd %root%

:: clean WEB-INF\lib and WEB-INF\classes folders
del /s /q "%web%\WEB-INF\lib\*"
for /d %%p in ("%web%\WEB-INF\lib\*") do rmdir /s /q "%%p"
del /s /q "%web%\WEB-INF\classes\*"
for /d %%p in ("%web%\WEB-INF\classes\*") do rmdir /s /q "%%p"

:: copy lib and classes to web-inf
xcopy /s /e /i "%lib%\*" "%web%\WEB-INF\lib\" /Y
xcopy /s /e /i "%bin%\*" "%web%\WEB-INF\classes\" /Y

:: archive web folder into war file
jar -cvf "%war_name%.war" -C "%web%" .

:: deploy war to server 
copy "%war_name%.war" "%target_dir%" /Y

:: remove war and temp
del "%war_name%.war"
rmdir /s /q "%temp%"

:: remove bin content
del /s /q "%bin%\*"
for /d %%p in ("%bin%\*") do rmdir /s /q "%%p"

echo Deployment complete.
pause