package com.rain.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.enums.OrderStatusEnum;
import com.rain.yygh.enums.PaymentStatusEnum;
import com.rain.yygh.hosp.client.ScheduleFeignClient;
import com.rain.yygh.model.order.OrderInfo;
import com.rain.yygh.model.order.PaymentInfo;
import com.rain.yygh.model.user.Patient;
import com.rain.yygh.mq.MqConst;
import com.rain.yygh.mq.RabbitMQService;
import com.rain.yygh.order.mapper.OrderInfoMapper;
import com.rain.yygh.order.service.OrderInfoService;
import com.rain.yygh.order.service.PaymentService;
import com.rain.yygh.order.service.WeixinService;
import com.rain.yygh.order.utils.HttpRequestHelper;
import com.rain.yygh.user.client.PatientFeignClient;
import com.rain.yygh.vo.hosp.ScheduleOrderVo;
import com.rain.yygh.vo.msm.MsmVo;
import com.rain.yygh.vo.order.OrderCountQueryVo;
import com.rain.yygh.vo.order.OrderCountVo;
import com.rain.yygh.vo.order.OrderMqVo;
import com.rain.yygh.vo.order.OrderQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author rain
 * @since 2023-05-07
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ScheduleFeignClient scheduleFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        // 1.获取排班信息
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getScheduleById(scheduleId);
        // 2.获取就诊人信息
        Patient patient = patientFeignClient.getPatientById(patientId);

        // 3.从平台请求第三方医院，确认用户能否挂号
        Map<String, Object> map = new HashMap<>();
        map.put("hoscode", scheduleOrderVo.getHoscode());
        map.put("depcode", scheduleOrderVo.getDepcode());
        map.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        map.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        map.put("reserveTime", scheduleOrderVo.getReserveTime());
        map.put("amount", scheduleOrderVo.getAmount());

        JSONObject result =
                HttpRequestHelper.sendRequest(map, "http://localhost:9998/order/submitOrder");
        if (result != null && result.getInteger("code") == 200) {
            // 如果返回成功，得到返回其他数据
            JSONObject jsonObject = result.getJSONObject("data");
            String hosRecordId = jsonObject.getString("hosRecordId");
            Integer number = jsonObject.getInteger("number");
            String fetchTime = jsonObject.getString("fetchTime");

            String fetchAddress = jsonObject.getString("fetchAddress");

            // 如果医院接口返回成功，添加上面三部分数据到数据库
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);

            orderInfo.setOutTradeNo(System.currentTimeMillis() + "" + new Random().nextInt(100));
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            baseMapper.insert(orderInfo);
            // 更新可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            // 发送RabbitMQ信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setReservedNumber(reservedNumber);

            // 封装msmVo
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);

            orderMqVo.setMsmVo(msmVo);
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

            // 4.返回订单id
            return orderInfo.getId();
        } else {
            throw new YyghException(20001, "挂号失败");
        }

    }

    @Override
    public Page<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {
        // 获取查询条件
        Long userId = orderQueryVo.getUserId();
        String outTradeNo = orderQueryVo.getOutTradeNo();
        Long patientId = orderQueryVo.getPatientId();
        String patientName = orderQueryVo.getPatientName();
        String keyword = orderQueryVo.getKeyword();
        String orderStatus = orderQueryVo.getOrderStatus();
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("hosname", keyword);
        }
        if (!StringUtils.isEmpty(patientId)) {
            queryWrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            queryWrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            queryWrapper.ge("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd);
        }
        Page<OrderInfo> pages = baseMapper.selectPage(page, queryWrapper);
        pages.getRecords().stream().forEach(item -> {
            this.packageOrderInfo(item);
        });

        return pages;
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // 1.确定当前取消预约的时间 和 挂号订单的取消预约截止时间 对比, 当前时间是否已经超过了 挂号订单的取消预约截止时间
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        // 1.1 如果超过了，直接抛出异常，不让用户取消
        if (quitTime.isBeforeNow()) {
            throw new YyghException(20001, "已经过了取消预约时间");
        }
        // 2.从平台请求第三方医院，通知第三方医院，该用户已取消
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("hosRecordId", orderInfo.getHosRecordId());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/updateCancelStatus");
        // 2.1 判断第三方医院是否同意取消订单
        if (jsonObject == null || jsonObject.getInteger("code") != 200) {
            throw new YyghException(20001, "取消失败");
        }
        // 3.判断用户是否对当前挂号订单是否已支付
        if (orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()) {
            // 已支付：进行退款
            Boolean flag = weixinService.refund(orderId);
            if (!flag) {
                throw new YyghException(20001, "退款失败");
            }
        }
        // 无论用户是否进了支付

        // 4.更新订单的订单状态 及 支付记录表的支付状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        PaymentInfo paymentInfo = paymentService.getOne(queryWrapper);
        if (paymentInfo != null) {
            paymentInfo.setPaymentStatus(PaymentStatusEnum.REFUND.getStatus());
            paymentService.updateById(paymentInfo);
        }
        // 5.更新医生的剩余可预约数信息
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("halo");
        msmVo.setParam(null);
        orderMqVo.setMsmVo(msmVo);
        //6.给就诊人发送短信提示：
        rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
    }

    @Override
    public void patientRemind() {
        // 先查询预约时间在今天的order
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status",-1);
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);

        for (OrderInfo orderInfo : orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitMQService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }
    }

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        Map<String,Object> map = new HashMap<>();
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        for (OrderCountVo orderCountVo : orderCountVoList) {
            String reserveDate = orderCountVo.getReserveDate();
            Integer count = orderCountVo.getCount();
            dateList.add(reserveDate);
            countList.add(count);
        }
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    private void packageOrderInfo(OrderInfo item) {
        String statusNameByStatus = OrderStatusEnum.getStatusNameByStatus(item.getOrderStatus());
        Map<String, Object> param = item.getParam();
        param.put("orderStatusString", statusNameByStatus);
    }
}
