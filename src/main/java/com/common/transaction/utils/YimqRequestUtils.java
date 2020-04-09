package com.common.transaction.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author gaotiedun
 * Created Date 2019/06/24 16:41
 * Updated Date      by
 * @version v2.0
 * Description
 */
public class YimqRequestUtils {

    /**
     *  解析httpRequest参数为Map
     * @param request 请求参数
     * @return 把http请求参数解析为Map
     */
    public static final Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> params =  WebUtils.getParametersStartingWith(request, null);
        removeEmpty(params);
        if(params.isEmpty()){
            params = (Map) JSONObject.parse(getParameterJsonStr(request));
            removeEmpty(params);
        }
        return (params == null || params.isEmpty()) ? new HashMap<>() : params;
    }


    /**
     * 移除为空的元素
     * @param params
     */
    public static final void removeEmpty(Map<String, Object> params){
        if(params == null || params.isEmpty()) return;
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Object> entry = it.next();
            String key=entry.getKey();
            if(null == params.get(key) || StringUtils.isEmpty(params.get(key).toString())){
                it.remove();
            }
        }
    }

    public static final String getParameterJsonStr(HttpServletRequest request) {
        String params = null;
        try{
            params = readRaw(request.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
        return params;
    }
    public static String readRaw(InputStream inputStream) {
        String result = "";
        try {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inputStream.close();
            result = new String(outSteam.toByteArray(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        T obj = beanClass.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }


}
