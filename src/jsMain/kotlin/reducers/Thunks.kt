package reducers

import api.addEvent
import api.addEventRegistration
import api.addLinkedGuild
import api.addTableHosting
import api.fetchDiscordAccountInfo
import api.fetchEvents
import api.fetchPlayerInfo
import api.fetchServerSettings
import api.removeEventRegistration
import api.removeTableHosting
import api.updateEventRegistration
import api.updatePlayer
import api.updateTableHosting
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.codecranachan.roster.Event
import org.codecranachan.roster.Guild
import org.codecranachan.roster.PlayerDetails
import org.codecranachan.roster.Table
import org.codecranachan.roster.TableDetails
import org.reduxkotlin.Thunk

private val scope = MainScope()

fun updateUserId(): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        val player = fetchPlayerInfo()
        val discord = fetchDiscordAccountInfo()
        dispatch(UserIdentified(discord, player))
    }
}

fun updateServerSettings(): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val settings = fetchServerSettings()
        dispatch(ServerSettingsUpdated(settings))
        if (settings.guilds.isNotEmpty() && getState().calendar.selectedGuild == null) {
            dispatch(selectGuild(settings.guilds[0]))
        }
    }
}

fun linkGuild(g: Guild): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        addLinkedGuild(g)
        dispatch(selectGuild(g))
        dispatch(updateServerSettings())
    }
}

fun selectGuild(g: Guild): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        dispatch(GuildSelected(g))
        dispatch(updateEvents())
    }
}

fun createEvent(e: Event): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        addEvent(e)
        dispatch(updateEvents())
    }
}

fun registerPlayer(e: Event): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val player = getState().identity.player
        if (player != null) {
            addEventRegistration(e, player)
            dispatch(updateEvents())
        }
    }
}

fun unregisterPlayer(e: Event): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val player = getState().identity.player
        if (player != null) {
            removeEventRegistration(e, player)
            dispatch(updateEvents())
        }
    }
}

fun updateEvents(): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val calendar = getState().calendar
        if (calendar.selectedGuild != null) {
            val events = fetchEvents(calendar.selectedGuild)
            dispatch(EventsUpdated(events))
        }
    }
}

fun registerTable(e: Event): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val dm = getState().identity.player
        if (dm != null) {
            addTableHosting(e, dm)
            dispatch(updateEvents())
        }
    }
}

fun unregisterTable(e: Event): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val dm = getState().identity.player
        if (dm != null) {
            removeTableHosting(e, dm)
            dispatch(updateEvents())
        }
    }
}

fun joinTable(e: Event, t: Table?): Thunk<ApplicationState> = { dispatch, getState, _ ->
    scope.launch {
        val p = getState().identity.player
        if (p != null) {
            updateEventRegistration(e, p, t)
            dispatch(updateEvents())
        }
    }
}

fun updateTableDetails(tableId: Uuid, details: TableDetails): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        updateTableHosting(tableId, details)
        dispatch(updateEvents())
    }
}

fun updatePlayerDetails(details: PlayerDetails): Thunk<ApplicationState> = { dispatch, _, _ ->
    scope.launch {
        updatePlayer(details)
        dispatch(updateUserId())
        dispatch(updateEvents())
    }
}
