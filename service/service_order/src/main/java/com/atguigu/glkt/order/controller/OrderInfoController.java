package com.atguigu.glkt.order.controller;


import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.atguigu.glkt.order.service.OrderInfoService;
import com.atguigu.glkt.result.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;
    //订单的列表方法
    @GetMapping("{page}/{limit}")
    public Result listOrder(@PathVariable long page,
                            @PathVariable long limit,
                            OrderInfoQueryVo orderInfoQueryVo) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        Map<String,Object> map = orderInfoService.selelctOrderInfoPage(pageParam, orderInfoQueryVo);
        return Result.ok(map);
    }
}

