FROM node:alpine as builder

COPY src /app

WORKDIR /app

RUN npm install

FROM node:alpine as app

COPY --from=builder app .

EXPOSE 8080

CMD [ "node", "app.js" ]