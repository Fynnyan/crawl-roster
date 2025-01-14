package org.codecranachan.roster

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

enum class TableState {
    Full,
    Ready,
    Understrength,
    Empty
}

@Serializable
data class PlaySession(val table: Table, val players: List<Player>) {
    fun getState(): TableState {
        return when {
            players.isEmpty() -> TableState.Empty
            players.size >= table.details.playerRange.last -> TableState.Full
            players.size <= table.details.playerRange.first -> TableState.Understrength
            else -> TableState.Ready
        }
    }

    fun isPlayer(player: Player): Boolean {
        return players.map { it.id }.contains(player.id)
    }

    fun isDungeonMaster(player: Player): Boolean {
        return table.dungeonMaster.id == player.id
    }

    fun occupancyPercentage(): Int {
        return players.size * 100 / table.details.playerRange.last
    }

}

@Serializable
data class EventDetails(
    val time: LocalTime? = null,
    val location: String? = null,
)

@Serializable
data class Event(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid = uuid4(),
    @Serializable(with = UuidSerializer::class)
    val guildId: Uuid,
    val date: LocalDate,
    val sessions: List<PlaySession> = listOf(),
    val unseated: List<Player> = listOf(),
    val details: EventDetails = EventDetails()
) {
    fun isRegistered(p: Player): Boolean {
        return sessions.any { it.isPlayer(p) } || unseated.map { it.id }.contains(p.id)
    }

    fun isHosting(p: Player): Boolean {
        return sessions.any { it.isDungeonMaster(p) }
    }

    fun seatedPlayerCount(): Int {
        return sessions.sumOf { it.players.size }
    }

    fun playerCount(): Int {
        return seatedPlayerCount() + unseated.size
    }

    fun tableSpace(): Int {
        return sessions.sumOf { it.table.details.playerRange.last }
    }

    fun unclaimedSeatCount(): Int {
        return tableSpace() - seatedPlayerCount()
    }

    private fun remainingCapacity(): Int {
        return tableSpace() - playerCount()
    }

    fun openSeatCount(): Int {
        return maxOf(0, remainingCapacity())
    }

    fun waitingListLength(): Int {
        return 0 - minOf(0, remainingCapacity())
    }

    fun getHostedTable(p: Player): Table? {
        return sessions.map { it.table }.find { it.dungeonMaster.id == p.id }
    }

    fun getFormattedDate(): String {
        return "${date.dayOfWeek.name.substring(0..2)} - ${date.dayOfMonth}. ${
            date.month.name.lowercase().replaceFirstChar { it.titlecase() }
        }, ${date.year}"
    }

}

@Serializable
data class EventRegistration(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid = uuid4(),
    @Serializable(with = UuidSerializer::class)
    val eventId: Uuid,
    @Serializable(with = UuidSerializer::class)
    val playerId: Uuid,
    @Serializable(with = UuidSerializer::class)
    val tableId: Uuid? = null
)

@Serializable
data class TableHosting(
    @Serializable(with = UuidSerializer::class)
    val id: Uuid = uuid4(),
    @Serializable(with = UuidSerializer::class)
    val eventId: Uuid,
    @Serializable(with = UuidSerializer::class)
    val dungeonMasterId: Uuid
)