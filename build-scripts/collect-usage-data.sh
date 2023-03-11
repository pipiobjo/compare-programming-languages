#!/bin/bash

#set -x
#set -o errexit # fail on error
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"

###
### defaults
###

SERVICE_NAME=
REPORT_FOLDER=
POD_NAME=
MODE=

CPU_USAGE_FILE_NAME="perf-cpu.json"
MEMORY_USAGE_FILE_NAME="perf-mem.json"
LOOP_FILE_NAME="execute-loop.txt"

EXECUTE_LOOP=true

###
### parse parameters
###

while :; do
    case $1 in

        --serviceName)       # Takes an option argument; ensure it has been specified.
          SERVICE_NAME=$2
          echo "SERVICE_NAME=$SERVICE_NAME"
            shift
            ;;
        --reportFolder)       # Takes an option argument; ensure it has been specified.
            REPORT_FOLDER=$2
            echo "REPORT_FOLDER=$REPORT_FOLDER"
            shift
            ;;
        --mode)       # Takes an option argument; ensure it has been specified.
            MODE=$2
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

###
### parse parameters
###

if [ -z "$SERVICE_NAME" ]; then
 echo -e "${RED}Parameter --serviceName missing ${SERVICE_NAME}${NO_COLOR} "
 exit 1
fi

if [ -z "$REPORT_FOLDER" ]; then
 echo -e "${RED}Parameter --reportFolder missing ${REPORT_FOLDER}${NO_COLOR} "
 exit 1
fi

if [[ "${MODE}" == "start" ]]; then
  echo "start collecting data"
elif [[ "${MODE}" == "stop" ]]; then
  echo "stop collecting data"
else
  echo "invalid mode --MODE $MODE, allowed values are 'start' 'stop'"
  exit 1
fi

mkdir -p $REPORT_FOLDER

CPU_USAGE_FILE="$REPORT_FOLDER/$CPU_USAGE_FILE_NAME"
#touch "$CPU_USAGE_FILE"

MEMORY_USAGE_FILE="$REPORT_FOLDER/$MEMORY_USAGE_FILE_NAME"
#touch "$MEMORY_USAGE_FILE"

LOOP_FILE="$REPORT_FOLDER/$LOOP_FILE_NAME"


###
### functions
###

function collectData() {
  POD_NAME=$1
  FINALIZE_JSON=$2

  TIMESTAMP=$(date +%s)
  TOP_POD_STRING=$(kubectl top pod "${POD_NAME}" --no-headers)
#  echo "TOP_POD_STRING=${TOP_POD_STRING}"
  CPU_USAGE=$(echo "${TOP_POD_STRING}" | awk '{print $2}')
  MEMORY_USAGE=$(echo "${TOP_POD_STRING}" | awk '{print $3}')
#  echo "CPU_USAGE=${CPU_USAGE}"
#  echo "MEMORY_USAGE=${MEMORY_USAGE}"
#  echo "TIMESTAMP=${TIMESTAMP}"

  if [ "$FINALIZE_JSON" = "true" ]; then
#    echo -e "${GREEN}finalizeJSON=true${NO_COLOR}"
    echo "{ \"timestamp\": \"${TIMESTAMP}\", \"cpu-usage\": \"${CPU_USAGE}\" }" >> "${CPU_USAGE_FILE}"
    echo "{ \"timestamp\": \"${TIMESTAMP}\", \"memory-usage\": \"${MEMORY_USAGE}\" }" >> "${MEMORY_USAGE_FILE}"
    echo "]}" >> "${CPU_USAGE_FILE}"
    echo "]}" >> "${MEMORY_USAGE_FILE}"
  else
#    echo -e "${RED}finalizeJSON=false${NO_COLOR}"
    echo "{ \"timestamp\": \"${TIMESTAMP}\", \"cpu-usage\": \"${CPU_USAGE}\" }," >> "${CPU_USAGE_FILE}"
    echo "{ \"timestamp\": \"${TIMESTAMP}\", \"memory-usage\": \"${MEMORY_USAGE}\" }," >> "${MEMORY_USAGE_FILE}"
  fi


}


#function stop(){
##    rm "${LOOP_FILE}"
##    sleep 5s
#    collectData "${POD_NAME}" true
#    sleep 1s
#    finalizeJSON
#}

function start(){
    touch "${LOOP_FILE}"
    while [ -f "${LOOP_FILE}" ]; do
#      echo -e "${GREEN}loop ... loop${NO_COLOR}"
      collectData "${POD_NAME}" "false"
      sleep 1
    done
    if [ ! -f "${LOOP_FILE}" ]; then
      sleep 3s
#      echo -e "${RED}breaking loop for pod ${POD_NAME}${NO_COLOR}"
      collectData "${POD_NAME}" true

    else
      echo -e "${RED}loop file still exists for pod ${POD_NAME}${NO_COLOR}"
    fi
}


###
### main
###

trap '{
  echo "caught signal"
  rm "${LOOP_FILE}"
}' SIGINT SIGTERM



POD_NAME=$(kubectl get pods -l="app=$SERVICE_NAME" -o json | jq -r '.items[0].metadata.name')



if [[ "${MODE}" == "start" ]]; then
  echo "{\"cpu-data\":[" > $CPU_USAGE_FILE
  echo "{\"memory-data\":[" > $MEMORY_USAGE_FILE
  start
fi

if [[ "${MODE}" == "stop" ]]; then
  echo "remove loop file ${LOOP_FILE}"
  rm "${LOOP_FILE}"
  sleep 5s
fi







