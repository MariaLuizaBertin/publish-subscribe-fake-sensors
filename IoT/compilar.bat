@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

echo ==================================================
echo         COMPILANDO PROJETO IOT HOSPITALAR
echo ==================================================

:: 1. Define o javac: tenta do PATH ou usa o JDK embutido do Eclipse
where javac >nul 2>nul
if %errorlevel% equ 0 (
    set JAVAC_CMD=javac
) else (
    set JAVAC_CMD="C:\Users\Witch\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.12.v20260826-1216\jre\bin\javac.exe"
)

:: 2. Garante que a pasta bin existe
if not exist "bin" mkdir bin

:: 3. Coleta todos os arquivos .java de src
dir /s /b src\*.java > sources.txt

:: 4. Compila os fontes com as bibliotecas da pasta lib
%JAVAC_CMD% -encoding UTF-8 -cp "lib\*" -d bin @sources.txt

if %errorlevel% equ 0 (
    del sources.txt
    echo.
    echo [SUCESSO] Projeto compilado com sucesso na pasta 'bin'!
    echo ==================================================
) else (
    del sources.txt
    echo.
    echo [ERRO] Falha durante a compilacao. Verifique as mensagens acima.
    echo ==================================================
    pause
    exit /b 1
)

