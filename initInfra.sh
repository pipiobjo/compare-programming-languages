#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/"


helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update


helm upgrade --install prometheus prometheus-community/prometheus