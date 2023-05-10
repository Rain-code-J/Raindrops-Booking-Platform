package com.rain.yygh.oss.controller;

import com.rain.yygh.common.result.R;
import com.rain.yygh.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags="阿里云文件管理")
@RestController
@RequestMapping("/user/oss/file")
public class OssController {

    @Autowired
    private OssService ossService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file){
        String uploadUrl = ossService.upload(file);
        return R.ok().message("文件上传成功").data("url", uploadUrl);
    }

}
