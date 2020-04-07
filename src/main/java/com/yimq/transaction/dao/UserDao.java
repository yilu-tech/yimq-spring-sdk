package com.yimq.transaction.dao;

import com.yimq.transaction.entity.UserEntity;
import org.springframework.stereotype.Repository;

/**
 * create by gaotiedun ON 2020/3/25 17:30
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@Repository
public interface UserDao {

    int saveOrUpdateUser(UserEntity userEntity);

    UserEntity selectUserByIdForUpdate(Integer userId);

    int deleteUserById(Integer userId);

}
