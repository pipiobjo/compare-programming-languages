#!/bin/bash

CURRENT_DIR=$(pwd)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"

${SCRIPT_DIR}/cleanup.sh

BUILD_SCRIPTS="${SCRIPT_DIR}/build-scripts"
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../k8s-playground/"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/define-colors.sh"

cd ${CURRENT_DIR}

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
BUILD_SCRIPTS="${SCRIPT_DIR}/build-scripts"

echo -e "${GREEN}ensure we using $KIND_CLUSTER_NAME k8s cluster${NO_COLOR} "
kubectl config use-context "kind-${KIND_CLUSTER_NAME}"
echo -e "${GREEN}setup infra${NO_COLOR} "


${BUILD_SCRIPTS}/../initInfra.sh

echo -e "${GREEN}build all service${NO_COLOR} "

BUILD_SCRIPTS="${SCRIPT_DIR}/build-scripts"

${BUILD_SCRIPTS}/java-19-pure-apko-loom-build-deploy.sh
${BUILD_SCRIPTS}/java-pure-apko-build-deploy.sh
${BUILD_SCRIPTS}/java-pure-apko-graalvm-build-deploy.sh
${BUILD_SCRIPTS}/java-pure-build-deploy.sh
${BUILD_SCRIPTS}/rust-actix-build-deploy.sh
${BUILD_SCRIPTS}/rust-actix-optimized-build-deploy.sh
${BUILD_SCRIPTS}/java-springboot-build-deploy.sh
${BUILD_SCRIPTS}/rust-rocket-build-deploy.sh
${BUILD_SCRIPTS}/python-flask-build-deploy.sh
${BUILD_SCRIPTS}/golang-chi-build-deploy.sh


echo -e "${GREEN}execute all load tests${NO_COLOR} "

${BUILD_SCRIPTS}/java-19-pure-apko-loom-load-tests.sh
${BUILD_SCRIPTS}/java-pure-apko-load-tests.sh
${BUILD_SCRIPTS}/java-pure-apko-graalvm-load-tests.sh
${BUILD_SCRIPTS}/java-pure-load-tests.sh
${BUILD_SCRIPTS}/java-springboot-load-tests.sh
${BUILD_SCRIPTS}/rust-actix-load-tests.sh
${BUILD_SCRIPTS}/rust-actix-optimized-load-tests.sh
${BUILD_SCRIPTS}/rust-rocket-load-tests.sh
${BUILD_SCRIPTS}/python-flask-load-tests.sh
${BUILD_SCRIPTS}/golang-chi-load-tests.sh
