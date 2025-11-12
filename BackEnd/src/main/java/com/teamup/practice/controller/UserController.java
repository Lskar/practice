package com.teamup.practice.controller;

import com.teamup.practice.dto.UserLoginDTO;
import com.teamup.practice.dto.UserRegisterDTO;
import com.teamup.practice.result.ResponseResult;
import com.teamup.practice.service.UserService;
import com.teamup.practice.vo.UserDetailVO;
import com.teamup.practice.vo.UserLoginVO;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Api(tags = "用户接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseResult<Void> register(@RequestBody UserRegisterDTO requestParam) {
        userService.register(requestParam);
        return ResponseResult.success();
    }
    @PostMapping("/login")
    public ResponseResult<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseResult.success(userService.login(userLoginDTO));
    }

    @GetMapping("/{id}")
    public ResponseResult<UserDetailVO> getUserDetail(@PathVariable Long id){
        return ResponseResult.success(userService.getUserDetail(id));
    }






}
