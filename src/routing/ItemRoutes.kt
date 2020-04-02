package eu.yeger.routing

import eu.yeger.model.Item
import eu.yeger.model.respondWithResult
import eu.yeger.service.ItemService
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.itemRoutes() {
    val itemService: ItemService by inject()

    route("items") {
        get {
            val items = itemService.getAllItems()
            call.respondWithResult(items)
        }

        post {
            val item = call.receive<Item>()
            val result = itemService.createItem(item)
            call.respondWithResult(result)
        }

        put {
            val item = call.receive<Item>()
            val result = itemService.updateItem(item)
            call.respondWithResult(result)
        }
    }
}