package com.atguigu.glkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.glkt.exception.GlktException;
import com.atguigu.glkt.vod.service.VideoService;
import com.atguigu.glkt.vod.service.VodService;
import com.atguigu.glkt.vod.utils.ConstantPropertiesUtil;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class VodServiceImpl implements VodService {

    @Resource
    private VideoService videoService;
    @Value("${tencent.video.appid}")
    private String appId;
    //上传视频
    @Override
    public String uploadVideo() {
        //指定当前腾讯云账号id和key
        VodUploadClient client = new VodUploadClient(ConstantPropertiesUtil.ACCESS_KEY_ID,
                ConstantPropertiesUtil.ACCESS_KEY_SECRET);
        //上传请求对象
        VodUploadRequest request = new VodUploadRequest();
        //设置视频文件在本地路径
        request.setMediaFilePath("E:\\壁纸\\001.mp4");
        request.setProcedure("LongVideoPreset");
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            //获取上传视频后的视频id
            return response.getFileId();
        } catch (Exception e) {
            // 业务方进行异常处理
            throw new GlktException(20001, "上传视频失败");
        }
    }

    //删除视频
    @Override
    public void removeVideo(String fileId) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(ConstantPropertiesUtil.ACCESS_KEY_ID, ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(fileId);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new GlktException(20001, "删除视频失败");
        }
    }

    //点播视频播放接口
    @Override
    public Map<String, Object> getPlayAuth(Long courseId, Long videoId) {
        //根据小节id获取小节对象，获取腾讯云视频id
        Video video = videoService.getById(videoId);
        if (video == null) {
            throw new GlktException(20001, "小节信息不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("videoSourceId", video.getVideoSourceId());
        map.put("appId",appId);
        return map;
    }
}
