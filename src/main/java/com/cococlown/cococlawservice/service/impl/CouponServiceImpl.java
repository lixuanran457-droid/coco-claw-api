package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cococlown.cococlawservice.dto.CouponDTO;
import com.cococlown.cococlawservice.dto.UserCouponDTO;
import com.cococlown.cococlawservice.entity.Coupon;
import com.cococlown.cococlawservice.entity.UserCoupon;
import com.cococlown.cococlawservice.mapper.CouponMapper;
import com.cococlown.cococlawservice.mapper.UserCouponMapper;
import com.cococlown.cococlawservice.service.CouponService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券服务实现类
 */
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public List<CouponDTO> getAvailableCoupons() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
               .le(Coupon::getStartTime, LocalDateTime.now())
               .ge(Coupon::getEndTime, LocalDateTime.now())
               .apply("used_count < total_count");
        
        List<Coupon> coupons = couponMapper.selectList(wrapper);
        List<CouponDTO> result = new ArrayList<>();
        
        for (Coupon coupon : coupons) {
            CouponDTO dto = convertToDTO(coupon);
            dto.setCanReceive(true);
            result.add(dto);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券不存在或已下架");
        }
        
        // 检查是否在领取时间内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new RuntimeException("优惠券不在领取时间内");
        }
        
        // 检查是否已领完
        if (coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new RuntimeException("优惠券已领完");
        }
        
        // 检查用户已领取数量
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
               .eq(UserCoupon::getCouponId, couponId);
        long userReceivedCount = userCouponMapper.selectCount(wrapper);
        
        if (coupon.getPerLimit() > 0 && userReceivedCount >= coupon.getPerLimit()) {
            throw new RuntimeException("该优惠券每人限领" + coupon.getPerLimit() + "张");
        }
        
        // 领取优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setReceiveTime(now);
        userCoupon.setExpireTime(coupon.getEndTime());
        userCoupon.setStatus(0); // 未使用
        
        userCouponMapper.insert(userCoupon);
        
        // 更新优惠券已领取数量
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponMapper.updateById(coupon);
        
        return true;
    }

    @Override
    public List<UserCouponDTO> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        
        wrapper.orderByDesc(UserCoupon::getReceiveTime);
        
        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
        List<UserCouponDTO> result = new ArrayList<>();
        
        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon != null) {
                UserCouponDTO dto = new UserCouponDTO();
                BeanUtils.copyProperties(userCoupon, dto);
                dto.setCouponId(coupon.getId());
                dto.setName(coupon.getName());
                dto.setType(coupon.getType());
                dto.setValue(coupon.getValue());
                dto.setMinAmount(coupon.getMinAmount());
                
                // 设置类型名称
                dto.setTypeName(getTypeName(coupon.getType()));
                
                // 检查是否可用
                boolean canUse = userCoupon.getStatus() == 0 && 
                                 LocalDateTime.now().isBefore(userCoupon.getExpireTime());
                dto.setCanUse(canUse);
                
                result.add(dto);
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean useCoupon(Long userId, Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new RuntimeException("优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 0) {
            throw new RuntimeException("优惠券已使用或已过期");
        }
        
        if (LocalDateTime.now().isAfter(userCoupon.getExpireTime())) {
            // 更新过期状态
            LambdaUpdateWrapper<UserCoupon> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserCoupon::getId, userCouponId)
                        .set(UserCoupon::getStatus, 2);
            userCouponMapper.update(null, updateWrapper);
            throw new RuntimeException("优惠券已过期");
        }
        
        // 更新使用状态
        userCoupon.setStatus(1);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
        
        return true;
    }

    @Override
    public List<UserCouponDTO> getAvailableUserCoupons(Long userId, BigDecimal orderAmount) {
        List<UserCouponDTO> allCoupons = getUserCoupons(userId, 0);
        List<UserCouponDTO> available = new ArrayList<>();
        
        for (UserCouponDTO coupon : allCoupons) {
            // 检查过期
            if (LocalDateTime.now().isAfter(coupon.getExpireTime())) {
                continue;
            }
            // 检查门槛
            if (coupon.getMinAmount() != null && orderAmount.compareTo(coupon.getMinAmount()) >= 0) {
                available.add(coupon);
            }
        }
        
        return available;
    }

    @Override
    public boolean createCoupon(Coupon coupon) {
        coupon.setUsedCount(0);
        return couponMapper.insert(coupon) > 0;
    }

    @Override
    public boolean updateCoupon(Coupon coupon) {
        return couponMapper.updateById(coupon) > 0;
    }

    @Override
    @Transactional
    public boolean deleteCoupon(Long couponId) {
        // 删除优惠券
        couponMapper.deleteById(couponId);
        // 删除用户已领取的记录
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getCouponId, couponId);
        userCouponMapper.delete(wrapper);
        return true;
    }

    @Override
    @Transactional
    public boolean grantCouponToUsers(Long couponId, List<Long> userIds) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        
        for (Long userId : userIds) {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setReceiveTime(LocalDateTime.now());
            userCoupon.setExpireTime(coupon.getEndTime());
            userCoupon.setStatus(0);
            userCouponMapper.insert(userCoupon);
        }
        
        return true;
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        BeanUtils.copyProperties(coupon, dto);
        dto.setTypeName(getTypeName(coupon.getType()));
        return dto;
    }

    private String getTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "满减券";
            case 2: return "折扣券";
            case 3: return "无门槛券";
            default: return "未知";
        }
    }
}
