#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"

###############
# K8S (KIND) DEFAULTS
###############
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../../k8s-playground/"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"


SERVICE_CONTEXT_PATH="/java-pure/"

SERVICE_BASE_PATH="localhost:${K8S_HTTP_PORT}/${SERVICE_CONTEXT_PATH}/"

curl -s "${SERVICE_BASE_PATH}/api/user" | jq


curl -s --header "Content-Type: application/json" \
  --request POST \
  --data '{"login":"xyz","password":"xyz"}' \
  "${SERVICE_BASE_PATH}/api/user" | jq

curl -s "${SERVICE_BASE_PATH}/api/user" | jq

curl -s --header "Content-Type: application/json" \
  --request POST \
  --data '{"login":"xyz","password":"xyz"}' \
  "${SERVICE_BASE_PATH}/api/user" | jq


xk6 build v0.41.0 --with github.com/szkiba/xk6-dashboard --with github.com/grafana/xk6-distributed-tracing --with github.com/gpiechnik2/xk6-httpagg --with github.com/dgzlopes/xk6-interpret --with github.com/dgzlopes/xk6-url