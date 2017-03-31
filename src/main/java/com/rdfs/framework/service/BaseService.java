package com.rdfs.framework.service;

import java.util.List;
import java.util.Map;

import com.rdfs.framework.page.PageInfo;
import com.rdfs.framework.page.RemotePage;

public abstract interface BaseService<T> {
	
  public abstract T insert(T paramT);

  public abstract int updateNotNullById(T paramT);

  public abstract int updateById(T paramT);

  public abstract int deleteById(Number paramNumber);

  public abstract int deleteByObject(T paramT);

  public abstract int deleteByParamNotEmpty(Map<String, Object> paramMap);

  public abstract int deleteByParam(Map<String, Object> paramMap);

  public abstract T queryById(Number paramNumber);

  public abstract List<T> queryByObject(T paramT);

  public abstract T queryUniqueByObject(T paramT);

  public abstract T queryUniqueByParams(Map<String, Object> paramMap);

  public abstract List<T> queryByParamNotEmpty(Map<String, Object> paramMap);

  public abstract List<T> queryByParam(Map<String, Object> paramMap);

  public abstract Integer queryByObjectCount(T paramT);

  public abstract Integer queryByParamNotEmptyCount(Map<String, Object> paramMap);

  public abstract Integer queryByParamCount(Map<String, Object> paramMap);

  public abstract RemotePage queryPageByObject(T paramT, PageInfo paramPageInfo);

  public abstract RemotePage queryPageByParamNotEmpty(Map<String, Object> paramMap, PageInfo paramPageInfo);

  public abstract RemotePage queryPageByParam(Map<String, Object> paramMap, PageInfo paramPageInfo);
}