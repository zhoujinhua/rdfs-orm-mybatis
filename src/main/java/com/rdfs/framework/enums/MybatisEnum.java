package com.rdfs.framework.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class MybatisEnum {
	public static String setLikeCloumn(Map<String, Object> params, String[] likeNameArray) {
		StringBuffer extSql = new StringBuffer();
		for (int i = 0; i < likeNameArray.length; i++) {
			String likeN = likeNameArray[i];
			String likeV = (String) params.get(likeN);
			if ((likeN != null) && (StringUtils.isNotBlank(likeV))) {
				extSql.append(likeN).append(" like '%" + likeV + "%'").append(" and");
				params.remove(likeN);
			}
		}
		if (extSql.length() != 0) {
			return extSql.substring(0, extSql.length() - 4);
		}
		return extSql.toString();
	}

	public static StringBuffer setLikeCloumn(Map<String, Object> params, String[] likeNameArray, String sql2) {
		StringBuffer extSql = new StringBuffer(setLikeCloumn(params, likeNameArray));
		if (extSql.length() != 0) {
			extSql.append(" and ");
		}
		extSql.append(sql2);
		return extSql;
	}

	public static enum MYBATIS_SPECIAL_STRING {
		ORDER_BY, LIMIT, COLUMNS, TABLES, WHERE, LIKE;

		public static List<String> list() {
			List<String> result = new ArrayList<>();
			for (MYBATIS_SPECIAL_STRING entry : values()) {
				result.add(entry.name());
			}
			return result;
		}
	}
}