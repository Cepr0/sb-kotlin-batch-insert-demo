package io.github.cepr0.sbkotlindemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.IntStream
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.PrePersist

@SpringBootApplication
class Application(private val repo: ModelRepo) {
    @EventListener
    fun onReady(e: ApplicationReadyEvent) {
        val models = IntStream.range(0, 10).mapToObj { Model(null, it) }.collect(toList())
        repo.saveAll(models)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Entity
data class Model (
        @Id @GeneratedValue private var id: UUID? = null,
        private var value: Int
) {
    @PrePersist
    fun prePersist() {
        id = UUID.randomUUID()
    }
}

interface ModelRepo : JpaRepository<Model, Int>
