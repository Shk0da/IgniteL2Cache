package com.github.shk0da.demo.persistence.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.query.Query;

/**
 * Implementation of {@link QueryResultsRegion}. This region is used to store query results.
 * <p>
 * Query results caching can be enabled in the Hibernate configuration file:
 * <pre name="code" class="xml">
 * &lt;hibernate-configuration&gt;
 *     &lt;!-- Enable L2 cache. --&gt;
 *     &lt;property name="cache.use_second_level_cache"&gt;true&lt;/property&gt;
 *
 *     &lt;!-- Enable query cache. --&gt;
 *     &lt;property name="cache.use_second_level_cache"&gt;true&lt;/property&gt;
 *
 *     &lt;!-- Use Ignite as L2 cache provider. --&gt;
 *     &lt;property name="cache.region.factory_class"&gt;com.github.shk0da.demo.persistence.cache.hibernate.HibernateRegionFactory&lt;/property&gt;
 *
 *     &lt;!-- Specify entity. --&gt;
 *     &lt;mapping class="com.example.Entity"/&gt;
 *
 *     &lt;!-- Enable L2 cache with nonstrict-read-write access strategy for entity. --&gt;
 *     &lt;class-cache class="com.example.Entity" usage="nonstrict-read-write"/&gt;
 * &lt;/hibernate-configuration&gt;
 * </pre>
 * By default queries are not cached even after enabling query caching, to enable results caching for a particular
 * query, call {@link Query#setCacheable(boolean)}:
 * <pre name="code" class="java">
 *     Session ses = getSession();
 *
 *     Query qry = ses.createQuery("...");
 *
 *     qry.setCacheable(true); // Enable L2 cache for query.
 * </pre>
 * Note: the query cache does not cache the state of the actual entities in the cache, it caches only identifier
 * values. For this reason, the query cache should always be used in conjunction with
 * the second-level cache for those entities expected to be cached as part of a query result cache
 */
public class IgniteQueryResultsRegion extends IgniteGeneralDataRegion implements QueryResultsRegion {
    /**
     * @param factory Region factory.
     * @param name    Region name.
     * @param ignite  Grid.
     * @param cache   Region cache.
     */
    public IgniteQueryResultsRegion(HibernateRegionFactory factory, String name,
                                    Ignite ignite, HibernateCacheProxy cache) {
        super(factory, name, ignite, cache);
    }
}
