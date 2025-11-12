package com.teamup.practice.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("users")
@NoArgsConstructor
public class User {

    /**
     * 用户ID
     */

    @TableId
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
     * 密码
     */

    String password;


    /**
     * 真实姓名
     */

    String realName;
}
