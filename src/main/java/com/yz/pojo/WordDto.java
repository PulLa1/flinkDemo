package com.yz.pojo;

import lombok.Data;

import java.sql.Timestamp;


/**
 * @author yz
 * @Description
 * @create 2020-06-12 14:00
 */
@Data
public class WordDto {
    private String word;
    private Integer ct;
    private Timestamp dealTime;
}
