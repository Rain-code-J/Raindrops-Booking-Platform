package com.rain.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
public interface DictFeignClient {

    @GetMapping("/admin/cmn/dict/{value}")
    String getRegionNameByValue(@PathVariable(value = "value") Long value);

    @GetMapping("/admin/cmn/dict/{dictCode}/{value}")
    String getNameByValueAndDictCode(
            @PathVariable(value = "dictCode") String dictCode,
            @PathVariable(value = "value") Long value);

}
