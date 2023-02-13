#!/bin/bash
#set -e
#set -x
###
### parse parameters
###
MICROSERVICE_NAME=
while :; do
    case $1 in
        --serviceName)       # Takes an option argument; ensure it has been specified.
                MICROSERVICE_NAME=$2
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

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"
K8S_PLAYGROUND_DIR="${SCRIPT_DIR}/../../../k8s-playground/"
# load colors
source "$K8S_PLAYGROUND_DIR/kind/shell-based-setup/k8s/scripts/define-colors.sh"

REPORT_FOLDER="$SCRIPT_DIR/../report/dist/reports/${MICROSERVICE_NAME}"
mkdir -p "$REPORT_FOLDER"

## JAVA PURE SERVICE
REST_CONTEXT_PATH="/${MICROSERVICE_NAME}"
REPORT_FILE="${REPORT_FOLDER}/loadtest-results.html"
REPORT_FILE_JSON="${REPORT_FOLDER}/loadtest-results.json"
DOCKER_IMAGE_SIZE_JSON="${REPORT_FOLDER}/container-image-size.json"
CPU_PERF_DATA_JSON="${REPORT_FOLDER}/perf-cpu.json"
MEM_PERF_DATA_JSON="${REPORT_FOLDER}/perf-mem.json"


echo "starting loadtests for ${REST_CONTEXT_PATH} writing report to ${REPORT_FILE}"
k6 run \
  -e MY_CONTEXT_PATH="${REST_CONTEXT_PATH}" \
  -e REPORT_FILE="${REPORT_FILE}" \
  -e REPORT_FILE_JSON="${REPORT_FILE_JSON}" \
  ${SCRIPT_DIR}../k6-load-testing/script.js


# get image size
DOCKER_IMAGES=$(kubectl get pods -l="app=${MICROSERVICE_NAME}" -o jsonpath="{..image}" | tr ' ' "\n" | sort -u | uniq)
IFS=', ' read -r -a DOCKER_IMAGE_ARRAY <<< "$DOCKER_IMAGES"

DOCKER_IMAGE="${DOCKER_IMAGE_ARRAY[0]}"
echo -e "${GREEN}Referenced image: ${DOCKER_IMAGE}${NO_COLOR} "

DOCKER_IMAGE_SIZE=$(docker image inspect "${DOCKER_IMAGE}" --format='{{.Size}}')
echo "DOCKER_IMAGE_SIZE=${DOCKER_IMAGE_SIZE}"

echo "{\"image-size\": ${DOCKER_IMAGE_SIZE}}" > "${DOCKER_IMAGE_SIZE_JSON}"


## get metric values from prometheus
echo -e "${GREEN}Getting Prometheus Data${NO_COLOR} "
kubectl port-forward svc/prometheus-server 9090:80 &
KUBECTL_PID=$!
echo "KUBECTL_PID: ${KUBECTL_PID}"

sleep 1

# kill the port-forward regardless of how this script exits
trap '{
    echo killing kubectl port-forward: $KUBECTL_PID
    kill $KUBECTL_PID
}' EXIT

curl "http://localhost:9090/api/v1/status/runtimeinfo" | jq
echo -e "${GREEN}Curl cpu data: ${MICROSERVICE_NAME}${NO_COLOR} "

# CPU DATA
#curl -s -g 'http://localhost:9090/api/v1/query?query=container_cpu_user_seconds_total{container="java-pure"}[5m]' | jq -r '.data.result[0].values'
CPU_QUERY_URL="http://localhost:9090/api/v1/query?query=container_cpu_user_seconds_total{container=\"${MICROSERVICE_NAME}\"}[10m]"
echo "CPU_QUERY_URL=${CPU_QUERY_URL}"
CPU_DATA=$(curl -s -g "${CPU_QUERY_URL}")
echo "$CPU_DATA" > ${CPU_PERF_DATA_JSON}

# MEM DATA
MEM_QUERY_URL="http://localhost:9090/api/v1/query?query=container_memory_rss{container=\"${MICROSERVICE_NAME}\"}[10m]"
#curl -s -g 'http://localhost:9090/api/v1/query?query=container_memory_rss{container="${MICROSERVICE_NAME}"}[5m]' | jq -r '.data.result[0].values'
echo "MEM_QUERY_URL=${MEM_QUERY_URL}"
MEM_DATA=$(curl -s -g "${MEM_QUERY_URL}")
echo "$MEM_DATA" > ${MEM_PERF_DATA_JSON}
