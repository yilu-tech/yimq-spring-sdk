package com.yimq.transaction.dao;

import com.yimq.transaction.entity.ProcessesEntity;
import org.springframework.stereotype.Repository;

/**
 * create by gaotiedun ON 2020/3/25 16:09
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface ProcessDao {

    int saveOrUpdateProcess(ProcessesEntity processesEntity);

    ProcessesEntity selectProcessByIdForUpdate(Integer id);

    ProcessesEntity selectProcessById(Integer id);

}
