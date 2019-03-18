package io.github.cepr0.sbkotlindemo

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

/**
 * Base entity class with arbitrary 'id' type.
 */
@MappedSuperclass
abstract class BaseEntity<T> {

    @Id @GeneratedValue var id: T? = null
    @Version var version: Int? = null
    @field:CreationTimestamp var createdAt: Instant? = null
    @field:UpdateTimestamp var updatedAt: Instant? = null

    override fun toString(): String {
        return "id=$id, version=$version, createdAt=$createdAt, updatedAt=$updatedAt"
    }

    // See details here https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as BaseEntity<*>
        return if (id == null) false else id == other.id
    }

    override fun hashCode(): Int {
        return 31
    }
}