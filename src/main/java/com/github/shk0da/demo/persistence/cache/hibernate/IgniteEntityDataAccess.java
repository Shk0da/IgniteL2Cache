package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 *
 */
public class IgniteEntityDataAccess extends IgniteCachedDomainDataAccess implements EntityDataAccess {
    /**
     *
     */
    private final AccessType accessType;

    /**
     *
     */
    public IgniteEntityDataAccess(HibernateAccessStrategyAdapter stgy, AccessType accessType,
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
    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory,
                                   String tenantIdentifier) {
        return HibernateKeyWrapper.staticCreateEntityKey(id, persister, tenantIdentifier);
    }

    /**
     *
     */
    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return ((HibernateKeyWrapper) cacheKey).id();
    }

    /**
     *
     */
    @Override
    public boolean insert(SharedSessionContractImplementor ses, Object key, Object val, Object ver) {
        return stgy.insert(key, val);
    }

    /**
     *
     */
    @Override
    public boolean afterInsert(SharedSessionContractImplementor ses, Object key, Object val, Object ver) {
        return stgy.afterInsert(key, val);
    }

    /**
     *
     */
    @Override
    public boolean update(SharedSessionContractImplementor ses, Object key, Object val, Object curVer, Object prevVer) {
        return stgy.update(key, val);
    }

    /**
     *
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor ses, Object key, Object val, Object curVer, Object prevVer, SoftLock lock) {
        return stgy.afterUpdate(key, val);
    }

    /**
     *
     */
    @Override
    public AccessType getAccessType() {
        return accessType;
    }
}
