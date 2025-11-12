package com.teamup.practice.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("users")
@NoArgsConstructor
public class User {

    @TableId
    Long userId;

    String username;

    String email;

    String phone;

    String password;

    String realName;
}
