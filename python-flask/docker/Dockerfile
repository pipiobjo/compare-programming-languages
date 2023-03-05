# base image
FROM python:3.12.0a4-alpine3.17 AS compile-image

ENV VIRTUAL_ENV=/opt/venv
RUN python3 -m venv $VIRTUAL_ENV
ENV PATH="$VIRTUAL_ENV/bin:$PATH"

RUN mkdir wheels


# add and install requirements
RUN pip install --upgrade pip && pip install pip-tools && pip install wheel
COPY src/app/requirements.txt .
RUN pip wheel -r requirements.txt --wheel-dir=/wheels


FROM python:3.12.0a4-alpine3.17 AS runtime-image

RUN mkdir wheels

# copy Python dependencies from build image
COPY --from=compile-image /wheels /wheels

# switch working directory
WORKDIR /app

COPY src/app /app

# set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV PATH="/opt/venv/bin:/app:$PATH"

RUN pip install --no-index --find-links=/wheels -r requirements.txt

RUN chmod +x /app/boot.sh

# configure the container to run in an executed manner
ENTRYPOINT [ "boot.sh" ]
