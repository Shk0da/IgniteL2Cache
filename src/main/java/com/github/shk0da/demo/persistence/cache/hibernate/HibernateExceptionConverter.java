package com.github.shk0da.demo.persistence.cache.hibernate;

/**
 * Converts Ignite errors into Hibernate runtime exceptions.
 */
public interface HibernateExceptionConverter {
    /**
     * @param e Exception.
     * @return Converted exception.
     */
    RuntimeException convert(Exception e);
}
