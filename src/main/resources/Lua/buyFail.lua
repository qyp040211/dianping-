-- 数据库生成订单出现异常，将Redis中的库存恢复，删除购买过的用户ID

-- 参数列表
-- 优惠券id
local voucherId = ARGV[1]
-- 用户id
local userId = ARGV[2]

-- 库存key
local stockKey = 'seckill:stock:' .. voucherId
-- 订单key
local orderKey = 'seckill:order:' .. voucherId

-- 恢复库存
redis.call('incrby', stockKey, 1)

-- 去除userId
redis.call('srem', orderKey, userId)