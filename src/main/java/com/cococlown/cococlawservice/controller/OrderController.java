package com.cococlown.cococlawservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.OrderCreateDTO;
import com.cococlown.cococlawservice.dto.OrderDTO;
import com.cococlown.cococlawservice.dto.OrderQueryDTO;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器 - P3模块
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @ApiOperation("创建订单")
    @PostMapping
    public Result<Order> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody OrderCreateDTO dto) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            Order order = orderService.createOrder(userId, dto);
            return Result.success("订单创建成功", order);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单列表（用户端）
     */
    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<IPage<OrderDTO>> getOrderList(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(page);
        query.setPageSize(pageSize);
        query.setStatus(status);
        
        IPage<OrderDTO> result = orderService.getUserOrderPage(userId, query);
        return Result.success(result);
    }

    /**
     * 获取订单详情
     */
    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDTO> getOrderDetail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        OrderDTO order = orderService.getOrderDetail(userId, id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            boolean success = orderService.cancelOrder(userId, id);
            return Result.success("订单已取消", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @ApiOperation("确认收货")
    @PostMapping("/{id}/confirm")
    public Result<Boolean> confirmReceive(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            boolean success = orderService.confirmReceive(userId, id);
            return Result.success("已确认收货", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 申请退款
     */
    @ApiOperation("申请退款")
    @PostMapping("/{id}/refund")
    public Result<Boolean> applyRefund(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            boolean success = orderService.applyRefund(userId, id, reason);
            return Result.success("退款申请已提交", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除订单
     */
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable Long id) {
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        try {
            boolean success = orderService.deleteOrder(userId, id);
            return Result.success("订单已删除", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 后台管理接口 ====================

    /**
     * 后台：获取所有订单
     */
    @ApiOperation("后台：获取所有订单")
    @GetMapping("/admin/list")
    public Result<IPage<OrderDTO>> getAdminOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        
        OrderQueryDTO query = new OrderQueryDTO();
        query.setPage(page);
        query.setPageSize(pageSize);
        query.setStatus(status);
        query.setKeyword(keyword);
        
        IPage<OrderDTO> result = orderService.getAdminOrderPage(query);
        return Result.success(result);
    }

    /**
     * 后台：更新订单状态
     */
    @ApiOperation("后台：更新订单状态")
    @PutMapping("/admin/{id}/status")
    public Result<Boolean> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            boolean success = orderService.updateOrderStatus(id, status);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 后台：处理退款
     */
    @ApiOperation("后台：处理退款")
    @PostMapping("/admin/{id}/refund")
    public Result<Boolean> processRefund(@PathVariable Long id) {
        try {
            boolean success = orderService.processRefund(id);
            return Result.success("退款处理成功", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 游客订单查询 ====================

    /**
     * 游客：发送查单验证码
     */
    @ApiOperation("游客：发送查单验证码")
    @PostMapping("/guest/send-captcha")
    public Result<String> sendGuestQueryCaptcha(@RequestParam String email) {
        try {
            orderService.sendGuestQueryCaptcha(email);
            return Result.success("验证码已发送", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 游客：通过邮箱查询订单
     */
    @ApiOperation("游客：查询订单")
    @GetMapping("/guest/list")
    public Result<IPage<OrderDTO>> getGuestOrderList(
            @RequestParam String email,
            @RequestParam String captcha,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        try {
            OrderQueryDTO query = new OrderQueryDTO();
            query.setPage(page);
            query.setPageSize(pageSize);
            query.setStatus(status);

            IPage<OrderDTO> result = orderService.getGuestOrderPage(email, captcha, query);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
