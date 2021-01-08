package com.common.transaction.dao;

import com.common.transaction.entity.ProcessesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * create by gaotiedun ON 2020/4/8 10:01
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface ProcessDao {

    ProcessesEntity selectProcessByIdForUpdateSkipLocked(BigInteger id);

    int insertProcess(ProcessesEntity processesEntity);

    int updateProcess(ProcessesEntity processesEntity);

    ProcessesEntity selectProcessById(BigInteger id);

    ProcessesEntity selectProcessByIdForUpdate(BigInteger id);

    List<ProcessesEntity> selectProcessForUpdate(List<BigInteger> processIdList, List<Integer> processStatusList);

    int clearProcedure(String type, @Param("canClearProcessId") String canClearProcessId);
}
