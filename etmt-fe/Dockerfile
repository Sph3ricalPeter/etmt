FROM node:17-alpine

WORKDIR /app

ENV PATH /app/node/node_modules/.bin:$PATH
ENV NODE_OPTIONS --openssl-legacy-provider

COPY package.json ./

COPY package-lock.json ./

RUN npm install

COPY . ./

CMD ["npm", "start"]
