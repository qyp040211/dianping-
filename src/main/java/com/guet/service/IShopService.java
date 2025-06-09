package com.guet.service;

import com.guet.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.result.Result;

public interface IShopService extends IService<Shop> {

    Result queryById (Long id) throws Exception;

    Result saveShopAndDelCache (Shop shop);

    Result updateByIdAndDelCache (Shop shop);

    Result queryShopByType (Integer typeId, Integer current, Double x, Double y);
}
