package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;

/**
 *
 */
public class IgniteCollectionDataAccess extends IgniteCachedDomainDataAccess implements CollectionDataAccess {
    /**
     *
     */
    private final AccessType accessType;

    /**
     *
     */
    public IgniteCollectionDataAccess(HibernateAccessStrategyAdapter stgy, AccessType accessType,
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
    public Object generateCacheKey(
            Object id,
            CollectionPersister persister,
            SessionFactoryImplementor factory,
            String tenantIdentifier) {
        return HibernateKeyWrapper.staticCreateCollectionKey(id, persister, tenantIdentifier);
    }

    /**
     *
     */
    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return ((HibernateKeyWrapper) cacheKey).id();
    }
}
