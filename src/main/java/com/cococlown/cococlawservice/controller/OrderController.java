package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据ID获取订单
     */
    @ApiOperation("根据ID获取订单")
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 创建订单
     */
    @ApiOperation("创建订单")
    @PostMapping
    public Result<Boolean> createOrder(@RequestBody Order order) {
        boolean success = orderService.createOrder(order);
        if (success) {
            return Result.success("创建成功", true);
        }
        return Result.error("创建失败");
    }
}
