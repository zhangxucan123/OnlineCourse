package com.atguigu.glkt.order.service;

import java.util.Map;

public interface WXPayService {
    Map<String, String> createJsapi(String orderNo);

    Map<String, String> queryPayStatus(String orderNo);
}
