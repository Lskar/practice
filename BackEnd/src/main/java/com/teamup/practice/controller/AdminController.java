package com.teamup.practice.controller;

import com.teamup.practice.dto.query.ConsultationsPageQuery;
import com.teamup.practice.dto.query.UserPageQuery;
import com.teamup.practice.result.ResponseResult;
import com.teamup.practice.service.ConsultationsService;
import com.teamup.practice.service.UserService;
import com.teamup.practice.vo.ConsultationsVO;
import com.teamup.practice.vo.UserDetailVO;
import com.teamup.practice.vo.page.PageVO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
@Api(tags = "管理员接口")
public class AdminController {

    private final UserService userService;

    private final ConsultationsService consultationsService;


    @GetMapping("/consultations/page")
    public ResponseResult<PageVO<ConsultationsVO>> getConsultationsList(ConsultationsPageQuery query) {

        return ResponseResult.success(consultationsService.getConsultationsList(query));
    }


    @GetMapping("/users/page")
    public ResponseResult<PageVO<UserDetailVO>> getUsersList(UserPageQuery query) {

        return ResponseResult.success(userService.getUsersList(query));
    }
















}
