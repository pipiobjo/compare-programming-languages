#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
REPORT_FOLDER="$SCRIPT_DIR/../load-test-report"
mkdir -p "$REPORT_FOLDER"

## JAVA PURE SERVICE
REST_CONTEXT_PATH="/python-flask"
REPORT_FILE="${REPORT_FOLDER}/python-flask.html"

echo "starting loadtests for ${REST_CONTEXT_PATH} writing report to ${REPORT_FILE}"
k6 run \
  -e MY_CONTEXT_PATH="${REST_CONTEXT_PATH}" \
  -e REPORT_FILE="${REPORT_FILE}" \
  ${SCRIPT_DIR}../k6-load-testing/script.js

