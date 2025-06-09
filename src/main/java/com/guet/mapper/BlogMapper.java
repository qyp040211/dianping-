package com.guet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guet.dto.UserDTO;
import com.guet.entity.Blog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
* @author lyj
* @description 针对表【tb_blog】的数据库操作Mapper
* @createDate 2024-02-15 18:09:23
* @Entity  com.guet.entity.Blog
*/
public interface BlogMapper extends BaseMapper<Blog> {

    List<UserDTO> queryUserDTOByIds (Set<String> userIds);

    @Update("UPDATE tb_blog SET images = #{imagePath} WHERE id = #{blogId}")
    int updateBlogImage(@Param("blogId") Long blogId, @Param("imagePath") String imagePath);

}




