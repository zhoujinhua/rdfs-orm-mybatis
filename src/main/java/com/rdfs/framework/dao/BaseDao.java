package com.rdfs.framework.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.rdfs.framework.templete.MyBatisTemplate;

public abstract interface BaseDao<T> {
	
  @InsertProvider(type=MyBatisTemplate.class, method="insert")
  @Options(useGeneratedKeys=true)
  public abstract void insert(T paramT);

  @UpdateProvider(type=MyBatisTemplate.class, method="updateNotNullById")
  public abstract int updateNotNullById(T paramT);

  @UpdateProvider(type=MyBatisTemplate.class, method="updateById")
  public abstract int updateById(T paramT);

  @DeleteProvider(type=MyBatisTemplate.class, method="deleteById")
  public abstract int deleteById(Number paramNumber);

  @DeleteProvider(type=MyBatisTemplate.class, method="deleteByObject")
  public abstract int deleteByObject(T paramT);

  @DeleteProvider(type=MyBatisTemplate.class, method="deleteByParamNotEmpty")
  public abstract int deleteByParamNotEmpty(Map<String, Object> paramMap);

  @DeleteProvider(type=MyBatisTemplate.class, method="deleteByParam")
  public abstract int deleteByParam(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryById")
  public abstract T queryById(Number paramNumber);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByObject")
  public abstract List<T> queryByObject(T paramT);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByParamNotEmpty")
  public abstract List<T> queryByParamNotEmpty(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByParam")
  public abstract List<T> queryByParam(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByObjectCount")
  public abstract Integer queryByObjectCount(T paramT);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByParamNotEmptyCount")
  public abstract Integer queryByParamNotEmptyCount(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryByParamCount")
  public abstract Integer queryByParamCount(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryPageByParamNotEmpty")
  public abstract List<T> queryPageByParamNotEmpty(Map<String, Object> paramMap);

  @SelectProvider(type=MyBatisTemplate.class, method="queryPageByParam")
  public abstract List<T> queryPageByParam(Map<String, Object> paramMap);
}