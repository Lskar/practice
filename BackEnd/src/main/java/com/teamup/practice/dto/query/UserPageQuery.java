package com.teamup.practice.dto.query;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "用户分页查询条件")
public class UserPageQuery extends PageQuery{


    /**
     * 用户ID
     */

    Long userId;



    /**
     * 用户名
     */

    String username;


    /**
     * 邮箱
     */

    String email;



    /**
     * 手机号
     */

    String phone;



    /**
     * 真实姓名
     */

    String realName;



}
