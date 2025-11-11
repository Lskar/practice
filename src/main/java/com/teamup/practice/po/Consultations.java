package com.teamup.practice.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName("consultations")
@NoArgsConstructor
public class Consultations {


    /**
     * 咨询ID
     */
    @TableId
    Long id;


    /**
     * 用户ID
     */
    Long userId;


    /**
     * 咨询标题
     */

    private String title;


    /**
     * 购车预算
     */
    private String budget;


    /**
     * 偏好车型
     */
    private String carType;


    /**
     * 使用场景
     */
    private List<String> useCase;


    /**
     * 燃料类型
     */
    private String fuelType;


    /**
     * 品牌偏好
     */
    private List<String> brands;


    /**
     * 详细需求描述
     */
    private String description;


    /**
     * AI模型
     */
    private Enum ai_model;


    /**
     * 咨询结果
     */
    private String result;



    /**
     * 用户评分：1-5星
     */
    private Integer rating;

}
