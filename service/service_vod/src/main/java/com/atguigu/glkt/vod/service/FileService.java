package com.atguigu.glkt.vod.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    //文件上传方法
    String upload(MultipartFile file);
}
