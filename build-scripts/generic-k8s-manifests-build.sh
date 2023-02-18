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


if  [ -z "$SERVICE_FOLDER" ] || [ ! -d "$SERVICE_FOLDER" ]; then
 echo -e "${RED}Parameter --serviceFolder missing or folder does not exists ${SERVICE_FOLDER}${NO_COLOR} "
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



############################################################################################################
### build k8s manifests
############################################################################################################

echo -e "\nensure we using $KIND_CLUSTER_NAME k8s cluster"
kubectl config use-context "kind-${KIND_CLUSTER_NAME}"

TEMP_FILE=$(mktemp)
echo "TEMP_FILE=$TEMP_FILE"
trap 'rm -- "$TEMP_FILE"' EXIT
#cat <<EOF | kubectl apply -f -
#read -r -d '' MANIFESTS << 'EOF'

cat << EOF > $TEMP_FILE
apiVersion: apps/v1
kind: Deployment
metadata:
  name: "${REPORT_NAME}"
  labels:
    app: "${REPORT_NAME}"
spec:
  replicas: 1
  selector:
    matchLabels:
      app: "${REPORT_NAME}"
  template:
    metadata:
      annotations:
        prometheus.io/scrape: "true"
      labels:
        app: "${REPORT_NAME}"

    spec:
      containers:
        - name: ${REPORT_NAME}
          image: ${DOCKER_IMAGE_NAME}
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
            - containerPort: 8081
# disable probes k8s assumes everything is ready, needed for startup time measurement
#          startupProbe:
#            httpGet:
#              path: /ops/start
#              port: 8081
#            initialDelaySeconds: 1
#            periodSeconds: 3
#            failureThreshold: 3
#          readinessProbe:
#            httpGet:
#              path: /ops/ready
#              port: 8081
#            initialDelaySeconds: 2
#            periodSeconds: 15
#            failureThreshold: 3
#          livenessProbe:
#            httpGet:
#              path: /ops/live
#              port: 8081
#            initialDelaySeconds: 2
#            periodSeconds: 15
#            failureThreshold: 3
          resources:
            limits:
              cpu:    "500m"   # maximum amount of cpu in millicpu
              memory: "200Mi"  # maximum amount of ram for the container
            requests:
              cpu:    "20m"   # minimum amount of cpu in millicpu
              memory: "20Mi"  # minimum amount of ram
---
apiVersion: v1
kind: Service
metadata:
  name: "${REPORT_NAME}"
spec:
  selector:
    app: "${REPORT_NAME}"
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: "${REPORT_NAME}"
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /\$2
spec:
  ingressClassName: nginx
  rules:
    - http:
        paths:
          - path: "/${REPORT_NAME}(/|\$)(.*)"
            pathType: Prefix
            backend:
              service:
                name: "${REPORT_NAME}"
                port:
                  number: 80


EOF

cat $TEMP_FILE
kubectl delete -f $TEMP_FILE

sleep 3s

START=$(date +%s)
kubectl apply -f $TEMP_FILE

kubectl wait --for=condition=ready pod -l app=${REPORT_NAME} --timeout=60s
LOCAL_HTTP_PORT=9180
kubectl port-forward deployment/${REPORT_NAME} $LOCAL_HTTP_PORT:8080 &
KUBECTL_PID=$!
echo "KUBECTL_PID: ${KUBECTL_PID}"

sleep 1

# kill the port-forward regardless of how this script exits
trap '{
    echo killing kubectl port-forward: $KUBECTL_PID
    kill $KUBECTL_PID
}' EXIT


BASE_URL="http://localhost:${LOCAL_HTTP_PORT}"

ENDPOINT_URL="${BASE_URL}/api/user/"
# curl endpioint
echo -e "\n${GREEN}curl ${ENDPOINT_URL}${NO_COLOR}"
curl --head -X GET --retry 500 --retry-all-errors --retry-delay 1 "${ENDPOINT_URL}"

END=$(date +%s)
DIFF=$(( $END - $START ))

echo -e "${BLUE}Startup Time: ${DIFF}${NO_COLOR} "

BUILD_REPORT_FIle="${SCRIPT_DIR}/../report/dist/reports/${REPORT_NAME}/startup-time.json"
echo -e "${GREEN}Writing startupTime to reportfile: ${BUILD_REPORT_FIle}${NO_COLOR} "
echo "{\"startup_time_in_seconds\": $DIFF}" > $BUILD_REPORT_FIle

















