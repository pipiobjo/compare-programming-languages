#!/usr/bin/env bash

CURRENT_DIR=$(pwd)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
BUILD_SCRIPTS="${SCRIPT_DIR}/build-scripts"
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../k8s-playground/"
K8S_CLEANUP_REGISTRY="${K8S_PLAYGROUND_DIR}/kind/shell-based-setup/clearContainerRegistry.sh"

source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"
# load colors
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/define-colors.sh"


echo -e "${GREEN}cleanup docker images${NO_COLOR} "
docker image prune -a -f
docker system prune -f

echo -e "${GREEN}cleanup k8s image registry${NO_COLOR} "

"${K8S_CLEANUP_REGISTRY}"