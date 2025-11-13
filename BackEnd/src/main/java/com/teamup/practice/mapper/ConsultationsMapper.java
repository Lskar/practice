package com.teamup.practice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamup.practice.po.Consultations;
import org.apache.ibatis.annotations.Delete;

public interface ConsultationsMapper extends BaseMapper<Consultations> {

    /**
     * 根据用户ID删除咨询记录
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM consultations WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);

}
