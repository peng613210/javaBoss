package com.zhihui.service.impl;

import com.tools.common.encrypt.EncryptUtil;
import com.zhihui.dao.UserDetailMapper;
import com.zhihui.dao.UserMapper;
import com.zhihui.entity.User;
import com.zhihui.entity.UserDetail;
import com.zhihui.service.UserService;
import com.zhihui.token.Token;
import com.zhihui.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public ResultVo register(User user) {
        if (user != null) {
            if (userMapper.selectByEmail(user.getEmail()) != null) {
                return ResultVo.setERROR();
            }
            user.setPassword(EncryptUtil.md5Enc(user.getPassword()));
            if (userMapper.insertSelective(user) > 0) {
                UserDetail userDetail = new UserDetail();
                userDetail.setUid(user.getId());
                userDetailMapper.insertSelective(userDetail);
                return ResultVo.setOK("注册成功");
            }
        }
        return ResultVo.setERROR();
    }

    @Override
    public ResultVo login(User user) {
        if (user != null) {
            User admin = userMapper.selectByEmail(user.getEmail());
            if (admin != null) {
                if ((EncryptUtil.md5Enc(user.getPassword())).equals(admin.getPassword())) {
                    String token = UUID.randomUUID().toString();
                    Token.token.put(token, admin);
                    return ResultVo.setOK(token);
                } else {
                    return ResultVo.setERROR();
                }
            } else {
                return ResultVo.setERROR();
            }

        }
        return ResultVo.setERROR();
    }

    @Override
    public ResultVo logout(String token) {
        Token.token.remove(token);
        return ResultVo.setOK("注销成功");
    }

    @Override
    public User findUNameById(Integer id) {
        User user = userMapper.findUNameById(id);
        return user;
    }

    @Override
    public ResultVo updateNickname(String token, String name) {
        User user = Token.token.get(token);
        if(user!=null){
            user.setName(name);
            if(userMapper.updateByPrimaryKeySelective(user)>0){
                return ResultVo.setOK("修改成功");
            }
        }
        return ResultVo.setERROR();
    }


}
