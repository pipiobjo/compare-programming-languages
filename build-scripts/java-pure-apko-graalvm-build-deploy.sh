#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"

SERVICE_FOLDER_NAME="java-pure-apko-graalvm"
SERVICE_FOLDER="${SCRIPT_DIR}/../${SERVICE_FOLDER_NAME}"
SERVICE_DOCKER_BUILD_CONTEXT="${SERVICE_FOLDER}/"
SERVICE_DOCKER_FILE="${SERVICE_FOLDER}/docker/openjdk/DockerfileUsingWolfiBase"

# building apko / wolfi base image
docker run --rm -v ${SERVICE_FOLDER}/docker/java-base-apko:/work -w /work cgr.dev/chainguard/apko build --debug "/work/image.yaml" "my-java-base:latest" "my-java-base.tar"
docker load <  "${SERVICE_FOLDER}/docker/java-base-apko/my-java-base.tar"

. ${SCRIPT_DIR}/generic-docker-build.sh \
  --docker-context-folder ${SERVICE_DOCKER_BUILD_CONTEXT} \
  --dockerfile ${SERVICE_DOCKER_FILE} \
  --dockerImageName ${SERVICE_FOLDER_NAME} \
  --reportName ${SERVICE_FOLDER_NAME}

. ${SCRIPT_DIR}/generic-k8s-kustomize-build.sh \
  --serviceFolder "${SERVICE_FOLDER}" \
  --dockerImageName "${GENERATED_IMAGE}"

