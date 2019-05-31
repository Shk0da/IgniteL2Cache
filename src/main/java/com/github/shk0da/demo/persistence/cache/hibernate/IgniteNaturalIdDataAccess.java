package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 *
 */
public class IgniteNaturalIdDataAccess extends IgniteCachedDomainDataAccess implements NaturalIdDataAccess {
    /**
     *
     */
    private final AccessType accessType;

    /**
     *
     */
    public IgniteNaturalIdDataAccess(HibernateAccessStrategyAdapter stgy, AccessType accessType,
                                     RegionFactory regionFactory,
                                     DomainDataRegion domainDataRegion, Ignite ignite,
                                     HibernateCacheProxy cache) {
        super(stgy, regionFactory, domainDataRegion, ignite, cache);

        this.accessType = accessType;
    }

    /**
     *
     */
    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     *
     */
    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor ses) {
        return DefaultCacheKeysFactory.staticCreateNaturalIdKey(naturalIdValues, persister, ses);
    }

    /**
     *
     */
    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetNaturalIdValues(cacheKey);
    }

    /**
     *
     */
    @Override
    public boolean insert(SharedSessionContractImplementor ses, Object key, Object val) throws CacheException {
        return stgy.insert(key, val);
    }

    /**
     *
     */
    @Override
    public boolean afterInsert(SharedSessionContractImplementor ses, Object key, Object val) throws CacheException {
        return stgy.afterInsert(key, val);
    }

    /**
     *
     */
    @Override
    public boolean update(SharedSessionContractImplementor ses, Object key, Object val) throws CacheException {
        return stgy.update(key, val);
    }

    /**
     *
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor ses, Object key, Object val, SoftLock lock) throws CacheException {
        return stgy.afterUpdate(key, val);
    }
}
