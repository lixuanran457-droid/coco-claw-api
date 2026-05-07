package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.UsageRecord;
import com.cococlown.cococlawservice.mapper.UsageRecordMapper;
import com.cococlown.cococlawservice.service.UsageRecordService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用记录Service实现
 */
@Service
public class UsageRecordServiceImpl extends ServiceImpl<UsageRecordMapper, UsageRecord> implements UsageRecordService {

    @Override
    public void recordUsage(Long userId, Long apiKeyId, String model,
                           Integer inputTokens, Integer outputTokens,
                           BigDecimal cost, Integer latencyMs,
                           String ip, String userAgent, String requestId,
                           boolean success, String errorMessage) {
        UsageRecord record = new UsageRecord();
        record.setUserId(userId);
        record.setApiKeyId(apiKeyId);
        record.setModel(model);
        record.setInputTokens(inputTokens);
        record.setOutputTokens(outputTokens);
        record.setTotalTokens((inputTokens != null ? inputTokens : 0) + (outputTokens != null ? outputTokens : 0));
        record.setCost(cost);
        record.setLatencyMs(latencyMs);
        record.setIpAddress(ip);
        record.setUserAgent(userAgent);
        record.setRequestId(requestId);
        record.setStatus(success ? 1 : 0);
        record.setErrorMessage(errorMessage);
        record.setCreateTime(LocalDateTime.now());

        this.save(record);
    }

    @Override
    public List<UsageRecord> getUserUsageRecords(Long userId, String startDate, String endDate, int page, int pageSize) {
        LambdaQueryWrapper<UsageRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UsageRecord::getUserId, userId)
               .orderByDesc(UsageRecord::getCreateTime);

        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(UsageRecord::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(UsageRecord::getCreateTime, LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }

        int offset = (page - 1) * pageSize;
        wrapper.last("LIMIT " + offset + ", " + pageSize);

        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getUserUsageStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 总使用量
        Long totalUsed = this.baseMapper.selectSumTokens(userId);
        stats.put("totalUsed", totalUsed != null ? totalUsed : 0);

        // 今日使用
        stats.put("todayUsed", getTodayUsage(userId));

        // 本月使用
        stats.put("monthUsed", getMonthUsage(userId));

        // 总消费
        BigDecimal totalCost = this.baseMapper.selectSumCost(userId);
        stats.put("totalCost", totalCost != null ? totalCost : BigDecimal.ZERO);

        // 调用次数
        Long callCount = this.baseMapper.selectCount(userId);
        stats.put("callCount", callCount != null ? callCount : 0);

        return stats;
    }

    @Override
    public Integer getTodayUsage(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        Integer count = this.baseMapper.selectSumTokensByDateRange(userId, startOfDay, endOfDay);
        return count != null ? count : 0;
    }

    @Override
    public Integer getMonthUsage(Long userId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().atTime(LocalTime.MAX);

        Integer count = this.baseMapper.selectSumTokensByDateRange(userId, startOfMonth, endOfMonth);
        return count != null ? count : 0;
    }
}
