package org.codecranachan.roster.api

import com.benasher44.uuid.Uuid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.codecranachan.roster.Event
import org.codecranachan.roster.EventRegistration
import org.codecranachan.roster.repo.*

class EventApi(private val repository: Repository) {

    fun install(r: Route) {
        with(r) {
            get("/api/v1/events/{id}") {
                val id = Uuid.fromString(call.parameters["id"])
                val event = repository.fetchEvent(id)
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(event)
                }
            }

            post("/api/v1/events") {
                val event = call.receive<Event>()
                repository.addEvent(event)
                call.respond(HttpStatusCode.Created)
            }

            post("/api/v1/events/{evtId}/registrations") {
                val evtId = Uuid.fromString(call.parameters["evtId"])
                val reg = call.receive<EventRegistration>()
                repository.addEventRegistration(EventRegistration(reg.id, evtId, reg.playerId))
                call.respond(HttpStatusCode.Created)
            }

            delete("/api/v1/events/{evtId}/registrations/{plrId}") {
                val evtId = Uuid.fromString(call.parameters["evtId"])
                val plrId = Uuid.fromString(call.parameters["plrId"])
                repository.removeEventRegistration(evtId, plrId)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}