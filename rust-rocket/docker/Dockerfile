####################################################################################################
## Builder
####################################################################################################
FROM rust:latest AS builder

RUN rustup target add x86_64-unknown-linux-musl
RUN apt update && apt install -y musl-tools musl-dev
RUN update-ca-certificates

# Create appuser
ENV USER=rustrocket
ENV UID=10001

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    "${USER}"


WORKDIR /rustrocket

COPY ./ .

RUN cargo build --target x86_64-unknown-linux-musl --release

####################################################################################################
## Final image
####################################################################################################
FROM scratch

# Import from builder.
COPY --from=builder /etc/passwd /etc/passwd
COPY --from=builder /etc/group /etc/group

WORKDIR /rustrocket

# Copy our build
COPY --from=builder /rustrocket/target/x86_64-unknown-linux-musl/release/rustrocket ./

# Use an unprivileged user.
USER rustrocket:rustrocket

ENV ROCKET_ADDRESS=0.0.0.0

# EXPOSE 8080 8081

CMD ["/rustrocket/rustrocket"]

