package com.yz.pojo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yz
 * @Description
 * @create 2020-06-30 16:10
 */
@Data
public class ControlLoop  implements java.io.Serializable{

    private Timestamp dealTime;
    private Double mvValue;
    private Double pvValue;
    private Double spValue;
    private Double modeValue;

    @Override
    public String toString() {
        return dealTime +"\t"+
                mvValue +"\t"+
                pvValue +"\t"+
                spValue +"\t"+
                modeValue ;
    }
}
