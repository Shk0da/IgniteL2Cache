package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;

/**
 * Implementation of {@link TimestampsRegion}. This region is automatically created when query
 * caching is enabled and it holds most recent updates timestamps to queryable tables.
 * Name of timestamps region is {@link RegionFactory#DEFAULT_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAME}.
 */
public class IgniteTimestampsRegion extends IgniteGeneralDataRegion implements TimestampsRegion {
    /**
     * @param factory    Region factory.
     * @param regionName Region name.
     * @param cache      Region cache.
     */
    public IgniteTimestampsRegion(RegionFactory factory, String regionName, Ignite ignite, HibernateCacheProxy cache) {
        super(factory, regionName, ignite, cache);
    }
}
