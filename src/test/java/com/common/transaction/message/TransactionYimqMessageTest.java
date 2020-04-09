package com.common.transaction.message;

import com.common.transaction.client.YIMQClient;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * create by gaotiedun ON 2020/4/2 11:29
 *
 * @version v2.0
 * Description :
 * Updated Date      by
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class TransactionYimqMessageTest {
    @Resource
    private TransactionYimqMessage transactionMessage;

    @Resource
    private YIMQClient client;

    /*@Ignore
    @Test
    void messageCommit() {
        transactionMessage.topic = "user.create";
        try {
            transactionMessage.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        transactionMessage.commit();
    }*/

    /*@Ignore
    @Test
    void addEcSubTaskAndPrepareTest() {
        transactionMessage.topic = "content.update";
        try {
            EcSubTask ecSubTask = client.ec("content@content.change");
            JSONObject param1 = new JSONObject();
            param1.put("title","new title1");
            ecSubTask.data(param1.toJSONString());
            ecSubTask.join();
            EcSubTask ecSubTask1 = client.ec("content@content.change");
            JSONObject param2 = new JSONObject();
            param2.put("title","new title2");
            ecSubTask1.data(param2.toJSONString());
            ecSubTask1.join();
            transactionMessage.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}