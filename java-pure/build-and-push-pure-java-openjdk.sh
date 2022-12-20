#!/bin/bash
echo "building .... "
docker build -t pure-java-openjdk -f docker/openjdk/Dockerfile .
