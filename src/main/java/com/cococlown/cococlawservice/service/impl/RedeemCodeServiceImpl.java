package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.entity.RedeemCode;
import com.cococlown.cococlawservice.entity.UserToken;
import com.cococlown.cococlawservice.mapper.RedeemCodeMapper;
import com.cococlown.cococlawservice.service.RedeemCodeService;
import com.cococlown.cococlawservice.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 兑换码Service实现
 */
@Service
public class RedeemCodeServiceImpl extends ServiceImpl<RedeemCodeMapper, RedeemCode> implements RedeemCodeService {

    @Autowired
    private UserTokenService userTokenService;

    @Override
    @Transactional
    public String redeemCode(Long userId, String code) {
        RedeemCode redeemCode = this.getOne(
            new LambdaQueryWrapper<RedeemCode>()
                .eq(RedeemCode::getCode, code.toUpperCase())
        );

        if (redeemCode == null) {
            return "兑换码不存在";
        }

        if (redeemCode.getStatus() != 1) {
            if (redeemCode.getStatus() == 0) return "兑换码已禁用";
            if (redeemCode.getStatus() == 2) return "兑换码已使用完";
            if (redeemCode.getStatus() == 3) return "兑换码已过期";
        }

        if (redeemCode.getExpireTime() != null && redeemCode.getExpireTime().isBefore(LocalDateTime.now())) {
            redeemCode.setStatus(3);
            this.updateById(redeemCode);
            return "兑换码已过期";
        }

        if (redeemCode.getUsedCount() >= redeemCode.getMaxUseCount()) {
            redeemCode.setStatus(2);
            this.updateById(redeemCode);
            return "兑换码已使用完";
        }

        // 执行兑换
        if (redeemCode.getType() == 1) {
            // Token兑换
            userTokenService.addToken(userId, redeemCode.getTokenAmount());
        } else if (redeemCode.getType() == 2) {
            // 套餐兑换（这里简化处理，实际应创建订阅记录）
            // TODO: 实现套餐订阅逻辑
        }

        // 更新使用记录
        redeemCode.setUsedCount(redeemCode.getUsedCount() + 1);
        redeemCode.setUserId(userId);
        redeemCode.setBindTime(LocalDateTime.now());

        if (redeemCode.getUsedCount() >= redeemCode.getMaxUseCount()) {
            redeemCode.setStatus(2);
        }

        this.updateById(redeemCode);

        return "兑换成功";
    }

    @Override
    @Transactional
    public List<String> generateCodes(Integer type, Integer value, Integer count, Integer expireDays) {
        List<String> codes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String code = generateUniqueCode();
            RedeemCode redeemCode = new RedeemCode();
            redeemCode.setCode(code);
            redeemCode.setType(type);
            if (type == 1) {
                redeemCode.setTokenAmount(value);
            } else if (type == 2) {
                redeemCode.setPackageDays(value);
            }
            redeemCode.setMaxUseCount(1);
            redeemCode.setExpireTime(LocalDateTime.now().plusDays(expireDays != null ? expireDays : 30));
            redeemCode.setStatus(1);
            redeemCode.setCreateTime(LocalDateTime.now());

            this.save(redeemCode);
            codes.add(code);
        }

        return codes;
    }

    @Override
    public List<RedeemCode> getUserRedeemHistory(Long userId) {
        return this.list(
            new LambdaQueryWrapper<RedeemCode>()
                .eq(RedeemCode::getUserId, userId)
                .orderByDesc(RedeemCode::getBindTime)
        );
    }

    @Override
    public boolean validateCode(String code) {
        RedeemCode redeemCode = this.getOne(
            new LambdaQueryWrapper<RedeemCode>()
                .eq(RedeemCode::getCode, code.toUpperCase())
                .eq(RedeemCode::getStatus, 1)
        );
        return redeemCode != null;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (this.count(new LambdaQueryWrapper<RedeemCode>().eq(RedeemCode::getCode, code)) > 0);
        return code;
    }
}
