package com.common.transaction.dao;

import com.common.transaction.entity.MessageEntity;
import org.springframework.stereotype.Repository;

/**
 * create by gaotiedun ON 2020/3/31 14:06
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface MessageDao {

    MessageEntity selectMessageById(Integer messageId);

    MessageEntity selectMessageByIdForUpdate(Integer messageId);

    int saveOrUpdateMessage(MessageEntity messageEntity);

    int deleteMessageByMessageId(Integer messageId);

}
