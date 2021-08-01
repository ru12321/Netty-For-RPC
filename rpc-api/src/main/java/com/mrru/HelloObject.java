package com.mrru;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: com.mrru.HelloObject
 * @author: 茹某
 * @date: 2021/8/1 9:02
 * 测试用api的实体
 **/
@Data
@AllArgsConstructor
public class HelloObject implements Serializable
{
    private Integer id;
    private String message;
}
