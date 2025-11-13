package com.teamup.practice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamup.practice.dto.query.ConsultationsPageQuery;
import com.teamup.practice.mapper.ConsultationsMapper;
import com.teamup.practice.po.Consultations;
import com.teamup.practice.service.ConsultationsService;
import com.teamup.practice.utils.BeanUtil;
import com.teamup.practice.vo.ConsultationsVO;
import com.teamup.practice.vo.page.PageVO;
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
        boolean insert = save(consultations);
        if (!insert) {
            throw new RuntimeException("添加失败");
        }
    }

    @Override
    public PageVO<ConsultationsVO> getConsultationsList(ConsultationsPageQuery query) {

        Page<Consultations> pageResult = lambdaQuery()
                .eq(query.getUserId() != null,Consultations::getUserId,query.getUserId())
                .like(StrUtil.isNotBlank(query.getTitle()),Consultations::getTitle,query.getTitle())
                .like(StrUtil.isNotBlank(query.getCarType()),Consultations::getCarType,query.getCarType())
                .like(StrUtil.isNotBlank(query.getFuelType()),Consultations::getFuelType,query.getFuelType())
                //TODO 字段属性有问题
//                .like(Consultations::getAi_model,query.getAi_model())
                .like(query.getRating()!=null, Consultations::getRating,query.getRating())
                .page(query.toMpPage(query.getSortBy(),query.getIsAsc()));


        return PageVO.of(pageResult, ConsultationsVO.class);
    }
    
    @Override
    public void deleteConsultation(Long id, Long userId) {
        // 检查咨询记录是否存在
        Consultations consultation = getById(id);
        if (consultation == null) {
            throw new RuntimeException("咨询记录不存在");
        }
        
        // 检查是否是该用户自己的记录
        if (!consultation.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除他人的咨询记录");
        }
        
        // 删除记录
        removeById(id);
    }
}
