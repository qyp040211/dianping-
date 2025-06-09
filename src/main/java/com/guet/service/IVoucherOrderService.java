package com.guet.service;

import com.guet.dto.VoucherOrderIdDTO;
import com.guet.entity.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guet.result.Result;

import java.util.concurrent.ExecutionException;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result buySeckillVoucher (Long voucherId) throws ExecutionException, InterruptedException;

    void createVoucherOrder (VoucherOrderIdDTO voucherOrderIdDTO);
}
