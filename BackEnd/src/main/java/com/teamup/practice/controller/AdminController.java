package com.teamup.practice.controller;

import com.teamup.practice.dto.UserUpdateDTO;
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
import org.springframework.web.bind.annotation.*;

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
    
    /**
     * 管理员更新用户资料
     * @param id 用户ID
     * @param userUpdateDTO 用户更新信息
     * @return
     */
    @PutMapping("/users/{id}")
    public ResponseResult<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(id, userUpdateDTO);
        return ResponseResult.success();
    }
    
    /**
     * 管理员删除用户
     * @param id 用户ID
     * @return
     */
    @DeleteMapping("/users/{id}")
    public ResponseResult<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseResult.success();
    }

}
