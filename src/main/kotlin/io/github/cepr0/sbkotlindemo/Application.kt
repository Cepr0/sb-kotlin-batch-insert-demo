package io.github.cepr0.sbkotlindemo

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.IntStream
import javax.persistence.*

@SpringBootApplication
class Application(private val repo: ModelRepo) {
    @EventListener
    fun onReady(e: ApplicationReadyEvent) {
        val models = IntStream.range(0, 10).mapToObj { Model(it) }.collect(toList())
        repo.saveAll(models)
        repo.findAll().forEach(System.out::println)

        val model = repo.getModelById(UUID.randomUUID()) ?: Model(0) // throw IllegalArgumentException("foo not found")
        println(model)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@MappedSuperclass
open class BaseEntity (
        @Id @GeneratedValue private var id: UUID? = null,
        @Version private var version: Long? = null,
        @field:CreationTimestamp private var createdAt: Instant? = null,
        @field:UpdateTimestamp private var updatedAt: Instant? = null
) {
    @PrePersist
    fun prePersist() {
        id = UUID.randomUUID()
    }

    override fun toString(): String {
        return "BaseEntity(id=$id, version=$version, createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}

@Entity
data class Model (private val value: Int) : BaseEntity() {
    override fun toString(): String {
        return "Model(${super.toString()}, value=$value)"
    }
}

interface ModelRepo : JpaRepository<Model, UUID> {
    fun getModelById(id: UUID): Model?
}
