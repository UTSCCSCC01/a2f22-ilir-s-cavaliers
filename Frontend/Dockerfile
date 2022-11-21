FROM node:14.16.0-alpine3.10
ENV PORT 3000
WORKDIR /frontend
# install app dependencies
COPY ./package.json ./

RUN yarn
# add app
COPY . ./
# start app
EXPOSE 3000
CMD "yarn" "dev"