package questbot.api

import java.util.concurrent.CompletableFuture

interface IBootstrap {

    fun startup()
    fun shutdown(): CompletableFuture<Void>
}