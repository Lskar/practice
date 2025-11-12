package com.teamup.practice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamup.practice.config.JwtProperties;
import com.teamup.practice.dto.UserLoginDTO;
import com.teamup.practice.dto.UserRegisterDTO;
import com.teamup.practice.enums.HttpStatusEnum;
import com.teamup.practice.exception.UserNotFoundException;
import com.teamup.practice.mapper.ConsultationsMapper;
import com.teamup.practice.mapper.UserMapper;
import com.teamup.practice.po.Consultations;
import com.teamup.practice.po.User;
import com.teamup.practice.service.UserService;
import com.teamup.practice.utils.BeanUtil;
import com.teamup.practice.utils.JwtTool;
import com.teamup.practice.vo.UserDetailVO;
import com.teamup.practice.vo.UserLoginVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final JwtTool jwtTool;


    private final JwtProperties jwtProperties;

    private final ConsultationsMapper consultationsMapper;


    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();


        User user = lambdaQuery().eq(User::getUsername, username).one();

        log.debug("adminDO:{}", user);

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtTool.createToken(user.getUserId(), jwtProperties.getTokenTTL());

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);

        return BeanUtil.convert(user,userLoginVO);

    }

    @Override
    public void register(UserRegisterDTO requestParam) {
        int insert = baseMapper.insert(BeanUtil.convert(requestParam, User.class));
    }

    @Override
    public UserDetailVO getUserDetail(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new UserNotFoundException(HttpStatusEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        return BeanUtil.convert(user, UserDetailVO.class);
    }


}
