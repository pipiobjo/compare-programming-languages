#!/bin/bash
echo "building .... "
docker build -t rust-actix-optimized -m 4g -f docker/Dockerfile .