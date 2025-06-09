package com.guet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.dto.UserDTO;
import com.guet.entity.User;
import com.guet.result.Result;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author lyj
* @description 针对表【tb_user】的数据库操作Mapper
* @createDate 2024-02-15 18:09:23
* @Entity  com.guet.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    List<UserDTO> queryCommon (List<Long> userIdList);

    Result signCount ();

    @Update("UPDATE tb_user SET icon = #{icon} WHERE id = #{userId}")
    int updateUserIcon(@Param("userId") Long userId, @Param("icon") String icon);

}




