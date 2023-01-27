#!/bin/bash
#set -e
#set -x
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
MICROSERVICE_NAME="java-pure-apko"

"${SCRIPT_DIR}/generic-load-tests.sh" --serviceName ${MICROSERVICE_NAME}
