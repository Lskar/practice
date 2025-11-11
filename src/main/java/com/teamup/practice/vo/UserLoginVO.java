package com.teamup.practice.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户登录返回参数")
public class UserLoginVO {


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;


    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名称")
    private String username;


    /**
     * 登录token
     */
    @ApiModelProperty(value = "登录token")
    private String token;



}
