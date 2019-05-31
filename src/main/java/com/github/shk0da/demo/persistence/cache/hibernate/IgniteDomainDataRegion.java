package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.ExtendedStatisticsSupport;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.AbstractDomainDataRegion;

/**
 *
 */
public class IgniteDomainDataRegion extends AbstractDomainDataRegion implements ExtendedStatisticsSupport {
    /**
     *
     */
    private final HibernateCacheProxy cache;

    /**
     *
     */
    private HibernateAccessStrategyFactory stgyFactory;

    /**
     *
     */
    public IgniteDomainDataRegion(DomainDataRegionConfig regionCfg,
                                  RegionFactory regionFactory,
                                  CacheKeysFactory defKeysFactory,
                                  DomainDataRegionBuildingContext buildingCtx,
                                  HibernateAccessStrategyFactory stgyFactory) {
        super(regionCfg, regionFactory, defKeysFactory, buildingCtx);

        this.stgyFactory = stgyFactory;

        cache = stgyFactory.regionCache(getName());

        completeInstantiation(regionCfg, buildingCtx);
    }

    /**
     *
     */
    @Override
    protected EntityDataAccess generateEntityAccess(EntityDataCachingConfig entityAccessCfg) {
        AccessType accessType = entityAccessCfg.getAccessType();
        Ignite ignite = stgyFactory.node();
        switch (accessType) {
            case READ_ONLY:
                HibernateAccessStrategyAdapter readOnlyStgy =
                        stgyFactory.createReadOnlyStrategy(cache);
                return new IgniteEntityDataAccess(readOnlyStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case NONSTRICT_READ_WRITE:
                HibernateAccessStrategyAdapter nonStrictReadWriteStgy =
                        stgyFactory.createNonStrictReadWriteStrategy(cache);
                return new IgniteEntityDataAccess(nonStrictReadWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case READ_WRITE:
                HibernateAccessStrategyAdapter readWriteStgy =
                        stgyFactory.createReadWriteStrategy(cache);
                return new IgniteEntityDataAccess(readWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case TRANSACTIONAL:
                HibernateAccessStrategyAdapter transactionalStgy =
                        stgyFactory.createTransactionalStrategy(cache);
                return new IgniteEntityDataAccess(transactionalStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            default:
                throw new IllegalArgumentException("Unknown Hibernate access type: " + accessType);
        }
    }

    /**
     *
     */
    @Override
    protected CollectionDataAccess generateCollectionAccess(CollectionDataCachingConfig cachingCfg) {
        HibernateCacheProxy cache = stgyFactory.regionCache(getName());
        AccessType accessType = cachingCfg.getAccessType();
        Ignite ignite = stgyFactory.node();
        switch (accessType) {
            case READ_ONLY:
                HibernateAccessStrategyAdapter readOnlyStgy =
                        stgyFactory.createReadOnlyStrategy(cache);
                return new IgniteCollectionDataAccess(readOnlyStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case NONSTRICT_READ_WRITE:
                HibernateAccessStrategyAdapter nonStrictReadWriteStgy =
                        stgyFactory.createNonStrictReadWriteStrategy(cache);
                return new IgniteCollectionDataAccess(nonStrictReadWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case READ_WRITE:
                HibernateAccessStrategyAdapter readWriteStgy =
                        stgyFactory.createReadWriteStrategy(cache);
                return new IgniteCollectionDataAccess(readWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case TRANSACTIONAL:
                HibernateAccessStrategyAdapter transactionalStgy =
                        stgyFactory.createTransactionalStrategy(cache);
                return new IgniteCollectionDataAccess(transactionalStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            default:
                throw new IllegalArgumentException("Unknown Hibernate access type: " + accessType);
        }
    }

    /**
     *
     */
    @Override
    protected NaturalIdDataAccess generateNaturalIdAccess(NaturalIdDataCachingConfig naturalIdAccessCfg) {
        HibernateCacheProxy cache = stgyFactory.regionCache(getName());
        AccessType accessType = naturalIdAccessCfg.getAccessType();
        Ignite ignite = stgyFactory.node();
        switch (accessType) {
            case READ_ONLY:
                HibernateAccessStrategyAdapter readOnlyStgy =
                        stgyFactory.createReadOnlyStrategy(cache);
                return new IgniteNaturalIdDataAccess(readOnlyStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case NONSTRICT_READ_WRITE:
                HibernateAccessStrategyAdapter nonStrictReadWriteStgy =
                        stgyFactory.createNonStrictReadWriteStrategy(cache);
                return new IgniteNaturalIdDataAccess(nonStrictReadWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case READ_WRITE:
                HibernateAccessStrategyAdapter readWriteStgy =
                        stgyFactory.createReadWriteStrategy(cache);
                return new IgniteNaturalIdDataAccess(readWriteStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            case TRANSACTIONAL:
                HibernateAccessStrategyAdapter transactionalStgy =
                        stgyFactory.createTransactionalStrategy(cache);
                return new IgniteNaturalIdDataAccess(transactionalStgy, accessType, getRegionFactory(),
                        this, ignite, cache);

            default:
                throw new IllegalArgumentException("Unknown Hibernate access type: " + accessType);
        }
    }

    /**
     *
     */
    @Override
    public void destroy() throws CacheException {
        // no-op
    }

    /**
     *
     */
    @Override
    public long getElementCountInMemory() {
        return cache.offHeapEntriesCount();
    }

    /**
     *
     */
    @Override
    public long getElementCountOnDisk() {
        return cache.sizeLong();
    }

    /**
     *
     */
    @Override
    public long getSizeInMemory() {
        return cache.offHeapAllocatedSize();
    }
}
