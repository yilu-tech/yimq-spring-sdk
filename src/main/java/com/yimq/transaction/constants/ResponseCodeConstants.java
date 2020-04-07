package com.yimq.transaction.constants;

/**
 * @author gaotiedun
 * Created Date 2019/05/21 15:35
 * Updated Date      by
 * @version v2.0
 * Description
 */

/**
 * 返回状态码常量
 */
public class ResponseCodeConstants {
    /**
     * 访问权限不足
     */
    public static final int PERMISSION_DENY = 90000;

    /**
     * 数据保存异常
     */
    public static final int DATA_SAVE_EXCEPTION = 97000;

    /**
     * 数据更新异常
     */
    public static final int DATA_UPDATE_EXCEPTION = 97001;

    /**
     * 数据查询异常
     */
    public static final int DATA_QUERY_EXCEPTION = 97002;

    /**
     * 数据删除异常
     */
    public static final int DATA_DELETE_EXCEPTION = 97003;

    /**
     * 上传文件保存失败
     */
    public static final int UPLOAD_FILE_SAVE_FAILURE = 98000;

    /**
     * 上传文件不可超出1M
     */
    public static final int UPLOAD_FILE_EXCEED1M  = 98001;

    /**
     * 上传文件不可超出2M
     */
    public static final int UPLOAD_FILE_EXCEED2M  = 98002;

    /**
     * 上传文件不可超出3M
     */
    public static final int UPLOAD_FILE_EXCEED3M  = 98003;

    /**
     * 上传文件不可超出5M
     */
    public static final int UPLOADFILEEXCEED5M  = 98005;

    /**
     * 上传文件不可超出10M
     */
    public static final int UPLOAD_FILE_EXCEED10M  = 98010;

    /**
     * 上传文件不可超出20M
     */
    public static final int UPLOAD_FILE_EXCEED20M  = 98020;

    /**
     * 上传文件格式错误
     */
    public static final int UPLOAD_FILE_FORMALERROR  = 98100;

    /**
     * 参数错误
     */
    public static final int PARAMS_ERROR  = 99995;

    /**
     * 当前APP已是最新版本
     */
    public static final int APP_IS_NEWEST  = 99996;

    /**
     * 接口未定义
     */
    public static final int INTERFACE_UNDIFINED  = 99997;

    /**
     * 非法请求
     */
    public static final int ILLEGALITY_REQUEST   = 99998;

    /**
     * 系统错误
     */
    public static final int SYSTEM_ERROR   = 99999;

    /**
     * 调用成功
     */
    public static final int SUCCESS = 0;

    public static final int HTTP_PARAM_ERROR = 422;

    //调用失败
    public static final int FAIL = -1;
}
