#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
CURRENT_USER_DIR=$(pwd)
SERVICE_FOLDER_NAME="java-springboot-native"
SERVICE_FOLDER="${SCRIPT_DIR}/../${SERVICE_FOLDER_NAME}"
REPORT_NAME="${SERVICE_FOLDER_NAME}"
DOCKER_IMAGE_NAME="${SERVICE_FOLDER_NAME}"


###############
# K8S (KIND) DEFAULTS
###############
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../../k8s-playground/"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"
DOCKER_REGISTRY="$DOCKER_REGISTRY_HOST:$DOCKER_REGISTRY_PORT"

# load colors
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/define-colors.sh"


###
### defaults
###

VERSION=$(date +%Y%m%d%H%M%S)
MY_IMAGE="${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}"
MY_IMAGE_VERSION_TAG="${DOCKER_REGISTRY}/${SERVICE_FOLDER_NAME}:${VERSION}"



cd "${SERVICE_FOLDER}"
START=$(date +%s)

./gradlew bootBuildImage --imageName="${MY_IMAGE_VERSION_TAG}"
docker push -a "${MY_IMAGE}"
export GENERATED_IMAGE="${MY_IMAGE_VERSION_TAG}"

END=$(date +%s)
DIFF=$( $END - $START )
echo -e "${GREEN}Full docker build took $DIFF seconds ${MY_IMAGE_VERSION_TAG}${NO_COLOR} "


BUILD_REPORT_FOLDER="${SCRIPT_DIR}/../report/dist/reports/${REPORT_NAME}"
mkdir -p "${BUILD_REPORT_FOLDER}"
BUILD_REPORT_FIle="${BUILD_REPORT_FOLDER}/build-duration.json"

echo -e "${GREEN}Writing build report to ${BUILD_REPORT_FIle}${NO_COLOR} "
echo "{\"buildDuration\": $DIFF}" > "${BUILD_REPORT_FIle}"

cd "${CURRENT_USER_DIR}"




. ${SCRIPT_DIR}/generic-k8s-manifests-build.sh \
  --serviceFolder "${SERVICE_FOLDER}" \
  --dockerImageName "${GENERATED_IMAGE}" \
  --reportName ${SERVICE_FOLDER_NAME}

