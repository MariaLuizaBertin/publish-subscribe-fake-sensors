@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul

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

:menu
cls
echo ==================================================
echo      SISTEMA HOSPITALAR IOT - EXECUCAO (MAVEN)
echo ==================================================
echo  [1] Publicador (Publisher - monitor paciente1)
echo  [2] Publicador com ID personalizado
echo  [3] Subscriber: Painel Enfermagem (recebe tudo: #)
echo  [4] Subscriber: Painel Medico (paciente1.#)
echo  [5] Subscriber: App Emergencia (somente critico)
echo  [6] Recompilar projeto (mvn compile)
echo  [0] Sair
echo ==================================================
set /p opcao="Escolha uma opcao: "

if "%opcao%"=="1" (
    echo.
    echo Iniciando Publisher para paciente1 via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="main.Main" -Dexec.args="paciente1"
    pause
    goto menu
)
if "%opcao%"=="2" (
    set /p pac="Digite o ID do paciente (ex: paciente2): "
    echo.
    echo Iniciando Publisher para !pac! via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="main.Main" -Dexec.args="!pac!"
    pause
    goto menu
)
if "%opcao%"=="3" (
    echo.
    echo Iniciando Painel Enfermagem via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="enfermagem"
    pause
    goto menu
)
if "%opcao%"=="4" (
    set /p pac="Digite o ID do paciente [padrao: paciente1]: "
    if "!pac!"=="" set pac=paciente1
    echo.
    echo Iniciando Painel Medico para !pac! via Maven...
    call %MVN_CMD% -q compile exec:java -Dexec.mainClass="subscriber.Subscriber" -Dexec.args="medico !pac!"
    pause
    goto menu
)
if "%opcao%"=="5" (
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
if "%opcao%"=="6" (
    call compilar.bat
    pause
    goto menu
)
if "%opcao%"=="0" exit /b 0

echo Opcao invalida.
timeout /t 2 >nul
goto menu
