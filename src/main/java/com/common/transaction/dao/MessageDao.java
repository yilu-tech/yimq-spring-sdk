package com.common.transaction.dao;

import com.common.transaction.entity.MessageEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * create by gaotiedun ON 2020/3/31 14:06
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface MessageDao {

    MessageEntity selectMessageById(BigInteger messageId);

    MessageEntity selectMessageByIdForUpdate(BigInteger messageId);

    int saveOrUpdateMessage(MessageEntity messageEntity);

    int deleteMessageByMessageId(BigInteger messageId);

    List<MessageEntity> selectMessageListByIdForUpdate(List<BigInteger> messageIdList, Integer status);

    int deleteMessage(@Param("messageIdList") List<BigInteger> messageIdList);

}
