package com.teamup.practice.dto.query;

import com.teamup.practice.enums.AiModelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConsultationsPageQuery extends PageQuery{

    /**
     * 用户ID
     */
    Long userId;


    /**
     * 咨询标题
     */
    String title;


    /**
     * 偏好车型
     */
    private String carType;


    /**
     * 燃料类型
     */
    private String fuelType;


    /**
     * AI模型
     */
    private AiModelEnum ai_model;


    /**
     * 用户评分：1-5星
     */
    private Integer rating;

}
