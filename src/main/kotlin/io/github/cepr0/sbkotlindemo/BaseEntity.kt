package io.github.cepr0.sbkotlindemo

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.Persistable
import java.time.Instant
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * Example of base entity with arbitrary 'id' type.
 * Id generation must be provided in the constructor while extending this class.
 */
@MappedSuperclass
abstract class BaseEntity<T>(
        @Id private val id: T
) : Persistable<T> {

    @Version
    private val version: Long? = null

    @field:CreationTimestamp
    val createdAt: Instant? = null

    @field:UpdateTimestamp
    val updatedAt: Instant? = null

    override fun getId(): T {
        return id
    }

    override fun isNew(): Boolean {
        return version == null
    }

    override fun toString(): String {
        return "BaseEntity(id=$id, version=$version, createdAt=$createdAt, updatedAt=$updatedAt, isNew=$isNew)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BaseEntity<*>
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}