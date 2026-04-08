package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.CartDTO;
import com.cococlown.cococlawservice.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器 - P1核心模块
 */
@Api(tags = "购物车管理")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车列表
     */
    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public Result<List<CartDTO>> getCartList(@RequestHeader("X-User-Id") Long userId) {
        List<CartDTO> list = cartService.getCartList(userId);
        return Result.success(list);
    }

    /**
     * 添加到购物车
     */
    @ApiOperation("添加到购物车")
    @PostMapping("/add")
    public Result<Boolean> addToCart(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long skillId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            boolean success = cartService.addToCart(userId, skillId, quantity);
            return Result.success("添加成功", success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @ApiOperation("更新购物车商品数量")
    @PutMapping("/update")
    public Result<Boolean> updateQuantity(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long cartId,
            @RequestParam Integer quantity) {
        boolean success = cartService.updateCartQuantity(userId, cartId, quantity);
        return Result.success(success);
    }

    /**
     * 从购物车移除
     */
    @ApiOperation("从购物车移除")
    @DeleteMapping("/remove/{cartId}")
    public Result<Boolean> removeFromCart(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cartId) {
        boolean success = cartService.removeFromCart(userId, cartId);
        return Result.success(success);
    }

    /**
     * 清空购物车
     */
    @ApiOperation("清空购物车")
    @DeleteMapping("/clear")
    public Result<Boolean> clearCart(@RequestHeader("X-User-Id") Long userId) {
        boolean success = cartService.clearCart(userId);
        return Result.success(success);
    }

    /**
     * 获取购物车数量
     */
    @ApiOperation("获取购物车数量")
    @GetMapping("/count")
    public Result<Integer> getCartCount(@RequestHeader("X-User-Id") Long userId) {
        Integer count = cartService.getCartCount(userId);
        return Result.success(count);
    }
}
