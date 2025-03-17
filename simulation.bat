@echo off
REM Skrypt do uruchomienia symulacji ruchu drogowego

REM Sprawdź, czy podano wymagane argumenty
if "%~1"=="" (
    echo Usage: %0 input.json output.json
    exit /b 1
)
if "%~2"=="" (
    echo Usage: %0 input.json output.json
    exit /b 1
)

set INPUT_FILE=%~1
set OUTPUT_FILE=%~2

REM Sprawdź, czy plik wejściowy istnieje
if not exist "%INPUT_FILE%" (
    echo Error: Input file '%INPUT_FILE%' not found
    exit /b 1
)

REM Uruchom aplikację Spring Boot z podanymi parametrami
echo Running traffic simulation...
echo Input file: %INPUT_FILE%
echo Output file: %OUTPUT_FILE%

REM Uruchom aplikację Java z podanymi argumentami
java -jar backend\target\traffic-light-simulation-0.0.1-SNAPSHOT.jar "%INPUT_FILE%" "%OUTPUT_FILE%"

REM Sprawdź kod wyjścia
if %ERRORLEVEL% EQU 0 (
    echo Simulation completed successfully
    echo Results saved to: %OUTPUT_FILE%
) else (
    echo Simulation failed with exit code: %ERRORLEVEL%
)

exit /b %ERRORLEVEL% 