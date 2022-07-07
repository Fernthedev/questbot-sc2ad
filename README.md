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

