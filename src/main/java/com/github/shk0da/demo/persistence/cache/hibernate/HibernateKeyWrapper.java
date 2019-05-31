package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.internal.util.typedef.internal.S;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;

/**
 * Hibernate cache key wrapper.
 */
public class HibernateKeyWrapper implements Serializable {
    /**
     * Key.
     */
    private final Object key;

    /**
     * Entry.
     */
    private final String entry;

    /**
     *
     */
    private final String tenantId;

    /**
     * @param key      Key.
     * @param entry    Entry.
     * @param tenantId Tenant ID.
     */
    HibernateKeyWrapper(Object key, String entry, String tenantId) {
        this.key = key;
        this.entry = entry;
        this.tenantId = tenantId;
    }

    /**
     * @param id               ID.
     * @param persister        Persister.
     * @param tenantIdentifier Tenant ID.
     * @return Cache key.
     * @see DefaultCacheKeysFactory#staticCreateCollectionKey(Object, CollectionPersister, SessionFactoryImplementor, String)
     */
    static Object staticCreateCollectionKey(Object id,
                                            CollectionPersister persister,
                                            String tenantIdentifier) {
        return new HibernateKeyWrapper(id, persister.getRole(), tenantIdentifier);
    }

    /**
     * @param id               ID.
     * @param persister        Persister.
     * @param tenantIdentifier Tenant ID.
     * @return Cache key.
     * @see DefaultCacheKeysFactory#staticCreateEntityKey(Object, EntityPersister, SessionFactoryImplementor, String)
     */
    public static Object staticCreateEntityKey(Object id, EntityPersister persister, String tenantIdentifier) {
        return new HibernateKeyWrapper(id, persister.getRootEntityName(), tenantIdentifier);
    }

    /**
     * @return ID.
     */
    Object id() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        HibernateKeyWrapper that = (HibernateKeyWrapper) o;

        return (key != null ? key.equals(that.key) : that.key == null) &&
                (entry != null ? entry.equals(that.entry) : that.entry == null) &&
                (tenantId != null ? tenantId.equals(that.tenantId) : that.tenantId == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int res = key != null ? key.hashCode() : 0;
        res = 31 * res + (entry != null ? entry.hashCode() : 0);
        res = 31 * res + (tenantId != null ? tenantId.hashCode() : 0);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return S.toString(HibernateKeyWrapper.class, this);
    }
}
