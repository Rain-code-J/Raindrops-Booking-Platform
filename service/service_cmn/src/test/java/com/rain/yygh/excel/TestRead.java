package com.rain.yygh.excel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        String fileName = "D:\\桌面\\01.xlsx";
        EasyExcel.read(fileName,StuData.class,new StuDataListener()).sheet(0).doRead();
    }
}
