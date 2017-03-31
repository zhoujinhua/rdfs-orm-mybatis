package com.rdfs.framework.utils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rdfs.framework.annotation.Id;
import com.rdfs.framework.annotation.NotColumn;
import com.rdfs.framework.annotation.TableName;
import com.rdfs.framework.enums.MybatisEnum;

public class DtoUtil {

	private static final Map<String, Class<?>> FIELD_TYPE_MAP = new HashMap<>();
	public static final String SPACE_TABLE_NAME = "SPACE_TABLE_NAME";
	private static Map<Class<?>, Map<String, String>> columnMap = new HashMap<>();

	public static final void setBaseType() {
		if (!FIELD_TYPE_MAP.isEmpty()) {
			return;
		}
		FIELD_TYPE_MAP.put("Double", Double.class);
		FIELD_TYPE_MAP.put("Short", Short.class);
		FIELD_TYPE_MAP.put("Long", Long.class);
		FIELD_TYPE_MAP.put("Float", Float.class);
		FIELD_TYPE_MAP.put("Integer", Integer.class);
		FIELD_TYPE_MAP.put("Byte", Byte.class);
		FIELD_TYPE_MAP.put("String", String.class);
		FIELD_TYPE_MAP.put("Character", Character.class);
		FIELD_TYPE_MAP.put("sDate", java.sql.Date.class);
		FIELD_TYPE_MAP.put("Boolean", Boolean.class);
		FIELD_TYPE_MAP.put("Date", java.util.Date.class);
	}

	public static String tableName(Serializable obj) {
		String tableName = null;
		Annotation t = obj.getClass().getAnnotation(TableName.class);
		if (t != null) {
			tableName = ((TableName) t).name();
		} else {
			String objClassName = obj.getClass().getSimpleName();
			tableName = objClassName.replaceAll("([A-Z])", "_$1").replaceFirst("_", "").toLowerCase();
		}
		return tableName;
	}

