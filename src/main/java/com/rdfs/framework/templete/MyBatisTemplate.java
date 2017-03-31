package com.rdfs.framework.templete;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rdfs.framework.enums.MybatisEnum;
import com.rdfs.framework.utils.DtoUtil;

public class MyBatisTemplate<T extends Serializable> {
	private static final Logger log = LoggerFactory.getLogger(MyBatisTemplate.class);

	public String insert(T obj) {
		SqlBuilder.BEGIN();
		SqlBuilder.INSERT_INTO(DtoUtil.tableName(obj));
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.VALUES(DtoUtil.returnInsertColumnsName(obj), DtoUtil.returnInsertColumnsDefine(obj));
		return SqlBuilder.SQL();
	}

	public String updateById(T obj) {
		String idname = DtoUtil.id(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.UPDATE(DtoUtil.tableName(obj));
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.SET(DtoUtil.returnUpdateSetFull(obj));
		WHEREID(idname);
		return SqlBuilder.SQL();
	}

	public String updateNotNullById(T obj) {
		String idname = DtoUtil.id(obj);

		SqlBuilder.BEGIN();

		SqlBuilder.UPDATE(DtoUtil.tableName(obj));
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.SET(DtoUtil.returnUpdateSet(obj));
		WHEREID(idname);
		return SqlBuilder.SQL();
	}

	public String deleteById(T obj) {
		String idname = DtoUtil.id(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.DELETE_FROM(DtoUtil.tableName(obj));
		WHEREID(idname);
		return SqlBuilder.SQL();
	}

	public String deleteByObject(T obj) {
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.DELETE_FROM(DtoUtil.tableName(obj));
		SqlBuilder.WHERE(DtoUtil.whereColumnNotNull(obj));
		return SqlBuilder.SQL();
	}

	public String deleteByParamNotEmpty(Map<String, Object> param) {
		removeEmpty(param);
		SqlBuilder.BEGIN();
		Serializable obj = (Serializable) param.get("SPACE_TABLE_NAME");
		String limit = "";
		if (param.containsKey(MybatisEnum.MYBATIS_SPECIAL_STRING.LIMIT.name())) {
			limit = addlimit(param);
		}
		SqlBuilder.DELETE_FROM(DtoUtil.tableName(obj));
		param.remove("SPACE_TABLE_NAME");
		if ((param != null) && (param.values() != null) && (param.values().size() > 0))
			SqlBuilder.WHERE(DtoUtil.whereColumnNotEmpty(param));
		return SqlBuilder.SQL() + limit;
	}

	public String deleteByParam(Map<String, Object> param) {
		SqlBuilder.BEGIN();
		Serializable obj = (Serializable) param.get("SPACE_TABLE_NAME");
		String limit = "";
		if (param.containsKey(MybatisEnum.MYBATIS_SPECIAL_STRING.LIMIT.name())) {
			limit = addlimit(param);
		}
		SqlBuilder.DELETE_FROM(DtoUtil.tableName(obj));
		param.remove("SPACE_TABLE_NAME");
		if ((param != null) && (param.values() != null) && (param.values().size() > 0))
			SqlBuilder.WHERE(DtoUtil.whereColumn(param));
		return SqlBuilder.SQL() + limit;
	}

	public String queryById(T obj) {
		String idname = DtoUtil.id(obj);
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
		SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
		WHEREID(idname);
		return SqlBuilder.SQL();
	}

	public String queryByObject(T obj) {
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
		SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
		if (!"".equals(DtoUtil.whereColumnNotNull(obj))) {
			SqlBuilder.WHERE(DtoUtil.whereColumnNotNull(obj));
		}
		return SqlBuilder.SQL();
	}

	public String queryByParamNotEmpty(Map<String, Object> param) {
		try {
			removeEmpty(param);
			Serializable obj = (Serializable) param.get("SPACE_TABLE_NAME");
			param.remove("SPACE_TABLE_NAME");
			Object orderBy = param.get(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			DtoUtil.caculationColumnList(obj);
			SqlBuilder.BEGIN();
			SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
			SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
			if (!param.isEmpty())
				SqlBuilder.WHERE(DtoUtil.whereColumnNotEmpty(param));
			if (orderBy != null)
				SqlBuilder.ORDER_BY(orderBy.toString());
			String sql = SqlBuilder.SQL();
			log.debug(sql);
			return sql;
		} catch (Exception e) {
			log.error("出错了 ！" + param);
			e.printStackTrace();
		}
		return null;
	}

	public String queryByParam(Map<String, Object> param) {
		try {
			Serializable obj = (Serializable) param.get("SPACE_TABLE_NAME");
			Object orderBy = param.get(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			param.remove("SPACE_TABLE_NAME");
			param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			DtoUtil.caculationColumnList(obj);
			SqlBuilder.BEGIN();
			SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
			SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
			if (!param.isEmpty())
				SqlBuilder.WHERE(DtoUtil.whereColumn(param));
			if (orderBy != null)
				SqlBuilder.ORDER_BY(orderBy.toString());
			String sql = SqlBuilder.SQL();

			return sql;
		} catch (Exception e) {
			log.error("出错了！" + param);
			e.printStackTrace();
		}
		return null;
	}

	public String queryByObjectCount(T obj) {
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.SELECT(" count(*) total ");
		SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
		SqlBuilder.WHERE(DtoUtil.whereColumnNotNull(obj));
		return SqlBuilder.SQL();
	}

	public String queryByParamNotEmptyCount(Map<String, Object> param) {
		try {
			removeEmpty(param);
			Serializable obj = (Serializable) param.remove("SPACE_TABLE_NAME");
			DtoUtil.caculationColumnList(obj);
			SqlBuilder.BEGIN();
			SqlBuilder.SELECT(" count(*) total ");
			SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
			Object orderBy = param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			if (!param.isEmpty())
				SqlBuilder.WHERE(DtoUtil.whereColumn(param));
			param.put(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name(), orderBy);
			String sql = SqlBuilder.SQL();
			log.debug(sql);
			return sql;
		} catch (Exception e) {
			log.error("出错了！" + param);
			e.printStackTrace();
		}
		return null;
	}

	public String queryByParamCount(Map<String, Object> param) {
		try {
			Serializable obj = (Serializable) param.get("SPACE_TABLE_NAME");
			param.remove("SPACE_TABLE_NAME");
			DtoUtil.caculationColumnList(obj);
			SqlBuilder.BEGIN();
			SqlBuilder.SELECT(" count(*) total ");
			SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
			Object orderBy = param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
			if (!param.isEmpty())
				SqlBuilder.WHERE(DtoUtil.whereColumn(param));
			param.put(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name(), orderBy);
			String sql = SqlBuilder.SQL();
			log.debug(sql);
			return sql;
		} catch (Exception e) {
			log.error("出错了！" + param);
			e.printStackTrace();
		}
		return null;
	}

	public String queryPageByParamNotEmpty(Map<String, Object> param) {
		removeEmpty(param);
		Serializable obj = (Serializable) param.remove("SPACE_TABLE_NAME");
		String limit = addlimit(param);
		Object orderBy = param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
		SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
		if (!param.isEmpty())
			SqlBuilder.WHERE(DtoUtil.whereColumnNotEmpty(param));
		if (orderBy != null) {
			SqlBuilder.ORDER_BY(orderBy.toString());
		}
		return SqlBuilder.SQL() + limit;
	}

	public String queryPageByParam(Map<String, Object> param) {
		Serializable obj = (Serializable) param.remove("SPACE_TABLE_NAME");
		String limit = addlimit(param);
		Object orderBy = param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.ORDER_BY.name());
		DtoUtil.caculationColumnList(obj);
		SqlBuilder.BEGIN();
		SqlBuilder.SELECT(DtoUtil.queryColumn(obj));
		SqlBuilder.FROM(DtoUtil.tableName(obj) + " " + obj.getClass().getSimpleName());
		if (!param.isEmpty())
			SqlBuilder.WHERE(DtoUtil.whereColumn(param));
		if (orderBy != null) {
			SqlBuilder.ORDER_BY(orderBy.toString());
		}
		return SqlBuilder.SQL() + limit;
	}

	public void WHEREID(String idname) {
		SqlBuilder.WHERE(idname.replaceAll("([A-Z])", "_$1").toLowerCase() + "=#{" + idname + "}");
	}

	public String addlimit(Map<String, Object> param) {
		String subsql = " " + MybatisEnum.MYBATIS_SPECIAL_STRING.LIMIT.name() + " ";
		Object obj = param.remove(MybatisEnum.MYBATIS_SPECIAL_STRING.LIMIT.name());
		if (obj == null)
			subsql = subsql + "0,10";
		else {
			subsql = subsql + obj;
		}

		return subsql;
	}

	private void removeEmpty(Map<String, Object> params) {
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (params.get(key) == null) {
				params.remove(key);
				iterator = params.keySet().iterator();
			}
		}
	}
}