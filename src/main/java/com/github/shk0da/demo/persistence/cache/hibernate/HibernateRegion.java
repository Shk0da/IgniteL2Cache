package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.ExtendedStatisticsSupport;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.AbstractRegion;

/**
 * Implementation of {@link Region}. This interface defines base contract for all L2 cache regions.
 */
public abstract class HibernateRegion extends AbstractRegion implements ExtendedStatisticsSupport {
    /**
     * Cache instance.
     */
    protected final HibernateCacheProxy cache;

    /**
     * Grid instance.
     */
    protected Ignite ignite;

    /**
     *
     */
    protected HibernateRegion(RegionFactory factory, String name, Ignite ignite, HibernateCacheProxy cache) {
        super(name, factory);

        this.ignite = ignite;
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        try {
            cache.clear();
        } catch (IgniteCheckedException e) {
            throw new CacheException("Problem clearing cache [name=" + cache.name() + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws CacheException {
        // No-op.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getElementCountInMemory() {
        return cache.offHeapEntriesCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getElementCountOnDisk() {
        return cache.sizeLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSizeInMemory() {
        return cache.offHeapAllocatedSize();
    }
}
