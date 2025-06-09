package com.guet.service.impl;

import com.guet.entity.BlogComments;
import com.guet.mapper.BlogCommentsMapper;
import com.guet.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
