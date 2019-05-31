package com.github.shk0da.demo.persistence.cache.hibernate;

/**
 * An interface for transforming hibernate keys to Ignite keys.
 */
public interface HibernateKeyTransformer {
    /**
     * @param key Hibernate key.
     * @return Transformed key.
     */
    Object transform(Object key);
}
