package com.teamup.practice.controller;

import com.teamup.practice.dto.ConsultationRequest;
import com.teamup.practice.result.ResponseResult;
import com.teamup.practice.service.CarConsultationService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Api(tags = "ai调用接口")
public class CarConsultationController {

    private final CarConsultationService carConsultationService;

    @GetMapping("/getConsultations")
    public ResponseResult<String> getConsultations(@RequestBody ConsultationRequest request) {
        return ResponseResult.success(carConsultationService.consultation(request));
    }
}
