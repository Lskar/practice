package com.teamup.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamup.practice.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
