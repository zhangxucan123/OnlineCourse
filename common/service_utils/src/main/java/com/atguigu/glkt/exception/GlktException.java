package com.atguigu.glkt.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlktException extends RuntimeException{
    private Integer code;
    private String msg;
}
