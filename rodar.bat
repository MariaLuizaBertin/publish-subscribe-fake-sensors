@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

if "%JAVA_HOME%"=="" (
    for /d %%D in ("%USERPROFILE%\Downloads\eclipse*\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full*") do (
        if exist "%%D\jre\bin\javac.exe" set "JAVA_HOME=%%D\jre"
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
echo  [1] Iniciar Servidor Spring Boot (core.Main)
echo  [2] Subscriber: Painel Enfermagem (recebe tudo: #)
echo  [3] Subscriber: Painel Medico (paciente especifico)
echo  [4] Subscriber: App Emergencia (somente critico)
echo  [5] Recompilar projeto (mvn clean compile)
echo  [0] Sair
echo ==================================================
set /p opcao="Escolha uma opcao: "

if "%opcao%"=="1" (
    echo.
    echo Iniciando aplicacao Spring Boot ^(core.Main^)...
    call %MVN_CMD% spring-boot:run
    pause
    goto menu
)
if "%opcao%"=="2" (
    echo.
    echo Iniciando Painel Enfermagem via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="enfermagem"
    pause
    goto menu
)
if "%opcao%"=="3" (
    set /p pac="Digite o ID do paciente [padrao: paciente1]: "
    if "!pac!"=="" set pac=paciente1
    echo.
    echo Iniciando Painel Medico para !pac! via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="medico !pac!"
    pause
    goto menu
)
if "%opcao%"=="4" (
    set /p pac="Filtrar por paciente especifico? (deixe vazio para todos): "
    echo.
    if "!pac!"=="" (
        echo Iniciando App Emergencia para todos os pacientes via Maven...
        call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="emergencia"
    ) else (
        echo Iniciando App Emergencia para !pac! via Maven...
        call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="emergencia !pac!"
    )
    pause
    goto menu
)
if "%opcao%"=="5" (
    call compilar.bat
    pause
    goto menu
)
if "%opcao%"=="0" exit /b 0

echo Opcao invalida.
timeout /t 2 >nul
goto menu
