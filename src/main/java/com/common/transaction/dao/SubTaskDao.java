package com.common.transaction.dao;

import com.common.transaction.entity.SubTaskEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * create by gaotiedun ON 2020/4/1 15:39
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface SubTaskDao {

    int saveOrUpdateSubTask(SubTaskEntity subTaskEntity);

    SubTaskEntity selectSubTaskByIdForUpdate(BigInteger id);

    SubTaskEntity selectSubTaskById(BigInteger id);

    int deleteSubTaskByMessageId(BigInteger messageId);

    int deleteSubTask(@Param("messageIdList") List<BigInteger> messageIdList);

}
