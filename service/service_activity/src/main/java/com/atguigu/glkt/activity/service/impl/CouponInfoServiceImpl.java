package com.atguigu.glkt.activity.service.impl;

import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.activity.CouponUse;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.vo.activity.CouponUseQueryVo;
import com.atguigu.glkt.activity.mapper.CouponInfoMapper;
import com.atguigu.glkt.activity.service.CouponInfoService;
import com.atguigu.glkt.activity.service.CouponUseService;
import com.atguigu.glkt.client.user.UserInfoFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Resource
    private UserInfoFeignClient userInfoFeignClient;

    @Resource
    private CouponUseService couponUseService;

    @Override
    public IPage<CouponUse> selectCouponUsePage(Page<CouponUse> pageParam,
                                                CouponUseQueryVo couponUseQueryVo) {

        //获取条件值
        Long couponId = couponUseQueryVo.getCouponId();
        String couponStatus = couponUseQueryVo.getCouponStatus();
        String getTimeBegin = couponUseQueryVo.getGetTimeBegin();
        String getTimeEnd = couponUseQueryVo.getGetTimeEnd();
        //封装条件
        QueryWrapper<CouponUse> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(couponId)) {
            wrapper.eq("coupon_id",couponId);
        }
        if(!StringUtils.isEmpty(couponStatus)) {
            wrapper.eq("coupon_status",couponStatus);
        }
        if(!StringUtils.isEmpty(getTimeBegin)) {
            wrapper.ge("get_time",getTimeBegin);
        }
        if(!StringUtils.isEmpty(getTimeEnd)) {
            wrapper.le("get_time",getTimeEnd);
        }
        IPage<CouponUse> pageModel = couponUseService.page(pageParam, wrapper);
        List<CouponUse> records = pageModel.getRecords();
        records.forEach(this::getUserInfoBycouponUse);

        return pageModel;
    }

    @Override
    public void updateCouponInfoUseStatus(Long couponUseId, Long orderId) {
        CouponUse couponUse = new CouponUse();
        couponUse.setId(couponUseId);
        couponUse.setOrderId(orderId);
        couponUse.setCouponStatus("1");
        couponUse.setUsingTime(new Date());
        couponUseService.updateById(couponUse);
    }

    private void getUserInfoBycouponUse(CouponUse couponUse) {
        Long userId = couponUse.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            UserInfo userInfo = userInfoFeignClient.getById(userId);
            if (!StringUtils.isEmpty(userInfo)) {
                String phone = userInfo.getPhone();
                String nickName = userInfo.getNickName();
                couponUse.getParam().put("phone", phone);
                couponUse.getParam().put("nickName", nickName);
            }
        }
    }
}
