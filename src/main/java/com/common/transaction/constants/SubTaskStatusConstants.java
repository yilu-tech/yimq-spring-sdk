package com.common.transaction.constants;

/**
 * create by gaotiedun ON 2020/4/1 15:37
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class SubTaskStatusConstants {

    public static final Integer PREPARING = 10;     //准备中状态

    public static final Integer PREPARED = 20;      //已准备状态

    public static final Integer DOING = 30;         //执行中状态

    public static final Integer DONE = 40;          //执行结束状态

    public static final Integer CANCELLING = -30;   //终止中状态

    public static final Integer CANCELED = -40;     //已终止状态

}
