package com.yimq.transaction.constants;

/**
 * @author gaotiedun
 * Created Date 2019/05/17 20:22
 * Updated Date      by
 * @version v2.0
 * Description
 */
public class ResponseMessageConstants {

    public static final String SUCCESS = "正常返回";

    public static final String FAILURE = "执行失败";

    public static final String EXCEPTION = "发生异常";

    public static final String ILLEGALITY_REQUEST   = "非法请求";

    public static final String ERROR = "发生错误";

    public static final String PARAMS_ERROR = "参数错误";

    public static final String COIN_ACCOUNT_NOT_EXIST = "微币账户不存在";

    public static final String CREDITS_ACCOUNT_NOT_EXIST = "积分账户不存在";

    public static final String COIN_TYPE_UNDEFINED = "微币操作类型未定义";

    public static final String OPERATE_COIN_EXCEPTION = "操作微币账户异常";

    public static final String OPERATE_COIN_SUCCESS = "操作微币账户成功";

    public static final String OPERATE_CREDITS_EXCEPTION = "操作积分账户异常";

    public static final String OPERATE_CREDITS_SUCCESS = "操作积分账户成功";

    public static final String FREEZE_ACCOUNT_ERROR = "要解冻/冻结数目不正确";

    public static final String QUERY_LIST_IS_NULL = "查询列表结果为空";

    public static final String DATA_QUERY_EXCEPTION = "数据查询异常";
    //public static final String
    public static final String DATA_QUERY_WITHDRAW_EXCEPTION = "提现记录查询异常";

    public static final String SAVE_SUCCESS = "保存成功";

    public static final String SAVE_FAILURE = "保存失败";

    public static final String VERIFY_CODE_ERROR = "验证码验证失败";

    public static final String ROLE_IS_NULL = "用户角色ID为空";

    public static final String INIT_ACCOUNT_EXCEPTION = "初始化积分微币账户异常";

    public static final String COIN_ACCOUNT_NOT_ENOUGH = "微币账户余额不足";

    public static final String CREDITS_ACCOUNT_NOT_ENOUGH = "积分账户余额不足";

    public static final String MESSAGE_IS_CONSUMED = "该消息已经被消费";

    public static final String MESSAGE_IS_CANCELED = "该消息已经被处理";

    public static final String ORDER_IS_SETTLED = "该订单已经被结算";

    public static final String ORDER_IS_REFUNDED = "该订单已经退款";

    public static final String CHILD_TYPE_INVALID = "非法的积分微币操作类型";

    public static final String ACCOUNT_TYPE_MUST_INVEST = "必须为投资账户";

    public static final String WITHDRAW_COIN_MUST_HUNDRED_INTEGER_MULTIPLES = "提现微币数目必须为100的整数倍";

    public static final String TRADER_CODE_IS_ERROR = "交易密码错误";

    public static final String INVESTMENT_NOT_FOUND = "未找到对应的投资账户";

    public static final String USER_EXCEPTION = "用户异常";

    public static final String WIHTDRAW_CONFIG_EXCEPTION = "提现配置异常";

    public static final String WITHDRAW_TIME_NOT_ENOUGH = "剩余提现次数不足";

    public static final String WITHDRAW_AMOUNT_NOT_LIMIT = "账户微币数目未达到微币最小提现数目";

    public static final String COIN_NOT_ENOUGH = "微币余额不足";


}
