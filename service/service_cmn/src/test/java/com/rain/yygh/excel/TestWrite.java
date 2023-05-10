package com.rain.yygh.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        String fileName = "D:\\桌面\\01.xlsx";

        List<StuData> stuDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String sname = "小张" + i;
            StuData stuData = new StuData(i, sname);
            stuDataList.add(stuData);
        }

        EasyExcel.write(fileName, StuData.class).sheet("学生信息").doWrite(stuDataList);
    }
}
