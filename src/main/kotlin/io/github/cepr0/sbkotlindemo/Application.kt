package io.github.cepr0.sbkotlindemo

import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.BeanUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.IntStream
import javax.persistence.Entity

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@SpringBootApplication
class Application(val repo: ModelRepo, val service: ModelService) {

    val log = getLogger(Application::class.java)

    @EventListener
    fun onReady(e: ApplicationReadyEvent) {

        // batch inserting
        log.info("[i] Batch inserting...")
        repo.saveAll(IntStream.range(0, 5).mapToObj { Model("value #$it") }.collect(toList<Model>()))

        // read all data from DB
        log.info("[i] Reading all entities...")
        repo.findAll();//.forEach(System.out::println)

        // example of reading one model or returning the default
        log.info("[i] Reading one entity...")
        println(repo.findByIdOrNull(UUID.randomUUID()) ?: Model("default value"))

        // variant with Optional
        log.info("[i] Reading one entity (a variant with Optional)...")
        println(repo.findById(UUID.randomUUID()).orElse(Model("default value")))

        // per element updating
        log.info("[i] Per element updating...")
        repo.findAll().forEach { it.id?.let { id -> service.update(id, Model("${it.value} updated")) } }
        //repo.findAll().forEach(System.out::println)

        // batch updating
        log.info("[i] Batch updating...")
        service.batchUpdate({ repo.findAll() }, { value -> Model("${value} updated") })
        //repo.findAll().forEach(System.out::println)
    }
}

/**
 * Entity example - as sub-class of [BaseEntity], which uses [UUID] as 'id' type.
 */
@Entity
data class Model(var value: String) : BaseEntity<UUID>() {
    override fun toString(): String {
        return "Model(${super.toString()}, value=$value)"
    }
}

interface ModelRepo : JpaRepository<Model, UUID>

@Service
class ModelService(val repo: ModelRepo) {

    private val IGNORED_PROPS = arrayOf("id", "version", "createdAt", "updatedAt")

    @Transactional
    fun update(id: UUID, model: Model): Optional<Model> {
        return repo.findById(id).map {
            BeanUtils.copyProperties(model, it, *IGNORED_PROPS)
            return@map it
        }
    }

    @Transactional
    fun batchUpdate(models: () -> Collection<Model>, mapper: (String) -> Model): Collection<Model> {
        return models.invoke().map {
            val model = mapper.invoke(it.value)
            BeanUtils.copyProperties(model, it, *IGNORED_PROPS)
            return@map it
        }
    }
}
