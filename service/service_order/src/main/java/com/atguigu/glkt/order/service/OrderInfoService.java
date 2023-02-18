package com.atguigu.glkt.order.service;

import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.vo.order.OrderFormVo;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.atguigu.ggkt.vo.order.OrderInfoVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Map<String, Object> selelctOrderInfoPage(Page<OrderInfo> pageParam, OrderInfoQueryVo orderInfoQueryVo);

    Long submitOrder(OrderFormVo orderFormVo);

    OrderInfoVo getOrderInfoVoById(Long id);

    void updateOrderStatus(String out_trade_no);
}
