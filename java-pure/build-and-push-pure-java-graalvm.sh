#!/bin/bash
echo "building .... "
docker build -t pure-java-openjdk -m 4g -f docker/graalvm/Dockerfile .
