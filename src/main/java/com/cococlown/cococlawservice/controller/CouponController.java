package com.cococlown.cococlownsapi.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.CouponDTO;
import com.cococlown.cococlawservice.dto.UserCouponDTO;
import com.cococlown.cococlawservice.entity.Coupon;
import com.cococlown.cococlawservice.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券控制器 - P2模块
 */
@Api(tags = "优惠券管理")
@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 获取可领取的优惠券列表
     */
    @ApiOperation("获取可领取的优惠券列表")
    @GetMapping("/available")
    public Result<List<CouponDTO>> getAvailableCoupons() {
        List<CouponDTO> list = couponService.getAvailableCoupons();
        return Result.success(list);
    }

    /**
     * 领取优惠券
     */
    @ApiOperation("领取优惠券")
    @PostMapping("/receive/{couponId}")
    public Result<Boolean> receiveCoupon(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long couponId) {
        try {
            boolean success = couponService.receiveCoupon(userId, couponId);
            return Result.success("领取成功", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的优惠券
     */
    @ApiOperation("获取我的优惠券")
    @GetMapping("/my")
    public Result<List<UserCouponDTO>> getMyCoupons(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status) {
        List<UserCouponDTO> list = couponService.getUserCoupons(userId, status);
        return Result.success(list);
    }

    /**
     * 获取可用优惠券（结算时）
     */
    @ApiOperation("获取可用优惠券")
    @GetMapping("/available/my")
    public Result<List<UserCouponDTO>> getAvailableCoupons(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam BigDecimal orderAmount) {
        List<UserCouponDTO> list = couponService.getAvailableUserCoupons(userId, orderAmount);
        return Result.success(list);
    }

    /**
     * 后台：创建优惠券
     */
    @ApiOperation("创建优惠券")
    @PostMapping
    public Result<Boolean> createCoupon(@RequestBody Coupon coupon) {
        boolean success = couponService.createCoupon(coupon);
        return Result.success(success);
    }

    /**
     * 后台：更新优惠券
     */
    @ApiOperation("更新优惠券")
    @PutMapping("/{id}")
    public Result<Boolean> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        boolean success = couponService.updateCoupon(coupon);
        return Result.success(success);
    }

    /**
     * 后台：删除优惠券
     */
    @ApiOperation("删除优惠券")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCoupon(@PathVariable Long id) {
        boolean success = couponService.deleteCoupon(id);
        return Result.success(success);
    }

    /**
     * 后台：发放优惠券给指定用户
     */
    @ApiOperation("发放优惠券给指定用户")
    @PostMapping("/{id}/grant")
    public Result<Boolean> grantCouponToUsers(
            @PathVariable Long id,
            @RequestBody List<Long> userIds) {
        boolean success = couponService.grantCouponToUsers(id, userIds);
        return Result.success(success);
    }
}
