package com.rain.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    String upload(MultipartFile file);
}
