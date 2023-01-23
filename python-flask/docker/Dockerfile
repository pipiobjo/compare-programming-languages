# start by pulling the python image
FROM python:3.10.6-alpine

# copy the requirements file into the image
COPY src /app

# switch working directory
WORKDIR /app

# install the dependencies and packages in the requirements file
RUN pip install -r app/requirements.txt

RUN apk add --no-cache bash
RUN apk --no-cache add curl
RUN chmod +x app/boot.sh

# configure the container to run in an executed manner
ENTRYPOINT [ "app/boot.sh" ]
