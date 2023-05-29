package com.rain.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.cmn.listener.DictListener;
import com.rain.yygh.cmn.mapper.DictMapper;
import com.rain.yygh.cmn.service.DictService;
import com.rain.yygh.model.cmn.Dict;
import com.rain.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author rain
 * @since 2023-04-25
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private DictMapper dictMapper;

    /**
     * 查找子结点
     * @param parentId
     * @return
     */
    @Override
    @Cacheable(value = "dict")
    public List<Dict> findChilds(Long parentId) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>();
        queryWrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        for (Dict dict : dictList) {
            // 查询该节点是否有子结点
            Boolean isHasChildren = isHasChildren(dict.getId());
            dict.setHasChildren(isHasChildren);
        }
        return dictList;
    }

    /**
     * 判断该id下面是否还有子结点
     * @param parentId
     * @return
     */
    private Boolean isHasChildren(Long parentId) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>();
        queryWrapper.eq("parent_id",parentId);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public void download(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            // 获取所有的数据字典
            List<Dict> dictList = baseMapper.selectList(null);
            // 新建dictEeVoList：即Excel表格中存储的对象
            List<DictEeVo> dictEeVoList = new ArrayList<>(dictList.size());
            // 遍历：将dictList里的属性设置给dictEeVoList
            for (Dict dict : dictList) {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict,dictEeVo);
                dictEeVoList.add(dictEeVo);
            }
            // 开始写操作
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictEeVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void upload(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(dictMapper)).sheet(0).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 地区名称代码
     *
     * @param value 代码
     * @return {@link String}
     */
    @Override
    public String getRegionNameByValue(Long value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value",value);
        Dict dict = baseMapper.selectOne(queryWrapper);
        if (dict != null){
            return dict.getName();
        }
        return null;
    }

    @Override
    public String getNameByValueAndDictCode(String dictCode, Long value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);

        QueryWrapper<Dict> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("parent_id",dict.getId());
        queryWrapper1.eq("value",value);
        Dict dict1 = baseMapper.selectOne(queryWrapper1);

        return dict1.getName();
    }
}
