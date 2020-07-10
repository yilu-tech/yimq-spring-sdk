package com.common.transaction.constants;

/**
 * create by gaotiedun ON 2020/4/1 15:37
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class SubTaskStatusConstants {

    public static final int PREPARING = 10;     //准备中状态

    public static final int PREPARED = 20;      //已准备状态

    public static final int DOING = 30;         //执行中状态

    public static final int DONE = 40;          //执行结束状态

    public static final int CANCELLING = -30;   //终止中状态

    public static final int CANCELED = -40;     //已终止状态

}
