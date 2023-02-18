package com.atguigu.glkt.order.service.impl;

import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.order.service.WXPayService;
import com.atguigu.glkt.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WXPayServiceImpl implements WXPayService {

    //微信支付
    @Override
    public Map<String, String> createJsapi(String orderNo){
        Map<String, String> paramMap = new HashMap<>();
        //1、设置参数
        paramMap.put("appid", "wx700c0f0276a176f9");
        paramMap.put("mch_id", "1481962542");
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", "test");
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("total_fee", "1");
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", "http://glkt.atguigu.cn/api/order/wxPay/notify");
        paramMap.put("trade_type", "JSAPI");

        /**
         * 设置参数值当前微信用户openid
         *
         */
//			paramMap.put("openid", "o1R-t5trto9c5sdYt6l1ncGmY5Y");
        //UserInfo userInfo = userInfoFeignClient.getById(paymentInfo.getUserId());
//			paramMap.put("openid", "oepf36SawvvS8Rdqva-Cy4flFFg");
        paramMap.put("openid", "oQTXC56lAy3xMOCkKCImHtHoLL");

        try {
            HttpClientUtils client = new HttpClientUtils("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String xml = WXPayUtil.generateSignedXml(paramMap, "MXb72b9RfshXZD4FRGV5KLqmv5bx9LT9");
            client.setXmlParam(xml);
            client.setHttps(true);
            client.post();

            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            System.out.println("xml:" + resultMap);
            if(null != resultMap.get("result_code")  && !"SUCCESS".equals(resultMap.get("result_code"))) {
                throw new GlktException(20001, "支付失败");
            }
            //4、再次封装参数
            Map<String, String> parameterMap = new HashMap<>();
            String prepayId = String.valueOf(resultMap.get("prepay_id"));
            String packages = "prepay_id=" + prepayId;
            parameterMap.put("appId", "wxf913bfa3a2c7eeeb");
            parameterMap.put("nonceStr", resultMap.get("nonce_str"));
            parameterMap.put("package", packages);
            parameterMap.put("signType", "MD5");
            parameterMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            String sign = WXPayUtil.generateSignature(parameterMap, "MXb72b9RfshXZD4FRGV5KLqmv5bx9LT9");

            //返回结果
            Map<String, String> result = new HashMap();
            result.put("appId", "wx700c0f0276a176f9");
            result.put("timeStamp", parameterMap.get("timeStamp"));
            result.put("nonceStr", parameterMap.get("nonceStr"));
            result.put("signType", "MD5");
            result.put("paySign", sign);
            result.put("package", packages);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return paramMap;
    }

    //根据订单号调用微信接口查询支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        //1、封装参数
        Map paramMap = new HashMap<>();
        paramMap.put("appid", "");
        paramMap.put("mch_id", "");
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //2、设置请求
        try {
            HttpClientUtils client = new HttpClientUtils("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ""));
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map
            //7、返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
