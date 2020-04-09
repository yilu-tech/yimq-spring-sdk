package com.common.transaction.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * create by gaotiedun ON 2020/3/30 19:31
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
public class JSONHandler <T extends Object> extends BaseTypeHandler<T> {

    private static final Logger log = Logger.getLogger(JSONHandler.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private Class<T> clazz;

    static {
        mapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public JSONHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    /**
     * object转json string
     * @param object
     * @return
     */
    private String toJSON(T object) {
        try {
            String string = mapper.writeValueAsString(object);
            log.info(">>>> json handler string:{} <<<<"+string);
            return string;
        } catch (Exception e) {
            log.error(">>>> covert object to json string failed, error message: <<<<"+e.getMessage());
        }
        return null;
    }

    /**
     * json转object
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
    private T toObject(String json, Class<T> clazz) throws IOException, JsonProcessingException {
        if (json != null && json != "") {
            return mapper.readValue(json,clazz);
        }
        return null;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        try {
            preparedStatement.setString(i,toJSON(t));
        } catch (Exception e) {
            log.error(">>>> preparedStatement set string failed, error message:{} <<<<"+e.getMessage());
        }
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        try {
            return toObject(resultSet.getString(s), clazz);
        } catch (IOException e) {
            log.error(">>>> convert json string to object failed, error message:{} <<<<"+e.getMessage());
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        try {
            return toObject(resultSet.getString(i), clazz);
        } catch (IOException e) {
            log.error(">>>> convert json string to object failed, error message:{} <<<<"+e.getMessage());
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        try {
            return toObject(callableStatement.getString(i), clazz);
        } catch (IOException e) {
            log.error(">>>> convert json string to object failed, error message:{} <<<<"+e.getMessage());
        }
        return null;
    }

}
