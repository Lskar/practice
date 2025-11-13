package com.teamup.practice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.teamup.practice.dto.UserLoginDTO;
import com.teamup.practice.dto.UserRegisterDTO;
import com.teamup.practice.dto.UserUpdateDTO;
import com.teamup.practice.dto.query.UserPageQuery;
import com.teamup.practice.po.User;
import com.teamup.practice.vo.UserDetailVO;
import com.teamup.practice.vo.UserLoginVO;
import com.teamup.practice.vo.page.PageVO;

public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return 登录成功返回用户信息
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);

    void register(UserRegisterDTO requestParam);

    UserDetailVO getUserDetail(Long id);

    PageVO<UserDetailVO> getUsersList(UserPageQuery query);
    
    void updateUser(Long id, UserUpdateDTO userUpdateDTO);
    
    void deleteUser(Long id);
}
