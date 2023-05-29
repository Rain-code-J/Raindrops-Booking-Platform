package com.rain.yygh.hosp.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rain.yygh.common.result.R;
import com.rain.yygh.common.utils.MD5;
import com.rain.yygh.hosp.service.HospitalSetService;
import com.rain.yygh.model.hosp.HospitalSet;
import com.rain.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置信息")
//医院设置接口
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    //查询所有医院设置
    @ApiOperation(value = "查询所有的医院设置信息")
    @GetMapping("/findAll")
    public R findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("list", list);
    }

    // 根据医院设置id删除医院设置
    @ApiOperation(value = "根据医院设置id删除医院设置信息")
    @DeleteMapping("/deleteById/{id}")
    public R deleteById(@PathVariable("id") Long id) {
        hospitalSetService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "带查询条件的分页")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public R getPageInfo(@ApiParam(name = "pageNum", value = "当前页") @PathVariable("pageNum") Integer pageNum,
                         @ApiParam(name = "pageSize", value = "每页显示记录条数") @PathVariable("pageSize") Integer pageSize,
                         @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(pageNum, pageSize);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())) {
            queryWrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())) {
            queryWrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }
        // 带查询条件的分页
        hospitalSetService.page(page, queryWrapper);

        return R.ok().data("total", page.getTotal())
                .data("rows", page.getRecords());
    }

    @ApiOperation(value = "新增医院设置")
    @PostMapping("/saveHospSet")
    public R save(@ApiParam(name = "hospitalSet", value = "医院设置对象", required = true) @RequestBody
                    HospitalSet hospitalSet) {

        // 默认状态：1
        hospitalSet.setStatus(1);
        // 签名密钥
        Random random = new Random();
        String secretKey = MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000));
        hospitalSet.setSignKey(secretKey);

        hospitalSetService.save(hospitalSet);

        return R.ok();
    }

    @ApiOperation(value = "修改之前回显医院设置信息")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("item",hospitalSet);
    }

    @ApiOperation(value = "真正的修改")
    @PutMapping("/update")
    public R update(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @ApiOperation(value = "批量删除")
    @DeleteMapping("/batchDelete")
    public R batchDelete(@RequestBody List<Long> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    @ApiOperation(value = "锁定与解锁")
    @PutMapping("/status/{id}/{status}")
    public R updateStatus(@PathVariable("id") Long id,
                          @PathVariable("status") Integer status){
        // 根据id查询出hospitalSet对象
//        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 这里直接new对象吧，因为如果再次和数据库交互 会影响性能
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }
}