	public static String id(Serializable obj) {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (field.getAnnotation(Id.class) != null)
				return field.getName();
		}
		if ((obj.getClass().equals(Long.class)) || (obj.getClass().equals(Long.TYPE))
				|| (obj.getClass().equals(Integer.TYPE)) || (obj.getClass().equals(Integer.class))) {
			return "id";
		}
		throw new RuntimeException("undefine " + obj.getClass().getName() + " @Id");
	}

	private static boolean isNull(Serializable obj, String fieldname) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldname);
			return isNull(obj, field);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean isNull(Serializable obj, Field field) {
		try {
			field.setAccessible(true);
			return field.get(obj) == null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void caculationColumnList(Serializable obj) {
		setBaseType();
		Class<?> className = obj.getClass();
		if (columnMap.containsKey(className)) {
			return;
		}
		Field[] fields = className.getDeclaredFields();
		Map<String, String> fieldMap = new HashMap<>();
		NotColumn notColumn;
		for (Field field : fields) {
			notColumn = (NotColumn) field.getAnnotation(NotColumn.class);
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			boolean isFinal = Modifier.isFinal(field.getModifiers());
			boolean isPrimitive = (field.getType().isPrimitive()) || (FIELD_TYPE_MAP.containsValue(field.getType()));
			if ((notColumn != null) || (isStatic) || (isFinal) || (!isPrimitive)) {
				continue;
			}
			String fieldName = field.getName();
			String column = fieldName.replaceAll("([A-Z])", "_$1").toLowerCase();
			fieldMap.put(fieldName, column);
		}

		columnMap.put(className, fieldMap);
		Class<?> parentClass = className.getSuperclass();
		if ((parentClass != null) && (!parentClass.getSimpleName().equals("Serializable"))) {
			fields = className.getSuperclass().getDeclaredFields();
			for (Field field : fields) {
				notColumn = (NotColumn) field.getAnnotation(NotColumn.class);
				boolean isStatic = Modifier.isStatic(field.getModifiers());
				boolean isFinal = Modifier.isFinal(field.getModifiers());
				boolean isPrimitive = (field.getType().isPrimitive())
						|| (FIELD_TYPE_MAP.containsValue(field.getType()));
				if ((notColumn != null) || (isStatic) || (isFinal) || (!isPrimitive)) {
					continue;
				}
				String fieldName = field.getName();
				String column = fieldName.replaceAll("([A-Z])", "_$1").toLowerCase();
				fieldMap.put(fieldName, column);
			}
		}
	}

	public static String returnInsertColumnsName(Serializable obj) {
		StringBuilder sb = new StringBuilder();

		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		Iterator<String> iterator = fieldMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String fieldname = (String) iterator.next();
			if ((isNull(obj, fieldname)) && (!fieldname.contains("createTime")) && (!fieldname.contains("updateTime")))
				continue;
			if (i++ != 0) {
				sb.append(',');
			}
			sb.append((String) fieldMap.get(fieldname));
		}
		return sb.toString();
	}

	public static String returnInsertColumnsDefine(Serializable obj) {
		StringBuilder sb = new StringBuilder();

		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		Iterator<String> iterator = fieldMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String fieldname = (String) iterator.next();
			boolean isTime = (fieldname.equalsIgnoreCase("createTime")) || (fieldname.equalsIgnoreCase("updateTime"));
			if ((!isTime) && (isNull(obj, fieldname)))
				continue;
			if (i++ != 0) {
				sb.append(',');
			}
			if (isTime)
				sb.append("NOW()");
			else {
				sb.append("#{").append(fieldname).append('}');
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String returnUpdateSetFull(Serializable obj) {
		StringBuilder sb = new StringBuilder();

		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		int i = 0;
		for (Map.Entry column : fieldMap.entrySet()) {
			boolean isUpdateTime = ((String) column.getKey()).equalsIgnoreCase("updateTime");
			boolean isCreateTime = ((String) column.getKey()).equalsIgnoreCase("createTime");
			if (i++ != 0)
				sb.append(',');
			if (isUpdateTime)
				sb.append("update_time=NOW()");
			else if ((isCreateTime) && (isNull(obj, (String) column.getKey())))
				sb.append("create_time=NOW()");
			else {
				sb.append((String) column.getValue()).append("=#{").append((String) column.getKey()).append('}');
			}
		}

		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String returnUpdateSet(Serializable obj) {
		StringBuilder sb = new StringBuilder();

		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		int i = 0;
		for (Map.Entry column : fieldMap.entrySet()) {
			String key = (String) column.getKey();
			boolean isUpdateTime = key.equalsIgnoreCase("updateTime");
			if ((isNull(obj, key)) && (!isUpdateTime)) {
				continue;
			}
			if (i++ != 0)
				sb.append(',');
			if (isUpdateTime)
				sb.append("update_time=NOW()");
			else {
				sb.append((String) column.getValue()).append("=#{").append((String) column.getKey()).append('}');
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String whereColumnNotNull(Serializable obj) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		int i = 0;
		for (Map.Entry column : fieldMap.entrySet()) {
			if (isNull(obj, (String) column.getKey()))
				continue;
			if (i++ != 0)
				sb.append(" AND ");
			sb.append((String) column.getValue()).append("=#{").append((String) column.getKey() + "}");
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String whereColumn(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Map.Entry column : param.entrySet()) {
			if (i++ != 0)
				sb.append(" AND ");
			if (!MybatisEnum.MYBATIS_SPECIAL_STRING.list().contains(((String) column.getKey()).toUpperCase()))
				sb.append(((String) column.getKey()).replaceAll("([A-Z])", "_$1").toLowerCase()).append("=#{")
						.append((String) column.getKey() + "}");
			else if (MybatisEnum.MYBATIS_SPECIAL_STRING.LIKE.name().equalsIgnoreCase((String) column.getKey()))
				sb.append(column.getValue());
			else if (MybatisEnum.MYBATIS_SPECIAL_STRING.COLUMNS.name().equalsIgnoreCase((String) column.getKey())) {
				sb.append(column.getValue());
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String whereColumnNotEmpty(Map<String, Object> param) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Map.Entry column : param.entrySet()) {
			if (column.getValue() == null)
				continue;
			if (i++ != 0)
				sb.append(" AND ");
			if (!MybatisEnum.MYBATIS_SPECIAL_STRING.list().contains(((String) column.getKey()).toUpperCase()))
				sb.append(((String) column.getKey()).replaceAll("([A-Z])", "_$1").toLowerCase()).append("=#{")
						.append((String) column.getKey() + "}");
			else if (MybatisEnum.MYBATIS_SPECIAL_STRING.LIKE.name().equalsIgnoreCase((String) column.getKey()))
				sb.append(column.getValue());
			else if (MybatisEnum.MYBATIS_SPECIAL_STRING.COLUMNS.name().equalsIgnoreCase((String) column.getKey())) {
				sb.append(column.getValue());
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String queryColumn(Serializable obj) {
		StringBuilder sb = new StringBuilder();

		Map<String, String> fieldMap = columnMap.get(obj.getClass());
		int i = 0;
		for (Map.Entry column : fieldMap.entrySet()) {
			if (i++ != 0)
				sb.append(',');
			sb.append((String) column.getValue()).append(" as ").append((String) column.getKey());
		}
		return sb.toString();
	}

	public static String objString(Serializable obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Field f : fields) {
			if ((Modifier.isStatic(f.getModifiers())) || (Modifier.isFinal(f.getModifiers())))
				continue;
			Object value = null;
			try {
				f.setAccessible(true);
				value = f.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (value != null)
				sb.append(f.getName()).append('=').append(value).append(',');
		}
		sb.append(']');

		return sb.toString();
	}

	public class WhereColumn {
		public String name;
		public boolean isString;

		public WhereColumn(String name, boolean isString) {
			this.name = name;
			this.isString = isString;
		}
	}
}