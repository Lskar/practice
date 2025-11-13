package com.teamup.practice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamup.practice.config.JwtProperties;
import com.teamup.practice.dto.UserLoginDTO;
import com.teamup.practice.dto.UserRegisterDTO;
import com.teamup.practice.dto.UserUpdateDTO;
import com.teamup.practice.dto.query.UserPageQuery;
import com.teamup.practice.enums.HttpStatusEnum;
import com.teamup.practice.exception.UserNameExistException;
import com.teamup.practice.exception.UserNotFoundException;
import com.teamup.practice.exception.UserSaveErrorException;
import com.teamup.practice.mapper.ConsultationsMapper;
import com.teamup.practice.mapper.UserMapper;
import com.teamup.practice.po.User;
import com.teamup.practice.service.UserService;
import com.teamup.practice.utils.BeanUtil;
import com.teamup.practice.utils.JwtTool;
import com.teamup.practice.vo.UserDetailVO;
import com.teamup.practice.vo.UserLoginVO;
import com.teamup.practice.vo.page.PageVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.teamup.practice.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.teamup.practice.enums.HttpStatusEnum.BAD_REQUEST;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final JwtTool jwtTool;


    private final JwtProperties jwtProperties;

    private final ConsultationsMapper consultationsMapper;

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;





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

        if (!hasUserName(requestParam.getUsername())) {
            throw new UserNameExistException(BAD_REQUEST.getCode(),"用户名已存在");
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
            if (lock.tryLock()){
                int insert = baseMapper.insert(BeanUtil.convert(requestParam, User.class));
                if (insert <= 0) {
                    throw new UserSaveErrorException(BAD_REQUEST.getCode(),"用户保存失败");
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }

            throw new UserNameExistException(BAD_REQUEST.getCode(),"用户名已存在");

        }finally {
            lock.unlock();
        }

    }

    @Override
    public UserDetailVO getUserDetail(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new UserNotFoundException(HttpStatusEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        return BeanUtil.convert(user, UserDetailVO.class);
    }

    @Override
    public PageVO<UserDetailVO> getUsersList(UserPageQuery query) {


        Page<User> pageResult = lambdaQuery()
                .like(StrUtil.isNotBlank(query.getUsername()),User::getUsername, query.getUsername())
                .like(StrUtil.isNotBlank(query.getEmail()),User::getEmail, query.getEmail())
                .like(StrUtil.isNotBlank(query.getPhone()),User::getPhone, query.getPhone())
                .like(StrUtil.isNotBlank(query.getRealName()),User::getRealName, query.getRealName())
                .page(query.toMpPage(query.getSortBy(),query.getIsAsc()));


        return PageVO.of(pageResult, UserDetailVO.class);
    }
    
    @Override
    public void updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = getById(id);
        if (user == null) {
            throw new UserNotFoundException(HttpStatusEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        
        // 只更新非空字段
        if (StringUtils.hasText(userUpdateDTO.getRealName())) {
            user.setRealName(userUpdateDTO.getRealName());
        }
        if (StringUtils.hasText(userUpdateDTO.getPhone())) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        if (StringUtils.hasText(userUpdateDTO.getEmail())) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        
        updateById(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new UserNotFoundException(HttpStatusEnum.NOT_FOUND.getCode(), "用户不存在");
        }
        
        // 删除用户关联的咨询记录
        consultationsMapper.deleteByUserId(id);
        
        // 删除用户
        removeById(id);
    }

    public Boolean hasUserName(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }
}
