# Stage 1
FROM node:current-alpine3.17 as build
WORKDIR /app
COPY package.json .
RUN apk add --no-cache git make clang build-base python3
RUN yarn install
COPY . .
RUN yarn build

# Stage 2
FROM nginx:latest
WORKDIR /usr/share/nginx/html
COPY --from=build /app/dist .
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]