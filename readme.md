[searchbrew.com](http://searchbrew.com)
==============

The missing search for homebrew.

![screenshot](https://raw.github.com/stephennancekivell/searchbrew/master/screenshot.png)

## publish

```
eval $(docker-machine env searchbrew)
cd server
sbt docker:publishLocal
cd ../docker-compose
docker-compose up -d
```