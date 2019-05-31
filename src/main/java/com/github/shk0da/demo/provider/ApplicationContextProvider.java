package com.github.shk0da.demo.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class ApplicationContextProvider {

    @Getter
    private static ApplicationContext applicationContext;

    private static final Supplier<Ignite> ignite = new Supplier<Ignite>() {

        private Ignite ignite; // = null

        @Override
        public Ignite get() {
            if (ignite == null) {
                if (applicationContext == null) {
                    log.warn("ApplicationContext is not defined!");
                    return null;
                }
                ignite = getBean(Ignite.class);
            }
            return ignite;
        }
    };

    @Autowired
    public ApplicationContextProvider(ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static Ignite ignite() {
        return ignite.get();
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) {
        return getApplicationContext().getBean(beanName, requiredType);
    }

    public static String getProperty(String name, String defaultValue) {
        if (applicationContext == null) {
            log.warn("ApplicationContext is not defined!");
            return defaultValue;
        }
        return getApplicationContext().getEnvironment().getProperty(name, defaultValue);
    }

    public static Integer getIntProperty(String name, Integer defaultValue) {
        if (applicationContext == null) {
            log.warn("ApplicationContext is not defined!");
            return defaultValue;
        }
        String value = getApplicationContext().getEnvironment().getProperty(name, defaultValue.toString());
        return value != null && !value.isEmpty() ? Integer.valueOf(value) : 0;
    }
}
