package com.yey.kindergaten.bean;

/**
 * 含有图片目录。<br/>
 * <br/>
 * Created by yanglw on 2014/8/15.
 */
public class Dir extends PhotoBase
{
    /** 目录名称 */
    public String name;
    /** 该目录中图片的个数（不包含子文件夹中的图片个数） */
    public int length;
}
