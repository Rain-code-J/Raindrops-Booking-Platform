package com.rain.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author rain
 * @since 2023-04-25
 */
public interface DictService extends IService<Dict> {

    List<Dict> findChilds(Long parentId);

    void download(HttpServletResponse response);

    void upload(MultipartFile file);

    String getRegionNameByValue(Long value);

    String getNameByValueAndDictCode(String dictCode, Long value);
}
