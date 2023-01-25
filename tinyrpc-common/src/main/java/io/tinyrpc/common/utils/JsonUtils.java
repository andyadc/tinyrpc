package io.tinyrpc.common.utils;

import com.alibaba.fastjson2.JSON;

public final class JsonUtils {

	public static String toJSONString(Object obj) {
		return JSON.toJSONString(obj);
	}
}
