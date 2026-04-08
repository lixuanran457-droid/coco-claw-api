package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.CouponDTO;
import com.cococlown.cococlawservice.dto.UserCouponDTO;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {

    /**
     * 获取可领取的优惠券列表
     */
    List<CouponDTO> getAvailableCoupons();

    /**
     * 领取优惠券
     */
    boolean receiveCoupon(Long userId, Long couponId);

    /**
     * 获取用户已领取的优惠券
     */
    List<UserCouponDTO> getUserCoupons(Long userId, Integer status);

    /**
     * 使用优惠券（下单时调用）
     */
    boolean useCoupon(Long userId, Long userCouponId, Long orderId);

    /**
     * 获取用户可用优惠券（根据订单金额）
     */
    List<UserCouponDTO> getAvailableUserCoupons(Long userId, java.math.BigDecimal orderAmount);

    /**
     * 后台管理：创建优惠券
     */
    boolean createCoupon(com.cococlown.cococlawservice.entity.Coupon coupon);

    /**
     * 后台管理：更新优惠券
     */
    boolean updateCoupon(com.cococlown.cococlawservice.entity.Coupon coupon);

    /**
     * 后台管理：删除优惠券
     */
    boolean deleteCoupon(Long couponId);

    /**
     * 后台管理：发放优惠券给用户
     */
    boolean grantCouponToUsers(Long couponId, List<Long> userIds);
}
