package com.teamup.practice.controller;

import com.teamup.practice.po.Consultations;
import com.teamup.practice.result.ResponseResult;
import com.teamup.practice.service.ConsultationsService;
import com.teamup.practice.utils.BeanUtil;
import com.teamup.practice.vo.ConsultationsVO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
@Api(tags = "咨询管理接口")
public class ConsultationsController {

    private final ConsultationsService consultationsService;


    @RequestMapping("/getConsultationsByUserId/{userId}")
    public ResponseResult<List<ConsultationsVO>> getConsultationsByUserId(@PathVariable Long userId) {
        return ResponseResult.success(consultationsService.getConsultationsByUserId(userId));
    }

    @PostMapping("/addConsultations")
    public ResponseResult<Void> addConsultations(@RequestBody ConsultationsVO consultationsVO) {
        consultationsService.addConsultations(consultationsVO);
        return ResponseResult.success();
    }


}
