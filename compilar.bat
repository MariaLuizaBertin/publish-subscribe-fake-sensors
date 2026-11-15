@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

echo ==================================================
echo      COMPILANDO PROJETO IOT HOSPITALAR (MAVEN)
echo ==================================================

if exist "mvnw.cmd" (
    set MVN_CMD=mvnw.cmd
) else (
    where mvn >nul 2>nul
    if !errorlevel! equ 0 (
        set MVN_CMD=mvn
    ) else (
        echo [ERRO] Maven (mvn) nao encontrado no PATH do sistema.
        echo Instale o Apache Maven ou adicione-o ao PATH e tente novamente.
        pause
        exit /b 1
    )
)

call %MVN_CMD% compile

if %errorlevel% equ 0 (
    echo.
    echo [SUCESSO] Projeto compilado via Maven em 'target\classes'!
    echo ==================================================
) else (
    echo.
    echo [ERRO] Falha durante a compilacao via Maven.
    echo ==================================================
    pause
    exit /b 1
)
