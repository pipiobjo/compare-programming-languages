#!/bin/bash
echo "building .... "
docker build -t rust-rocket -m 4g -f docker/Dockerfile .