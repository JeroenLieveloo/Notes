const val CACHE_NAME = "thread-notes-v1";
val urlsToCache = arrayOf(
    "/",
    "/manifest.json"
)

external val self: ServiceWorkerGlobalScope

fun installServiceWorker() {
    self.addEventListener("install", { event ->
        event as InstallEvent
        event.waitUntil(
            self.caches.open(CACHE_NAME)
                .then { it.addAll(urlsToCache) }
            )
        }
    )
}