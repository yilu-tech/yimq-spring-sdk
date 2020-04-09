package com.common.transaction.dao;

import com.common.transaction.entity.ProcessesEntity;
import org.springframework.stereotype.Repository;

/**
 * create by gaotiedun ON 2020/4/8 10:01
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
