package com.cloverframework.core.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 可通过继承该类并重写toJsonString方法实现自定义格式输出
 * @author yl
 *
 */
public class JsonUtil {

	public static String toJsonString(Jsonable ja) {
		ObjectMapper om = new ObjectMapper();

		try {
			return om.writerWithDefaultPrettyPrinter().writeValueAsString(ja);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
