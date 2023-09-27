package io.tinyrpc.consumer.yaml.factory;

import io.tinyrpc.common.exception.RpcException;
import io.tinyrpc.constant.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ConsumerYamlConfigFactory {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerYamlConfigFactory.class);

	private static Map<String, Object> propMap;

	static {
		InputStream resourceAsStream = null;
		try {
			ClassLoader classLoader = ConsumerYamlConfigFactory.class.getClassLoader();
			URL resource = classLoader.getResource(RpcConstants.CONSUMER_YML_FILE_NAME);
			if (resource == null) {
				throw new RpcException(RpcConstants.CONSUMER_YML_FILE_NAME + " file is not found in classpath.");
			}

			Yaml yaml = new Yaml();
			resourceAsStream = classLoader.getResourceAsStream(RpcConstants.CONSUMER_YML_FILE_NAME);
			Map<String, Map<String, Map<String, Map<String, Object>>>> map = yaml.loadAs(resourceAsStream, HashMap.class);

			propMap = map.get(RpcConstants.TINYRPC).get(RpcConstants.ANDYADC).get(RpcConstants.CONSUMER);
		} catch (Exception e) {
			logger.error("read yaml file error", e);
			propMap = null;
		} finally {
			if (resourceAsStream != null) {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					logger.error("yaml stream close error: {}", e.getMessage());
				}
			}
		}
	}
}
