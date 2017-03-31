package com.rdfs.framework.page;

import java.io.Serializable;
import java.util.List;

public class EayUiPage
implements Serializable
{
private static final long serialVersionUID = -5412589223203505063L;
private List<?> rows;
private Integer total;

public EayUiPage()
{
}

public EayUiPage(RemotePage remotePage)
{
  this.rows = remotePage.getData();
  this.total = remotePage.getPage().getRecordCount();
}

public List<?> getRows() {
  return this.rows;
}

public void setRows(List<?> rows) {
  this.rows = rows;
}

public Integer getTotal() {
  return this.total;
}

public void setTotal(Integer total) {
  this.total = total;
}
}