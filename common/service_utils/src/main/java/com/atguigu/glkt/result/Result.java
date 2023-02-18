package com.atguigu.glkt.result;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.Data;

@Data
public class Result<T> {
    //状态码
    private Integer code;
    //返回状态信息(成功、失败)
    private String message;
    //返回数据
    private T data;

    public Result() {}

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        if (data != null) {
            result.setData(data);
        }
        result.setCode(20000);
        result.setMessage("成功");
        return result;
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data) {
        Result<T> result = new Result<>();
        if (data != null) {
            result.setData(data);
        }
        result.setCode(20001);
        result.setMessage("失败");
        return result;
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
