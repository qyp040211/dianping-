package com.guet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guet.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IShopTypeService extends IService<ShopType> {

    List<ShopType> queryByCacheOrList () throws JsonProcessingException;
}
