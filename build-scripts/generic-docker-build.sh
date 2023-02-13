#!/bin/bash

#!/bin/bash
#set -x
#set -o errexit # fail on error
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"


###############
# K8S (KIND) DEFAULTS
###############
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../../k8s-playground/"
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/k8s-env.sh"
DOCKER_REGISTRY="$DOCKER_REGISTRY_HOST:$DOCKER_REGISTRY_PORT"
#echo "DOCKER_REGISTRY=$DOCKER_REGISTRY"

# load colors
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/define-colors.sh"

###
### defaults
###

DOCKER_CONTEXT_FOLDER=
DOCKER_FILE=
DOCKER_IMAGE_NAME=
REPORT_NAME=

###
### parse parameters
###

while :; do
    case $1 in
        --docker-context-folder)       # Takes an option argument; ensure it has been specified.
            if [ "$2" ]; then
                DOCKER_CONTEXT_FOLDER=$2
            fi
            shift
            ;;
        --version)       # Takes an option argument; ensure it has been specified.
            if [ "$2" ]; then
                VERSION=$2
            fi
            shift
            ;;

        --dockerfile)       # Takes an option argument; ensure it has been specified.
                DOCKER_FILE=$2
            shift
            ;;
        --dockerImageName)       # Takes an option argument; ensure it has been specified.
                DOCKER_IMAGE_NAME=$2
            shift
            ;;
        --reportName)       # Takes an option argument; ensure it has been specified.
                REPORT_NAME=$2
            shift
            ;;
        --)              # End of all options.
            shift
            break
            ;;
        -?*)
            printf 'WARN: Unknown option (ignored): %s\n' "$1" >&2
            ;;
        *)               # Default case: No more options, so break out of the loop.
            break
    esac

    shift
done

############################################################################################################
### validating parameters
############################################################################################################


if  [ -z "$DOCKER_CONTEXT_FOLDER" ] || [ ! -d "$DOCKER_CONTEXT_FOLDER" ]; then
 echo -e "${RED}Parameter --docker-context-folder missing or folder does not exists ${DOCKER_CONTEXT_FOLDER}${NO_COLOR}"
 exit 1
fi


if  [ -z "$DOCKER_FILE" ] || [ ! -f "$DOCKER_FILE" ]; then
 echo -e "${RED}Parameter --dockerfile missing or file does not exists ${DOCKER_FILE}${NO_COLOR}"
 exit 1
fi


if  [ -z "$DOCKER_IMAGE_NAME" ]; then
 echo -e "${RED}Parameter --dockerImageName missing ${DOCKER_IMAGE_NAME}${NO_COLOR} "
 exit 1
fi

if [ -z "$REPORT_NAME" ]; then
 echo -e "${RED}Parameter --reportName missing ${REPORT_NAME}${NO_COLOR} "
 exit 1
fi


if  [ -z "$VERSION" ]; then
 VERSION=$(date +%Y%m%d%H%M%S)
fi


############################################################################################################
### build docker image
############################################################################################################

MY_IMAGE="${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}"
MY_IMAGE_LATEST_TAG="${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest"
MY_IMAGE_VERSION_TAG="${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${VERSION}"



function buildContainer() {
  DOCKER_BUILDKIT=1 docker build \
    -t "$MY_IMAGE_VERSION_TAG" \
    -t "$MY_IMAGE_LATEST_TAG" \
    -f "$DOCKER_FILE" \
    --cache-from="$MY_IMAGE_LATEST_TAG" \
    "$DOCKER_CONTEXT_FOLDER"

  DOCKER_BUILDKIT=1 docker push -a "${MY_IMAGE}" --quiet
}




START=$(date +%s)
buildContainer
END=$(date +%s)
DIFF=$(( $END - $START ))

echo -e "${GREEN}Full docker build took $DIFF seconds ${MY_IMAGE_VERSION_TAG}${NO_COLOR} "
BUILD_REPORT_FIle="${SCRIPT_DIR}/../report/dist/reports/${REPORT_NAME}/build-duration.json"

echo -e "${GREEN}Writing build report to ${BUILD_REPORT_FIle}${NO_COLOR} "
echo "{\"buildDuration\": $DIFF}" > $BUILD_REPORT_FIle

export GENERATED_IMAGE=${MY_IMAGE_VERSION_TAG}


