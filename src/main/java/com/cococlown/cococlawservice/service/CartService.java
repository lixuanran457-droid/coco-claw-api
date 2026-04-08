package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.CartDTO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 获取用户购物车列表
     */
    List<CartDTO> getCartList(Long userId);

    /**
     * 添加商品到购物车
     */
    boolean addToCart(Long userId, Long skillId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    boolean updateCartQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 从购物车移除商品
     */
    boolean removeFromCart(Long userId, Long cartId);

    /**
     * 清空购物车
     */
    boolean clearCart(Long userId);

    /**
     * 获取购物车商品数量
     */
    Integer getCartCount(Long userId);
}
