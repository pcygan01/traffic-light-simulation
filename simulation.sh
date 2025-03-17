#!/bin/bash

# Sprawdź, czy podano wymagane argumenty
if [ $# -ne 2 ]; then
    echo "Usage: $0 input.json output.json"
    exit 1
fi

INPUT_FILE=$1
OUTPUT_FILE=$2

# Sprawdź, czy plik wejściowy istnieje
if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: Input file '$INPUT_FILE' not found"
    exit 1
fi

# Uruchom aplikację Spring Boot z podanymi parametrami
echo "Running traffic simulation..."
echo "Input file: $INPUT_FILE"
echo "Output file: $OUTPUT_FILE"

# Uruchom aplikację Java z podanymi argumentami
java -jar backend/target/traffic-light-simulation-0.0.1-SNAPSHOT.jar "$INPUT_FILE" "$OUTPUT_FILE"

# Sprawdź kod wyjścia
EXIT_CODE=$?
if [ $EXIT_CODE -eq 0 ]; then
    echo "Simulation completed successfully"
    echo "Results saved to: $OUTPUT_FILE"
else
    echo "Simulation failed with exit code: $EXIT_CODE"
fi

exit $EXIT_CODE 