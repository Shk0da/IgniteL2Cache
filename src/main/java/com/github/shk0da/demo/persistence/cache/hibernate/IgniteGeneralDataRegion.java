package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteLogger;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.DirectAccessRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link DirectAccessRegion}. This interface defines common contract for {@link QueryResultsRegion}
 * and {@link TimestampsRegion}.
 */
public class IgniteGeneralDataRegion extends HibernateRegion implements DirectAccessRegion {
    /**
     *
     */
    private final IgniteLogger log;

    /**
     * @param factory Region factory.
     * @param name    Region name.
     * @param ignite  Grid.
     * @param cache   Region cache.
     */
    IgniteGeneralDataRegion(RegionFactory factory, String name,
                            Ignite ignite, HibernateCacheProxy cache) {
        super(factory, name, ignite, cache);

        log = ignite.log().getLogger(getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor ses) throws CacheException {
        try {
            Object val = cache.get(key);

            if (log.isDebugEnabled())
                log.debug("Get [cache=" + cache.name() + ", key=" + key + ", val=" + val + ']');

            return val;
        } catch (IgniteCheckedException e) {
            throw new CacheException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putIntoCache(Object key, Object val, SharedSessionContractImplementor ses) throws CacheException {
        try {
            cache.put(key, val);

            if (log.isDebugEnabled())
                log.debug("Put [cache=" + cache.name() + ", key=" + key + ", val=" + val + ']');
        } catch (IgniteCheckedException e) {
            throw new CacheException(e);
        }
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
}
