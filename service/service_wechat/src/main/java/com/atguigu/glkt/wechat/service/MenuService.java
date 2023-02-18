package com.atguigu.glkt.wechat.service;

import com.atguigu.ggkt.model.wechat.Menu;
import com.atguigu.ggkt.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单明细 订单明细 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
public interface MenuService extends IService<Menu> {

    //获取所有的一级菜单
    List<Menu> findMenuOneInfo();

    //获取所有菜单，包括一级和二级菜单
    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();

}
