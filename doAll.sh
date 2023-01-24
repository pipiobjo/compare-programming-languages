#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../k8s-playground/"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"


echo "ensure we using $KIND_CLUSTER_NAME k8s cluster"
kubectl config use-context "kind-${KIND_CLUSTER_NAME}"

echo "setup infra"

${SCRIPT_DIR}/initInfra.sh


echo "build all service"
BUILD_SCRIPTS="${SCRIPT_DIR}/build-scripts"

${BUILD_SCRIPTS}/java-pure-apko-build-deploy.sh
${BUILD_SCRIPTS}/java-pure-build-deploy.sh
${BUILD_SCRIPTS}/rust-actix-build-deploy.sh
${BUILD_SCRIPTS}/java-spring-gradle-build-deploy.sh

echo "execute all load tests"

${BUILD_SCRIPTS}/java-pure-apko-load-tests.sh
${BUILD_SCRIPTS}/java-pure-load-tests.sh
${BUILD_SCRIPTS}/rust-actix-load-tests.sh
${BUILD_SCRIPTS}/java-spring-gradle-load-tests.sh


