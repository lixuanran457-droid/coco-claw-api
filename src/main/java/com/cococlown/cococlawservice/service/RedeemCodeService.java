package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.entity.RedeemCode;
import java.util.List;

/**
 * 兑换码Service接口
 */
public interface RedeemCodeService extends IService<RedeemCode> {

    /**
     * 使用兑换码
     * @param userId 用户ID
     * @param code 兑换码
     * @return 结果信息
     */
    String redeemCode(Long userId, String code);

    /**
     * 生成兑换码（管理员）
     * @param type 类型
     * @param value 值
     * @param count 数量
     * @param expireDays 过期天数
     * @return 生成的兑换码列表
     */
    List<String> generateCodes(Integer type, Integer value, Integer count, Integer expireDays);

    /**
     * 获取用户的兑换记录
     * @param userId 用户ID
     * @return 兑换记录列表
     */
    List<RedeemCode> getUserRedeemHistory(Long userId);

    /**
     * 验证兑换码
     * @param code 兑换码
     * @return 是否有效
     */
    boolean validateCode(String code);
}
