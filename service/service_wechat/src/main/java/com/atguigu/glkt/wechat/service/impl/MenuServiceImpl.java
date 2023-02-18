package com.atguigu.glkt.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.ggkt.model.wechat.Menu;
import com.atguigu.ggkt.vo.wechat.MenuVo;
import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.wechat.mapper.MenuMapper;
import com.atguigu.glkt.wechat.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单明细 订单明细 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-15
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Resource
    private WxMpService wxMpService;

    //获取所有一级菜单
    @Override
    public List<Menu> findMenuOneInfo() {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", 0);
        return baseMapper.selectList(wrapper);
    }

    //获取所有菜单，包括一级和二级菜单
    @Override
    public List<MenuVo> findMenuInfo() {
        //1 创建list集合，用户最终数据封装
        List<MenuVo> finalMenuList = new ArrayList<>();
        //2 查询所有菜单数据（包括第一级和第二级）
        List<Menu> menuList = baseMapper.selectList(null);
        //从menuList中取得一级菜单数据
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());
        for (Menu menu : oneMenuList) {
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(menu, oneMenuVo);
            List<Menu> twoMenuList = menuList.stream()
                    .filter(item -> item.getParentId().equals(menu.getId()))
                    .collect(Collectors.toList());
            ArrayList<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);
            finalMenuList.add(oneMenuVo);
        }
        return finalMenuList;
    }


    //同步公众号菜单
    @Override
    public void syncMenu() {
        //获取所有的菜单数据
        List<MenuVo> menuInfo = findMenuInfo();
        //封装button里面结构 数组格式
        JSONArray buttonList = new JSONArray();
        for (MenuVo menuVo : menuInfo) {
            //JSON封装一级菜单
            JSONObject one = new JSONObject();
            one.put("name", menuVo.getName());
            //JSON封装二级菜单
            JSONArray sub_button = new JSONArray();
            for (MenuVo twoMenuVo : menuVo.getChildren()) {
                JSONObject view = new JSONObject();
                view.put("type", twoMenuVo.getType());
                if(twoMenuVo.getType().equals("view")) {
                    view.put("name", twoMenuVo.getName());
                    view.put("url", "http://zxc.vipgz4.91tunnel.com/#"
                            +twoMenuVo.getUrl());
                } else {
                    view.put("name", twoMenuVo.getName());
                    view.put("key", twoMenuVo.getMeunKey());
                }
                sub_button.add(view);
            }
            one.put("sub_button", sub_button);
            buttonList.add(one);
        }
        //封装最外层button部分
        JSONObject buttton = new JSONObject();
        buttton.put("button", buttonList);
        try {
            wxMpService.getMenuService().menuCreate(buttton.toJSONString());
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new GlktException(20001, "同步菜单失败");
        }
    }

    //删除公众号菜单
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new GlktException(20001, "删除菜单失败");
        }
    }
}
