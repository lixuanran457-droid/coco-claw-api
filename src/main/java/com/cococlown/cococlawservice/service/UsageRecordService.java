package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.UsageRecord;
import java.util.List;
import java.util.Map;

/**
 * 使用记录Service接口
 */
public interface UsageRecordService extends IService<UsageRecord> {

    /**
     * 记录使用
     * @param userId 用户ID
     * @param apiKeyId API密钥ID
     * @param model 模型
     * @param inputTokens 输入Token数
     * @param outputTokens 输出Token数
     * @param cost 消费
     * @param latencyMs 延迟
     * @param ip IP
     * @param userAgent User-Agent
     * @param requestId 请求ID
     * @param success 是否成功
     * @param errorMessage 错误信息
     */
    void recordUsage(Long userId, Long apiKeyId, String model, 
                     Integer inputTokens, Integer outputTokens,
                     java.math.BigDecimal cost, Integer latencyMs,
                     String ip, String userAgent, String requestId,
                     boolean success, String errorMessage);

    /**
     * 获取用户使用记录
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param pageSize 每页数量
     * @return 使用记录列表
     */
    List<UsageRecord> getUserUsageRecords(Long userId, String startDate, String endDate, int page, int pageSize);

    /**
     * 获取用户使用统计
     * @param userId 用户ID
     * @return 统计数据
     */
    Map<String, Object> getUserUsageStats(Long userId);

    /**
     * 获取今日使用量
     * @param userId 用户ID
     * @return 今日使用量
     */
    Integer getTodayUsage(Long userId);

    /**
     * 获取本月使用量
     * @param userId 用户ID
     * @return 本月使用量
     */
    Integer getMonthUsage(Long userId);
}
