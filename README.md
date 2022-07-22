# Quest bot for sc2ad's quest server

This bot handles the ~~memes~~ functionality of the discord server.

## Language/Library
This uses `Kotlin` as it's sole and primary language, with `Javacord` as its library. 

This makes heavy use of Guice for initializing and injecting dependencies (why not)

## Commands
Commands are scanned by the Injector at server startup. Inherit `CommandHandler` and the rest is done for you
 
Take a look at `PinkCuteCommand` as an example

## Listeners
Currently, there is only `MessageListener`. Inherit this class, bootstrapper will find it and inject it.

## Building
You can either use the profile configured in the project for Intellij or in the terminal `./gradlew clean build`

## Dev testing running
Setup `token.txt` with the bot token and then run using the Intellij run profile or `./gradlew run

## Deploying
Deployment assumes usage of the [gradle distribution plugin](https://docs.gradle.org/current/userguide/distribution_plugin.html#distribution_plugin)

In other words, using the zips in `build/distributions`

## Deploying with Docker
_No more Docker Hub free automatic builds 🥲_



Run in the repository:
```shell
gradlew clean installDist build
docker build . -t questbot-sc2ad
```

Then create a Dockerfile with the following and deploy:
```dockerfile
FROM questbot-sc2ad
COPY ./token.txt "/opt/app/bin/token.txt"
```