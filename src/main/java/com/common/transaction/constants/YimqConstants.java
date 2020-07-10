package com.common.transaction.constants;

/**
 * create by gaotiedun ON 2020/3/24 17:35
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class YimqConstants {

    public static final String TRY = "TRY";         //尝试

    public static final String CONFIRM = "CONFIRM"; //确认

    public static final String CANCEL = "CANCEL";   //取消

    public static final String MESSAGE_CHECK = "MESSAGE_CHECK";     //消息审核

    public static final String ACTOR_CLEAR = "ACTOR_CLEAR";       //消息清理

    public static final String GET_CONFIG = "GET_CONFIG";           //获取服务配置信息

    public static final String XA = "XA";           //XA事务类型

    public static final String TCC = "TCC";         //TCC事务类型

    public static final String EC = "EC";           //EC事务类型

    public static final String BCST = "BCST";       //BCST事务类型

    public static final Integer EC_TYPE = 10;       // EC事务类型码

    public static final Integer TCC_TYPE = 20;      //TCC事务类型码

    public static final Integer XA_TYPE = 30;       //XA事务类型码

    public static final Integer BCST_TYPE = 60;     //BCST事务类型码

    public static final Integer PENDING = 0;        //TCC事务的Pending状态

    public static final Integer ACTIVE = 1;         //TCC事务的


}
