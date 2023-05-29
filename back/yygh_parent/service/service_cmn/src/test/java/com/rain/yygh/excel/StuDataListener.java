package com.rain.yygh.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class StuDataListener extends AnalysisEventListener<StuData> {


    /**
     * 一行一行读
     * @param stuData
     * @param analysisContext
     */
    @Override
    public void invoke(StuData stuData, AnalysisContext analysisContext) {
        System.out.println("stuData = " + stuData);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("标题为：" + headMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("所有数据解析完成");
    }
}
