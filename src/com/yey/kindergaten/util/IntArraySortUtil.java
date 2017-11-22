package com.yey.kindergaten.util;

import java.util.Comparator;

/**
 * Created by zy on 2015/2/12.
 */

/**
 * 整形数组排序
 */
public class IntArraySortUtil implements Comparator {


    @Override
    public int compare(Object o1, Object o2) {

        int i = Integer.parseInt(String.valueOf(o1));
        int j = Integer.parseInt(String.valueOf(o2));
        if (i > j)
            return 1;
        if (i < j)
            return -1;
        return 0;
    }
}
