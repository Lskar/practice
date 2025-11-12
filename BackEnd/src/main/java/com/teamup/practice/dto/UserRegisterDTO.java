package com.teamup.practice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户注册参数")
public class UserRegisterDTO {


    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 密码
     */

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 电话
     */

    @ApiModelProperty(value = "电话", required = true)
    private String phone;

}
