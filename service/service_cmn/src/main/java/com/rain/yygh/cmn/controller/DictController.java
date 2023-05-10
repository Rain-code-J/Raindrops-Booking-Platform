package com.rain.yygh.cmn.controller;


import com.rain.yygh.cmn.service.DictService;
import com.rain.yygh.common.result.R;
import com.rain.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author rain
 * @since 2023-04-25
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("/findChilds/{parentId}")
    public R findChilds(@PathVariable("parentId") Long parentId){
        List<Dict> dictList = dictService.findChilds(parentId);
        return R.ok().data("dictList",dictList);
    }

    @ApiOperation(value = "导出文件")
    @GetMapping("/download")
    public void download(HttpServletResponse response){
        dictService.download(response);
    }

    @ApiOperation(value = "导入文件")
    @PostMapping("/upload")
    public R upload(MultipartFile file){
        dictService.upload(file);
        return R.ok();
    }

    @ApiOperation("根据省市区编号获取省市区名字")
    @GetMapping("/{value}")
    public String getRegionNameByValue(@PathVariable(value = "value") Long value){
        return dictService.getRegionNameByValue(value);
    }

    @ApiOperation("根据医院等级编号获取医院等级信息")
    @GetMapping("/{dictCode}/{value}")
    public String getNameByValueAndDictCode(
            @PathVariable(value = "dictCode") String dictCode,
            @PathVariable(value = "value") Long value){
        return dictService.getNameByValueAndDictCode(dictCode,value);
    }
}

