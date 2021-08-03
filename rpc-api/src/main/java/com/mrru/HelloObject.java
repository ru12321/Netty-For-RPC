package com.mrru;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试用api的实体
 *
 * @className: com.mrru.HelloObject
 * @author: 茹某
 * @date: 2021/8/1 9:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable
{
    private Integer id;
    private String message;
}
