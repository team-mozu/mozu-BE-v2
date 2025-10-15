package team.mozu.dsm.adapter.out.sse

import org.springframework.stereotype.Component
import team.mozu.dsm.application.port.out.sse.SseEventStorePort
import team.mozu.dsm.domain.sse.model.SseEvent
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class SseEventStoreAdapter : SseEventStorePort {

    // In-memory storage for events (could be replaced with Redis or DB)
    private val eventStore = ConcurrentHashMap<String, SseEvent>()

    override fun save(event: SseEvent) {
        eventStore[event.id] = event
    }

    override fun findEventsAfter(clientId: String, lastEventId: String?): List<SseEvent> {
        val allEvents = eventStore.values
            .filter { it.clientId == clientId }
            .sortedBy { it.timestamp }

        return if (lastEventId != null) {
            val lastEventTimestamp = eventStore[lastEventId]?.timestamp
            if (lastEventTimestamp != null) {
                allEvents.filter { it.timestamp.isAfter(lastEventTimestamp) }
            } else {
                // If lastEventId not found, return recent events (last 1 hour)
                val oneHourAgo = LocalDateTime.now().minusHours(1)
                allEvents.filter { it.timestamp.isAfter(oneHourAgo) }
            }
        } else {
            // If no lastEventId, return recent events (last 10 minutes)
            val tenMinutesAgo = LocalDateTime.now().minusMinutes(10)
            allEvents.filter { it.timestamp.isAfter(tenMinutesAgo) }
        }
    }

    override fun findTeamEventsAfter(teamId: UUID, lastEventId: String?): List<SseEvent> {
        val allEvents = eventStore.values
            .filter { it.teamId == teamId }
            .sortedBy { it.timestamp }

        return if (lastEventId != null) {
            val lastEventTimestamp = eventStore[lastEventId]?.timestamp
            if (lastEventTimestamp != null) {
                allEvents.filter { it.timestamp.isAfter(lastEventTimestamp) }
            } else {
                // If lastEventId not found, return recent events (last 1 hour)
                val oneHourAgo = LocalDateTime.now().minusHours(1)
                allEvents.filter { it.timestamp.isAfter(oneHourAgo) }
            }
        } else {
            // If no lastEventId, return recent events (last 10 minutes)
            val tenMinutesAgo = LocalDateTime.now().minusMinutes(10)
            allEvents.filter { it.timestamp.isAfter(tenMinutesAgo) }
        }
    }

    override fun deleteOldEvents(beforeTimestamp: LocalDateTime) {
        val keysToRemove = eventStore.values
            .filter { it.timestamp.isBefore(beforeTimestamp) }
            .map { it.id }

        keysToRemove.forEach { eventStore.remove(it) }
    }
}
