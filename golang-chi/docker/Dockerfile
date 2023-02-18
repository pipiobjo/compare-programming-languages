# Build environment
# -----------------
FROM golang:1.20-alpine as build-env
WORKDIR /build

RUN mkdir -p /app
RUN mkdir -p /build

ENV GO111MODULE="on" \
    CGO_ENABLED=0 \
    GOOS=linux \
    GOFLAGS="-mod=vendor"

COPY ./app /build

RUN go mod tidy && go mod download
RUN go build -ldflags '-w -s' -o /app/ -mod vendor /build/cmd/main.go \
    && echo "list app dir after build" && ls -la /app \
    && chmod +x /app/main

# Deployment environment
# ----------------------
FROM scratch
ENV USER=serviceuser
ENV UID=10001


COPY --chown=${UID}:0 --from=build-env /app/main /app/
USER ${UID}

EXPOSE 8080
EXPOSE 8081
ENTRYPOINT ["/app/main"]