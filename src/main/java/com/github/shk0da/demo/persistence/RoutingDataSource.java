package com.github.shk0da.demo.persistence;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<Route> ctx = new ThreadLocal<>();

    public static void clearReplicaRoute() {
        ctx.remove();
    }

    public static void setReplicaRoute() {
        ctx.set(Route.REPLICA);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ctx.get();
    }

    public enum Route {
        PRIMARY, REPLICA
    }
}