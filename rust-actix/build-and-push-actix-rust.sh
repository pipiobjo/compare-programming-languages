#!/bin/bash
echo "building .... "
docker build -t rust-actix -m 4g -f docker/Dockerfile .