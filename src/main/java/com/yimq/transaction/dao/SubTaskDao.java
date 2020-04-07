package com.yimq.transaction.dao;

import com.yimq.transaction.entity.SubTaskEntity;
import org.springframework.stereotype.Repository;

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

    SubTaskEntity selectSubTaskByIdForUpdate(Integer id);

    SubTaskEntity selectSubTaskById(Integer id);

}
