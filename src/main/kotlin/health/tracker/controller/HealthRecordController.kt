package health.tracker.controller

import health.tracker.model.HealthRecord
import health.tracker.persistence.HealthRecordRepository
import kotlinx.coroutines.flow.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class HealthRecordController(private val healthRecordRepository: HealthRecordRepository) {

    @PostMapping("/health/{profileId}/record")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun storeHealthRecord(@PathVariable profileId: Long, @RequestBody record: HealthRecord): HealthRecord {
        return healthRecordRepository.save(
            HealthRecord(
                null, profileId, record.temperature, record.bloodPressureSystolic, record.bloodPressureDiastolic, record.heartRate, record.date
            )
        )
    }

    @DeleteMapping("/health/{profileId}/records")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteRecords(@PathVariable profileId: Long) {
        healthRecordRepository.deleteAll()
    }

    @GetMapping("/health/{profileId}/records")
    suspend fun records(@PathVariable profileId: Long): Flow<HealthRecord> =
        healthRecordRepository.findByProfileId(profileId)
            .onStart { println("onStart FlowCollector<HealthRecord>") }
            .onEach { println(it) }
            .onCompletion { println("onCompletion FlowCollector<HealthRecord>") }


    @GetMapping("/health/{profileId}/avg")
    suspend fun avg(@PathVariable profileId: Long): AverageHealthStatus =
        healthRecordRepository
            .findByProfileId(profileId)
            .fold(AverageHealthStatus(0, 0.0, 0, 0, 0.0)) { acc, healthRecord ->
                acc.cnt++
                acc.temperature += healthRecord.temperature
                acc.bloodPressureSystolic += healthRecord.bloodPressureSystolic
                acc.bloodPressureDiastolic += healthRecord.bloodPressureDiastolic
                acc.heartRate += healthRecord.heartRate
                acc
            }.let {
                it.bloodPressureSystolic /= it.cnt
                it.bloodPressureDiastolic /= it.cnt
                it.temperature /= it.cnt
                it.heartRate /= it.cnt
                it
            }
}

class AverageHealthStatus(
    var cnt: Int, var temperature: Double,
    var bloodPressureSystolic: Int, var bloodPressureDiastolic: Int, var heartRate: Double
)