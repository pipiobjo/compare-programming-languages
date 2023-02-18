FROM node:latest

COPY src /app

WORKDIR /app

RUN npm install

COPY . .

EXPOSE 8080

CMD [ "node", "app.js" ]