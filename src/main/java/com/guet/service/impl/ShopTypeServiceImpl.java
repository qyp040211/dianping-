package com.guet.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guet.entity.ShopType;
import com.guet.mapper.ShopTypeMapper;
import com.guet.service.IShopTypeService;
import com.guet.utils.MyConvertUtils;
//import com.sun.deploy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.guet.constants.RedisConstants.CACHE_SHOP_TYPE_KEY;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate srt;

    @Autowired
    private ShopTypeMapper shopTypeMapper;


    @Override
    public List<ShopType> queryByCacheOrList () {
        // 先从Redis中获取
        List<String> cacheTypes = srt.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);

        // 如果得到的不是空列表
        if (!cacheTypes.isEmpty()) {
            // 拼接成标准JSON数组字符串
            String json = "[" + String.join(",", cacheTypes) + "]";
            // 批量解析为对象列表
            return JSONUtil.toList(json, ShopType.class);
        }

        // 在Redis中没找到，去数据库查
        LambdaQueryWrapper<ShopType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ShopType::getSort);
        List<ShopType> shopTypeList = shopTypeMapper.selectList(queryWrapper);

        // 放入Redis中
        srt.opsForList().rightPushAll(CACHE_SHOP_TYPE_KEY,
                MyConvertUtils.objectListToStringList(shopTypeList, type -> JSONUtil.toJsonStr(type)));

        // 查不到，返回null
        return shopTypeList;
    }
}
