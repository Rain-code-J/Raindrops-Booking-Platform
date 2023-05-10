package com.rain.yygh.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StuData {
    @ExcelProperty("学生编号")
    private int sid;
    @ExcelProperty("学生姓名")
    private String sname;
}
