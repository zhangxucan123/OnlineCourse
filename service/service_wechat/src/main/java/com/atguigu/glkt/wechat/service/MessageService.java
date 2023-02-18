package com.atguigu.glkt.wechat.service;

import java.util.Map;

public interface MessageService {
    //接受微信服务器发送来的消息
    String receiveMessage(Map<String, String> param);

    void pushPayMessage(long l);
}
