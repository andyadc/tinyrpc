package io.tinyrpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanManager {

    private static final Logger logger = LoggerFactory.getLogger(BeanManager.class);

    private static Map<String, Object> services = new ConcurrentHashMap<>();

    public static void registerBean(String serviceName, Object bean) {
        services.put(serviceName.toLowerCase(), bean);
        logger.info("BeanManager registered bean [{}-{}]", serviceName, bean);
    }

    public static Object getBean(String serviceName) {
        return services.get(serviceName.toLowerCase());
    }
}
