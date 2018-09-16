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

@SpringBootApplication
class Application(private val repo: ModelRepo) {

    @EventListener
    fun onReady(e: ApplicationReadyEvent) {

        // populate demo data
        repo.saveAll(IntStream
                .range(0, 10)
                .mapToObj { Model(it.toString()) }
                .collect(toList<Model>())
        )

        // read data from DB
        repo.findAll().forEach(System.out::println)

        // example of reading one model or returning default one in case of 'null'
        val model = repo.getModelById(UUID.randomUUID())
                ?: Model("default") // or replace with throw RuntimeException("Model not found")

        println(model)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

/**
 * Entity example - as sub-class of [BaseEntity], which uses [UUID] as 'id' type.
 */
@Entity
data class Model(
        val value: String
) : BaseEntity<UUID>(UUID.randomUUID()) { // <- providing id generation

    override fun toString(): String {
        return "Model(value=$value, ${super.toString()})"
    }
}

interface ModelRepo : JpaRepository<Model, UUID> {
    // example of replacement the 'Optional' as returned value
    fun getModelById(id: UUID): Model?
}
