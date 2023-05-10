package com.rain.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rain.yygh.common.exception.YyghException;
import com.rain.yygh.hosp.bean.ResultCodeEnum;
import com.rain.yygh.hosp.repository.HospitalRepository;
import com.rain.yygh.hosp.repository.ScheduleRepository;
import com.rain.yygh.hosp.service.DepartmentService;
import com.rain.yygh.hosp.service.ScheduleService;
import com.rain.yygh.model.hosp.BookingRule;
import com.rain.yygh.model.hosp.Department;
import com.rain.yygh.model.hosp.Hospital;
import com.rain.yygh.model.hosp.Schedule;
import com.rain.yygh.vo.hosp.BookingScheduleRuleVo;
import com.rain.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void saveSchedule(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule platformSchedule = scheduleRepository.findScheduleByHoscodeAndDepcodeAndHosScheduleId(hoscode, depcode, hosScheduleId);
        if (platformSchedule != null) {
            // 存在：修改操作
            schedule.setId(platformSchedule.getId());
            schedule.setCreateTime(schedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(platformSchedule.getIsDeleted());

            scheduleRepository.save(schedule);
        } else {
            // 不存在：添加操作
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);

            scheduleRepository.save(schedule);
        }

    }

    @Override
    public Page<Schedule> findPageSchedule(Integer page, Integer limit, Schedule schedule) {
        Example example = Example.of(schedule);
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page pageList = scheduleRepository.findAll(example, pageable);
        return pageList;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.findScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getSchedulePage(Long pageNum, Long pageSize, String hoscode, String depcode) {
        // 创建返回对象
        Map<String, Object> map = new HashMap<>();
        // 查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        // 聚合查询
        Aggregation aggregation = Aggregation.newAggregation(
                // 1.查询条件
                Aggregation.match(criteria),
                // 2.聚合
                Aggregation.group("workDate")
                        // 2.1根据workDate进行分组
                        .first("workDate").as("workDate")
                        // 2.2查询科室可预约数
                        .sum("reservedNumber").as("reservedNumber")
                        // 2.3查询科室剩余预约数
                        .sum("availableNumber").as("availableNumber")
                        // 2.4查询就诊医生人数
                        .count().as("docCount"),
                // 3.排序
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                // 4.分页
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<BookingScheduleRuleVo> aggResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        // 当前页数据列表
        List<BookingScheduleRuleVo> bookingScheduleRuleList = aggResults.getMappedResults();

        // 聚合查询
        Aggregation aggregation1 = Aggregation.newAggregation(
                // 1.查询条件
                Aggregation.match(criteria),
                // 2.聚合
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalResults = mongoTemplate.aggregate(aggregation1, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalResults.getMappedResults().size();

        //把日期对应星期获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(workDate)));
        }


        // 获取医院名称
        Hospital hospital = hospitalRepository.findHospitalByHoscode(hoscode);
        String hosname = hospital.getHosname();

        map.put("bookingScheduleRuleList", bookingScheduleRuleList);
        map.put("total", total);

        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosname);
        map.put("baseMap", baseMap);

        return map;
    }

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        DateTime dateTime = new DateTime(workdate);
        Date date = dateTime.toDate();
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        schedule.setDepcode(depcode);
        schedule.setWorkDate(date);
        Example example = Example.of(schedule);
        List<Schedule> scheduleRepositoryAll = scheduleRepository.findAll(example);

        scheduleRepositoryAll.stream().forEach(item -> {
            this.packageSchedule(item);
        });

        return scheduleRepositoryAll;
    }

    @Override
    public Map<String, Object> getBookingScheduleRule(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        // 最终返回数据
        Map<String, Object> map = new HashMap<>();
        // 获取预约规则
        Hospital hospital = hospitalRepository.findHospitalByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR.getCode(), ResultCodeEnum.DATA_ERROR.getMessage());
        }
        BookingRule bookingRule = hospital.getBookingRule();

        // 获取可预约日期的分页数据
        IPage<Date> iPage = this.getDateListPage(pageNum, pageSize, bookingRule);
        // 获取当前可预约日期
        List<Date> dateList = iPage.getRecords();

        // 获取可预约日期里面科室的剩余预约数
        Criteria criteria = Criteria
                .where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(dateList);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        // 获取预约规则List
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregationResults.getMappedResults();

        //合并数据 将统计数据ScheduleVo根据“安排日期”合并到BookingRuleVo
        Map<Date, BookingScheduleRuleVo> bookingScheduleRuleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(bookingScheduleRuleVoList)) {
            bookingScheduleRuleVoMap = bookingScheduleRuleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        // 获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList1 = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = bookingScheduleRuleVoMap.get(date);
            // 说明今天没有排班规则
            if (bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == dateList.size() - 1 && pageNum == iPage.getPages()){
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            if (i == 0 && pageNum == 1){
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            bookingScheduleRuleVoList1.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        map.put("bookingScheduleList", bookingScheduleRuleVoList1);
        map.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalRepository.findHospitalByHoscode(hoscode).getHosname());
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap", baseMap);
        return map;


    }

    @Override
    public Schedule getById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        Hospital hospital = hospitalRepository.findHospitalByHoscode(schedule.getHoscode());
        BookingRule bookingRule = hospital.getBookingRule();

        if (schedule == null){
            throw new YyghException(20001,"找不到schedule对象");
        }
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);

        // 获取相关信息
        String hosname = hospital.getHosname();
        String depname = departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode()).getDepname();
        Date quitTime = null;
        Date reserveDate = schedule.getWorkDate();
        Date quitDate = new DateTime(reserveDate).plusDays(bookingRule.getQuitDay()).toDate();
        quitTime = this.getDateTime(quitDate, bookingRule.getStopTime()).toDate();
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());

        // 设置相关信息
        scheduleOrderVo.setHosname(hosname);
        scheduleOrderVo.setDepname(depname);
        scheduleOrderVo.setQuitTime(quitTime);
        scheduleOrderVo.setReserveDate(reserveDate);
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setStartTime(startTime.toDate());
        scheduleOrderVo.setEndTime(endTime.toDate());
        scheduleOrderVo.setStopTime(stopTime.toDate());

        return scheduleOrderVo;
    }

    @Override
    public Boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
        return true;
    }

    @Override
    public void cancelSchedule(String scheduleId) {
        Schedule schedule=scheduleRepository.findByHosScheduleId(scheduleId);
        schedule.setAvailableNumber(schedule.getAvailableNumber()+1);
        scheduleRepository.save(schedule);
    }

    private IPage<Date> getDateListPage(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        // 放号时间
        String bookingRuleReleaseTime = bookingRule.getReleaseTime();
        DateTime releaseTime = this.getDateTime(new Date(), bookingRuleReleaseTime);
        // 预约周期
        Integer cycle = bookingRule.getCycle();

        // 如果过了今天的放号时间那么预约周期+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }

        // 获取所有的可预约日期
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            // 今天以及未来cycle的日期
            DateTime dateTime = new DateTime().plusDays(i);
            // 将今天以及未来cycle的日期，格式化
            String s = dateTime.toString("yyyy-MM-dd");
            // 将DateTIme --> Date
            Date date = new DateTime(s).toDate();
            // 添加到所有可预约日期的列表中去
            dateList.add(date);
        }

        // 对日期进行分页显示
        int start = (pageNum - 1) * pageSize;
        int end = (pageNum - 1) * pageSize + pageSize;
        // 如果计算出来的最后一项 大于 所有可预约日期的列表的size
        if (end > dateList.size()) {
            end = dateList.size();
        }
        List<Date> pageDateList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }

        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum,pageSize,dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    //封装排班详情其他值 医院名称、科室名称、日期对应星期
    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalRepository.findHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
