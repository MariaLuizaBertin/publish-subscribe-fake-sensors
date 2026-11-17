@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

if "%JAVA_HOME%"=="" (
    for /d %%E in ("%USERPROFILE%\Downloads\eclipse*") do (
        for /d %%D in ("%%E\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full*") do (
            if exist "%%D\jre\bin\javac.exe" set "JAVA_HOME=%%D\jre"
        )
        for /d %%D in ("%%E\plugins\org.eclipse.justj.openjdk.hotspot.jre.full*") do (
            if exist "%%D\jre\bin\javac.exe" set "JAVA_HOME=%%D\jre"
        )
    )
)

if exist "mvnw.cmd" (
    set "MVN_CMD=.\mvnw.cmd"
) else (
    where mvn >nul 2>nul
    if !errorlevel! equ 0 (
        set "MVN_CMD=mvn"
    ) else (
        echo [ERRO] Maven ^(mvn^) nao encontrado no PATH do sistema.
        echo Instale o Apache Maven ou adicione-o ao PATH e tente novamente.
        pause
        exit /b 1
    )
)

:menu
cls
echo ==================================================
echo   SISTEMA HOSPITALAR IOT - SPRING BOOT E MAVEN
echo ==================================================
echo  Paineis Web: http://localhost:8080/index.html
echo               http://localhost:8080/medico.html
echo               http://localhost:8080/movel.html
echo --------------------------------------------------
echo  [1] Iniciar Sistema (Spring Boot + Web)
echo  [2] Abrir Painel Web no navegador
echo  [3] Recompilar projeto (clean compile)
echo  [0] Sair
echo ==================================================
set /p opcao="Escolha uma opcao: "

if "%opcao%"=="1" (
    echo.
    echo Iniciando aplicacao Spring Boot ^(core.Main^)...
    echo Acesse os paineis em http://localhost:8080
    echo.
    call %MVN_CMD% spring-boot:run
    pause
    goto menu
)
if "%opcao%"=="2" (
    start "" "http://localhost:8080/index.html"
    goto menu
)
if "%opcao%"=="3" (
    echo.
    echo Recompilando projeto...
    call %MVN_CMD% clean compile
    pause
    goto menu
)
if "%opcao%"=="0" exit /b 0

echo Opcao invalida.
timeout /t 2 >nul
goto menu
