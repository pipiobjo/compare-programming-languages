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

SERVICE_FOLDER=
DOCKER_FILE=
DOCKER_IMAGE_NAME=

###
### parse parameters
###

while :; do
    case $1 in
        --serviceFolder)       # Takes an option argument; ensure it has been specified.
                SERVICE_FOLDER=$2
            shift
            ;;
        --dockerImageName)       # Takes an option argument; ensure it has been specified.
                DOCKER_IMAGE_NAME=$2
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


if  [ -z "$SERVICE_FOLDER" ] || [ ! -d "$SERVICE_FOLDER" ]; then
 echo -e "${RED}Parameter --serviceFolder missing or folder does not exists ${SERVICE_FOLDER}${NO_COLOR} "
 exit 1
fi


if  [ -z "$DOCKER_IMAGE_NAME" ]; then
 echo -e "${RED}Parameter --dockerImageName missing ${DOCKER_IMAGE_NAME}${NO_COLOR} "
 exit 1
fi



############################################################################################################
### build k8s manifests
############################################################################################################

SERVICE_K8S_FOLDER="${SERVICE_FOLDER}/k8s"
if  [ -z "$SERVICE_K8S_FOLDER" ] || [ ! -d "$SERVICE_K8S_FOLDER" ]; then
 echo -e "${RED}k8s folder does not exists ${SERVICE_K8S_FOLDER}${NO_COLOR}"
 exit 1
fi

KUSTOMIZE_TPL_FILE="${SERVICE_K8S_FOLDER}/kustomization.tpl.yaml"
if  [ -z "$KUSTOMIZE_TPL_FILE" ] || [ ! -f "$KUSTOMIZE_TPL_FILE" ]; then
 echo -e "${RED}kustomize template file does not exists ${KUSTOMIZE_TPL_FILE}${NO_COLOR}"
 exit 1
fi

KUSTOMIZE_FILE="${SERVICE_K8S_FOLDER}/kustomization.yaml"
if  [ -z "$KUSTOMIZE_FILE" ] || [ -f "$KUSTOMIZE_FILE" ]; then
 echo "kustomization file already exists, deleting it ${KUSTOMIZE_FILE}"
 rm ${KUSTOMIZE_FILE}
fi

MY_IMAGE=${DOCKER_IMAGE_NAME}
echo -e "${GREEN}k8s manifest using image: ${DOCKER_IMAGE_NAME}${NO_COLOR} "
sed "s|MY_IMAGE|$MY_IMAGE|g" "${KUSTOMIZE_TPL_FILE}" > "${KUSTOMIZE_FILE}"
cat "${KUSTOMIZE_FILE}"

#CURRENT_DIR=$(pwd)
#cd "${SERVICE_K8S_FOLDER}"


echo -e "\nensure we using $KIND_CLUSTER_NAME k8s cluster"
kubectl config use-context "kind-${KIND_CLUSTER_NAME}"
echo ${SERVICE_K8S_FOLDER}
kustomize build ${SERVICE_K8S_FOLDER} | kubectl apply -f -

#rm "${KUSTOMIZE_FILE}"




