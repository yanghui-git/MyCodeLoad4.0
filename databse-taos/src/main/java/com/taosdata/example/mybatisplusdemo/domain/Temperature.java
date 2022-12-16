package com.taosdata.example.mybatisplusdemo.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 记录温度
 */
@Data
public class Temperature {

    private Timestamp ts;

    private float temperature;

    /**
     * 标记子表tag
     *
     */
    private String location;

    /**
     * 标记子表tag
     *
     */
    private int tbIndex;

}
