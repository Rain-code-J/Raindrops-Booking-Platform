package com.rain.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.enums.RefundStatusEnum;
import com.rain.yygh.model.order.PaymentInfo;
import com.rain.yygh.model.order.RefundInfo;
import com.rain.yygh.order.mapper.RefundInfoMapper;
import com.rain.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author rain
 * @since 2023-05-10
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        // 通过orderId查询退款记录
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",paymentInfo.getOrderId());
        RefundInfo refundInfo = baseMapper.selectOne(queryWrapper);
        if (refundInfo != null){
            return refundInfo;
        }

        // 如果为空，那么保存退款记录
        refundInfo = new RefundInfo();
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
