package com.atguigu.glkt.order.service.impl;

import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.order.OrderDetail;
import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vo.order.OrderFormVo;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.atguigu.ggkt.vo.order.OrderInfoVo;
import com.atguigu.glkt.client.activity.CouponInfoFeignClient;
import com.atguigu.glkt.client.course.CourseFeignClient;
import com.atguigu.glkt.client.user.UserInfoFeignClient;
import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.order.mapper.OrderInfoMapper;
import com.atguigu.glkt.order.service.OrderDetailService;
import com.atguigu.glkt.order.service.OrderInfoService;
import com.atguigu.glkt.utils.AuthContextHolder;
import com.atguigu.glkt.utils.OrderNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private UserInfoFeignClient userInfoFeignClient;

    @Resource
    private CouponInfoFeignClient couponInfoFeignClient;

    //订单列表
    @Override
    public Map<String, Object> selelctOrderInfoPage(Page<OrderInfo> pageParam,
                                                    OrderInfoQueryVo orderInfoQueryVo) {
        //获取查询条件
        Long userId = orderInfoQueryVo.getUserId();
        String outTradeNo = orderInfoQueryVo.getOutTradeNo();
        String phone = orderInfoQueryVo.getPhone();
        String createTimeEnd = orderInfoQueryVo.getCreateTimeEnd();
        String createTimeBegin = orderInfoQueryVo.getCreateTimeBegin();
        Integer orderStatus = orderInfoQueryVo.getOrderStatus();

        //条件值非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(userId)) {
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(outTradeNo)) {
            wrapper.eq("out_trade_no",outTradeNo);
        }
        if(!StringUtils.isEmpty(phone)) {
            wrapper.eq("phone",phone);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper中的方法进行分页查询
        Page<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        long totalCount = pages.getTotal();
        long pageCount = pages.getPages();
        List<OrderInfo> records = pages.getRecords();
        //封装详情信息，
        records.forEach(this::getDetailofOrder);
        //放入map中返回
        Map<String,Object> map = new HashMap<>();
        map.put("total",totalCount);
        map.put("pageCount",pageCount);
        map.put("records",records);
        return map;
    }
    //生成订单方法
    @Override
    public Long submitOrder(OrderFormVo orderFormVo) {
        //1 获取生成订单条件值
        Long courseId = orderFormVo.getCourseId();
        Long couponId = orderFormVo.getCouponId();
        Long userId = AuthContextHolder.getUserId();
        //2 判断当前用户是否已经生成订单 (用户id和课程id一起判断)
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getCourseId, courseId);
        wrapper.eq(OrderDetail::getUserId, userId);
        OrderDetail orderDetail1 = orderDetailService.getOne(wrapper);
        if (orderDetail1 != null) {
            return orderDetail1.getOrderId();
        }
        //3 根据课程id查询课程信息
        Course course = courseFeignClient.getById(courseId);
        if (course == null) {
            throw new GlktException(20001, "课程不存在");
        }
        //查询用户信息
        UserInfo userInfo = userInfoFeignClient.getById(userId);
        if (userInfo == null) {
            throw new GlktException(20001, "用户不存在");
        }

        //优惠券金额
        BigDecimal couponReduce = new BigDecimal(0);
        if(null != couponId) {
            CouponInfo couponInfo = couponInfoFeignClient.getById(couponId);
            couponReduce = couponInfo.getAmount();
        }

        //创建订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userInfo.getNickName());
        orderInfo.setPhone(userInfo.getPhone());
        orderInfo.setProvince(userInfo.getProvince());
        orderInfo.setOriginAmount(course.getPrice());
        orderInfo.setCouponReduce(couponReduce);
        orderInfo.setFinalAmount(orderInfo.getOriginAmount().subtract(orderInfo.getCouponReduce()));
        orderInfo.setOutTradeNo(OrderNoUtils.getOrderNo());
        orderInfo.setTradeBody(course.getTitle());
        orderInfo.setOrderStatus("0");
        this.save(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        orderDetail.setUserId(userId);
        orderDetail.setCourseId(courseId);
        orderDetail.setCourseName(course.getTitle());
        orderDetail.setCover(course.getCover());
        orderDetail.setOriginAmount(course.getPrice());
        orderDetail.setCouponReduce(new BigDecimal(0));
        orderDetail.setFinalAmount(orderDetail.getOriginAmount().subtract(orderDetail.getCouponReduce()));
        orderDetailService.save(orderDetail);

        //更新优惠券状态
        if(null != orderFormVo.getCouponUseId()) {
            couponInfoFeignClient.updateCouponInfoUseStatus(orderFormVo.getCouponUseId(), orderInfo.getId());
        }

        return orderInfo.getId();
    }

    //根据订单id获取订单信息
    @Override
    public OrderInfoVo getOrderInfoVoById(Long id) {
        OrderInfo orderInfo = this.getById(id);
        OrderDetail orderDetail = orderDetailService.getById(id);

        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setCourseId(orderDetail.getCourseId());
        orderInfoVo.setCourseName(orderDetail.getCourseName());
        return orderInfoVo;
    }

    @Override
    public void updateOrderStatus(String outTradeNo) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOutTradeNo, outTradeNo);
        OrderInfo orderInfo = baseMapper.selectOne(wrapper);

        orderInfo.setOrderStatus("1");

        baseMapper.updateById(orderInfo);
    }

    private void getDetailofOrder(OrderInfo orderInfo) {
        Long id = orderInfo.getId();
        OrderDetail orderDetail = orderDetailService.getById(id);
        if (!StringUtils.isEmpty(orderDetail)) {
            String courseName = orderDetail.getCourseName();
            orderInfo.getParam().put("courseName", courseName);
        }
    }

}
