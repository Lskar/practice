package com.teamup.practice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamup.practice.po.Consultations;
import com.teamup.practice.vo.ConsultationsVO;

import java.util.List;


public interface ConsultationsService extends IService<Consultations> {


    List<ConsultationsVO> getConsultationsByUserId(Long userId);


    void addConsultations(ConsultationsVO consultationsVO);
}
