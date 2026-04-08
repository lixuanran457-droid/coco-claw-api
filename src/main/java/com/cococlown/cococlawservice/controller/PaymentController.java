package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.PaymentCreateDTO;
import com.cococlown.cococlawservice.dto.PaymentDTO;
import com.cococlown.cococlawservice.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器 - P1核心模块
 */
@Api(tags = "支付管理")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付订单（支持游客下单）
     */
    @ApiOperation("创建支付订单")
    @PostMapping("/create")
    public Result<PaymentDTO> createPayment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody PaymentCreateDTO dto) {
        try {
            PaymentDTO payment = paymentService.createPayment(userId, dto);
            return Result.success("支付订单创建成功", payment);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @ApiOperation("查询支付状态")
    @GetMapping("/status/{orderId}")
    public Result<PaymentDTO> getPaymentStatus(@PathVariable Long orderId) {
        try {
            PaymentDTO payment = paymentService.getPaymentStatus(orderId);
            return Result.success(payment);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付宝回调
     */
    @ApiOperation("支付宝回调")
    @PostMapping("/callback/alipay")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        try {
            paymentService.handlePaymentCallback("alipay", params);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    /**
     * 微信支付回调
     */
    @ApiOperation("微信支付回调")
    @PostMapping("/callback/wechat")
    public String wechatCallback(@RequestBody Map<String, String> params) {
        try {
            paymentService.handlePaymentCallback("wechat", params);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
        } catch (Exception e) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
    }

    /**
     * 取消支付
     */
    @ApiOperation("取消支付")
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancelPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId) {
        boolean success = paymentService.cancelPayment(orderId);
        return Result.success(success);
    }

    /**
     * 申请退款
     */
    @ApiOperation("申请退款")
    @PostMapping("/refund/{orderId}")
    public Result<Boolean> applyRefund(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        try {
            boolean success = paymentService.applyRefund(orderId, reason);
            return Result.success("退款申请已提交", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
