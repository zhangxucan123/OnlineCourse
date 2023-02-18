package com.atguigu.glkt.exception;

import com.atguigu.glkt.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice //aop
public class GlobalExceptionHandler {

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null).message("执行全局异常处理");
    }

    //自定义异常处理GlktException
    @ExceptionHandler(GlktException.class)
    @ResponseBody
    public Result error(GlktException e) {
        e.printStackTrace();
        return Result.fail(null).code(e.getCode()).message(e.getMsg());
    }
}
