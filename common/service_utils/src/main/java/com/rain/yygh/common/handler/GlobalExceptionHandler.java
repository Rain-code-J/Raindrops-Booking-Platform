package com.rain.yygh.common.handler;

import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理程序
 *
 * @author 董俊卓
 * @date 2023/04/16
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public R error(Exception exception){
        log.error(exception.getMessage());
        // 一般是输出到日志文件中
        exception.printStackTrace();
        return R.error().message(exception.getMessage());
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public R SQLException(ArithmeticException arithmeticException){
        log.error(arithmeticException.getMessage());
        arithmeticException.printStackTrace();
        return R.error().message("数学异常");
    }

    @ExceptionHandler(value = YyghException.class)
    public R YyghException(YyghException yyghException){
        log.error(yyghException.getMessage());
        yyghException.printStackTrace();
        return R.error().message(yyghException.getMessage()).code(yyghException.getCode());
    }


}