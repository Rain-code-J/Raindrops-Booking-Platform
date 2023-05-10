package com.rain.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rain.yygh.model.order.PaymentInfo;
import com.rain.yygh.model.order.RefundInfo;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author rain
 * @since 2023-05-10
 */
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

}
