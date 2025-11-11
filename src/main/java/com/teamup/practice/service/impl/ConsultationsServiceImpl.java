package com.teamup.practice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamup.practice.mapper.ConsultationsMapper;
import com.teamup.practice.mapper.UserMapper;
import com.teamup.practice.po.Consultations;
import com.teamup.practice.po.User;
import com.teamup.practice.service.ConsultationsService;
import com.teamup.practice.utils.BeanUtil;
import com.teamup.practice.vo.ConsultationsVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationsServiceImpl extends ServiceImpl<ConsultationsMapper, Consultations> implements ConsultationsService {


    @Override
    public List<ConsultationsVO> getConsultationsByUserId(Long userId) {

        LambdaQueryWrapper<Consultations> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Consultations::getUserId,userId);
        List<Consultations> consultations = list(queryWrapper);
        return consultations.stream().map(item -> BeanUtil.convert(item,ConsultationsVO.class)).toList();

    }

    @Override
    public void addConsultations(ConsultationsVO consultationsVO) {

        Consultations consultations = BeanUtil.convert(consultationsVO,Consultations.class);
        Boolean insert = save(consultations);
        if (!insert) {
            throw new RuntimeException("添加失败");
        }
    }
}
