package com.atguigu.glkt.vod.service;

import java.util.Map;

public interface VodService {
    //上传视频
    String uploadVideo();

    //删除视频
    void removeVideo(String fileId);

    //点播视频播放接口
    Map<String, Object> getPlayAuth(Long courseId, Long videoId);
}
