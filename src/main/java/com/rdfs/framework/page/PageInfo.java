package com.rdfs.framework.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

public class PageInfo implements Serializable {
	
	private static final long serialVersionUID = -6830864636737268480L;
	private static final Integer DEAFULT_PAGE_SIZE = Integer.valueOf(10);
	private static final Integer DEAFULT_PAGENO_STEP = Integer.valueOf(3);
	private Integer pageNoStep;
	private Integer reqeustPage;
	private Integer recordCount;
	private Integer pageSize;
	private Integer pageNum;
	private Integer curPage;
	private Integer showPreNo;
	private Integer showNextNo;
	private Integer showFirstNo;
	private Integer showLastNo;
	private List<Integer> showPageNoList;

	public PageInfo() {
		initPageInfo(Integer.valueOf(0), DEAFULT_PAGE_SIZE, DEAFULT_PAGENO_STEP);
	}

	public PageInfo(Integer reqeustPage) {
		initPageInfo(reqeustPage, DEAFULT_PAGE_SIZE, DEAFULT_PAGENO_STEP);
	}

	public PageInfo(Integer reqeustPage, Integer pageSize) {
		initPageInfo(reqeustPage, pageSize, DEAFULT_PAGENO_STEP);
	}

	public PageInfo(Integer reqeustPage, Integer pageSize, Integer pageNoStep) {
		initPageInfo(reqeustPage, pageSize, pageNoStep);
	}

	public void setRecordCount(Integer recordCount) {
		if (recordCount != null) {
			this.recordCount = recordCount;
			this.pageNum = Integer
					.valueOf((recordCount.intValue() + this.pageSize.intValue() - 1) / this.pageSize.intValue());
			jumpPage(this.reqeustPage);
		} else {
			this.recordCount = Integer.valueOf(0);
			this.pageNum = Integer.valueOf(1);
			jumpPage(Integer.valueOf(0));
		}
	}

	public RowBounds getPageInfo() {
		int offset = (this.curPage.intValue() - 1) * this.pageSize.intValue();
		if (offset < 0) {
			offset = 0;
		}
		RowBounds pageInfo = new RowBounds(offset, this.pageSize.intValue());
		return pageInfo;
	}

	public Integer getShowPreNo() {
		return this.showPreNo;
	}

	public Integer getShowNextNo() {
		return this.showNextNo;
	}

	public Integer getShowFirstNo() {
		return this.showFirstNo;
	}

	public Integer getShowLastNo() {
		return this.showLastNo;
	}

	public List<Integer> getShowPageNoList() {
		return this.showPageNoList;
	}

	public Integer getRecordCount() {
		return this.recordCount;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public Integer getPageNum() {
		return this.pageNum;
	}

	public Integer getCurPage() {
		return this.curPage;
	}

	public void jumpPage(Integer curPage) {
		curPage = Integer.valueOf(curPage == null ? 0 : curPage.intValue());
		if (curPage.intValue() < 1)
			curPage = Integer.valueOf(1);
		else if ((curPage.intValue() > this.pageNum.intValue()) && (this.pageNum.intValue() > 0)) {
			curPage = this.pageNum;
		}
		this.curPage = curPage;
		setPageNavigation();
	}

	private void initPageInfo(Integer reqeustPage, Integer pageSize, Integer pageNoStep) {
		this.reqeustPage = reqeustPage;
		this.pageSize = pageSize;
		this.pageNoStep = pageNoStep;
	}

	private void setPageNavigation() {
		if (this.curPage.intValue() <= 1)
			this.showPreNo = null;
		else {
			this.showPreNo = Integer.valueOf(this.curPage.intValue() - 1);
		}

		if (this.curPage.intValue() >= this.pageNum.intValue())
			this.showNextNo = null;
		else {
			this.showNextNo = Integer.valueOf(this.curPage.intValue() + 1);
		}

		if (this.pageNum.intValue() > 0) {
			this.showFirstNo = Integer.valueOf(1);
			this.showLastNo = this.pageNum;
		} else {
			this.showFirstNo = null;
			this.showLastNo = null;
		}

		this.showPageNoList = new ArrayList<>();
		for (int i = this.pageNoStep.intValue(); i > 0; i--) {
			if (this.curPage.intValue() - i > 1) {
				this.showPageNoList.add(Integer.valueOf(this.curPage.intValue() - i));
			}
		}

		if ((this.curPage.intValue() > 1) && (this.curPage.intValue() < this.pageNum.intValue())) {
			this.showPageNoList.add(this.curPage);
		}

		for (int i = 1; i < this.pageNoStep.intValue(); i++)
			if (this.curPage.intValue() + i < this.pageNum.intValue())
				this.showPageNoList.add(Integer.valueOf(this.curPage.intValue() + i));
	}

	public Integer getReqeustPage() {
		return this.reqeustPage;
	}

	public void setReqeustPage(Integer reqeustPage) {
		this.reqeustPage = reqeustPage;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}