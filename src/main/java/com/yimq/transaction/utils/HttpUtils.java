package com.yimq.transaction.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaotiedun
 * Created Date 2019/06/28 21:36
 * Updated Date      by
 * @version v2.0
 * Description
 */
@Component
public class HttpUtils {

    //发过来的是json串
    public static JSONObject sendPost(String requestUrl , Map<String,Object> paramsMap) {
        //调用httpRequest方法，这个方法主要用于请求地址，并加上请求参数
        String string = httpRequest(requestUrl,paramsMap,"POST");
        //处理返回的JSON数据并返回
        JSONObject json= JSONObject.parseObject(string);
        return json;
    }

    //发过来的是json串
    public static JSONObject sendGet(String requestUrl , Map<String,Object> paramsMap) {
        //调用httpRequest方法，这个方法主要用于请求地址，并加上请求参数
        String string = httpRequest(requestUrl,paramsMap,"GET");
        //处理返回的JSON数据并返回
        JSONObject json= JSONObject.parseObject(string);
        return json;
    }

    //消息队列中的参数都是一个字符串，所以对该方法重新封装
    public static JSONObject sendPost(String requestUrl ,String jsonStr,String messageId) {
        Map<String, Object> param = new HashMap<>();
        param.put("data",jsonStr);
        param.put("messageId",messageId);
        String string = httpRequest(requestUrl,param,"POST");
        //处理返回的JSON数据并返回
        JSONObject json= JSONObject.parseObject(string);
        return json;
    }

    private static String httpRequest(String requestUrl,Map params,String requestType) {
        //buffer用于接受返回的字符
        StringBuffer buffer = new StringBuffer();
        try {
            //建立URL，把请求地址给补全，其中urlencode（）方法用于把params里的参数给取出来
            URL url = new URL(requestUrl+"?"+urlencode(params));
            //打开http连接
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setRequestProperty("auth-type", "inner");
            httpUrlConn.setRequestProperty("Content-Type", "application/json");
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod(requestType);
            httpUrlConn.connect();

            //获得输入
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //将bufferReader的值给放到buffer里
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            //关闭bufferReader和输入流
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            //断开连接
            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回字符串
        return buffer.toString();
    }

    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
