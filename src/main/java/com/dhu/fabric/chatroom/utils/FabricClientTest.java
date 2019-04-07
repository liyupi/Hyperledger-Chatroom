package com.dhu.fabric.chatroom.utils;

import com.dhu.fabric.chatroom.sdk.ChaincodeManager;
import org.junit.Test;

import java.util.Map;

/**
 * @Author: Yupi Li
 * @Date: 2018/8/5 22:09
 */
public class FabricClientTest {
    @Test
    public void test1() throws Exception{
        ChaincodeManager manager = FabricClient.obtain().getManager();
        String fcn = "saveMessage" ;
        String[] arguments = new String[]{"aaa","hahahaha"};
        manager.invoke(fcn, arguments);
        arguments = new String[]{"aab","hahahaha"};
        manager.invoke(fcn, arguments);
        fcn = "getAllMessage" ;
        arguments = new String[]{"aaa","aac"};
        Map map = manager.query(fcn, arguments);
        System.out.println(map);
    }

}