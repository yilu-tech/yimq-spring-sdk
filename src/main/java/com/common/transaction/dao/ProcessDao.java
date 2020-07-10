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

    int insertProcess(ProcessesEntity processesEntity);

    int updateProcess(ProcessesEntity processesEntity);

    ProcessesEntity selectProcessById(Integer id);

    ProcessesEntity selectProcessByIdForUpdate(Integer id);

    int deleteProcessById(Integer id);
}
