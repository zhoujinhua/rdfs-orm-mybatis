package com.rdfs.framework.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rdfs.framework.annotation.NotColumn;
import com.rdfs.framework.dao.BaseDao;
import com.rdfs.framework.enums.MybatisEnum;
import com.rdfs.framework.page.PageInfo;
import com.rdfs.framework.page.RemotePage;
import com.rdfs.framework.service.BaseService;

public abstract class BaseServiceImpl<T> implements BaseService<T> {
	public abstract BaseDao<T> getDao();

	public T insert(T obj) {
		getDao().insert(obj);
		return obj;
	}

	public int updateNotNullById(T obj) {
		return getDao().updateNotNullById(obj);
	}

	public int updateById(T obj) {
		return getDao().updateById(obj);
	}

	public int deleteById(Number id) {
		return getDao().deleteById(id);
	}

	public int deleteByObject(T obj) {
		return getDao().deleteByObject(obj);
	}

	public int deleteByParamNotEmpty(Map<String, Object> param) {
		return getDao().deleteByParamNotEmpty(param);
	}

	public int deleteByParam(Map<String, Object> param) {
		return getDao().deleteByParam(param);
	}

	public T queryById(Number id) {
		return getDao().queryById(id);
	}

	public List<T> queryByObject(T obj) {
		return getDao().queryByObject(obj);
	}

	public List<T> queryByParamNotEmpty(Map<String, Object> params) {
		return getDao().queryByParamNotEmpty(params);
	}

	public List<T> queryByParam(Map<String, Object> params) {
		return getDao().queryByParam(params);
	}

	public Integer queryByObjectCount(T obj) {
		return getDao().queryByObjectCount(obj);
	}

	public Integer queryByParamNotEmptyCount(Map<String, Object> params) {
		return getDao().queryByParamNotEmptyCount(params);
	}

	public Integer queryByParamCount(Map<String, Object> params) {
		return getDao().queryByParamCount(params);
	}

	public RemotePage queryPageByObject(T obj, PageInfo info) {
		Map<String, Object> params = getValuesByObject(obj);
		return queryPageByParamNotEmpty(params, info);
	}

	public RemotePage queryPageByParamNotEmpty(Map<String, Object> params, PageInfo info) {
		info.setRecordCount(queryByParamNotEmptyCount(params));
		setLimit(params, info);
		return new RemotePage(getDao().queryPageByParamNotEmpty(params), info);
	}

	public RemotePage queryPageByParam(Map<String, Object> params, PageInfo info) {
		info.setRecordCount(queryByParamCount(params));
		setLimit(params, info);
		return new RemotePage(getDao().queryPageByParam(params), info);
	}

	public void setLimit(Map<String, Object> params, PageInfo info) {
		Integer begin = Integer.valueOf((info.getCurPage().intValue() - 1) * info.getPageSize().intValue());
		if ((begin == null) || ((begin != null) && (begin.intValue() < 0))) {
			begin = Integer.valueOf(0);
		}
		params.put(MybatisEnum.MYBATIS_SPECIAL_STRING.LIMIT.name(), begin + "," + info.getPageSize());
	}

	protected Map<String, Object> getValuesByObject(T obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<>();
		for (Field f : fields) {
			Object notColumn = f.getAnnotation(NotColumn.class);
			if ((Modifier.isStatic(f.getModifiers())) || (Modifier.isFinal(f.getModifiers())) || (notColumn != null))
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
			if ((value != null) && ((!(value instanceof String))
					|| (((value instanceof String)) && (StringUtils.isNotBlank((String) value)))))
				map.put(f.getName(), value);
		}
		return map;
	}

	public T queryUniqueByObject(T obj) {
		List<T> list = queryByObject(obj);
		if ((list == null) || (list.isEmpty()))
			return null;
		return list.get(0);
	}

	public T queryUniqueByParams(Map<String, Object> params) {
		List<T> list = queryByParam(params);
		if ((list == null) || (list.isEmpty()))
			return null;
		return list.get(0);
	}

	protected Map<String, Object> getValuesByParamObject(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<>();
		for (Field f : fields) {
			Object notColumn = f.getAnnotation(NotColumn.class);
			if ((Modifier.isStatic(f.getModifiers())) || (Modifier.isFinal(f.getModifiers())) || (notColumn != null))
				continue;
			Object value = null;
			try {
				f.setAccessible(true);
				value = f.get(obj);
				if ((value != null) && ((value instanceof String)) && (StringUtils.isBlank(value.toString())))
					value = null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (value != null)
				map.put(f.getName(), value);
		}
		return map;
	}
}