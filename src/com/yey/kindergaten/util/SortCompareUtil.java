package com.yey.kindergaten.util;

import java.util.Comparator;

import com.yey.kindergaten.bean.SchedulesBean;

@SuppressWarnings("rawtypes")
public class SortCompareUtil implements Comparator{


	

	@Override
	public int compare(Object arg0, Object arg1) {
		SchedulesBean bean0=(SchedulesBean) arg0;
		SchedulesBean bean1=(SchedulesBean) arg1;
		
		int flag=bean0.getDay().compareTo(bean1.getDay());
		return flag;
	}

}
