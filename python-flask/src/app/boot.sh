#!/bin/bash
pwd

cd app

gunicorn --bind 0.0.0.0:8080 wsgi:app