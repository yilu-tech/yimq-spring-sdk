package com.common.transaction.constants;

/**
 * create by gaotiedun ON 2020/3/31 14:12
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class MessageStatusConstants {

    public static final int PENDING = 10;

    public static final int DONE = 20;

    public static final int CANCELED = -20;

    public static final String BEGIN = "BEGIN";

    public static final String PREPARE = "PREPARE";

    public static final String COMMIT = "COMMIT";

    public static final String ROLLBACK = "ROLLBACK";

}
