package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cococlown.cococlawservice.dto.CartDTO;
import com.cococlown.cococlawservice.entity.Cart;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.mapper.CartMapper;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Override
    public List<CartDTO> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        wrapper.orderByDesc(Cart::getCreateTime);
        
        List<Cart> carts = cartMapper.selectList(wrapper);
        List<CartDTO> result = new ArrayList<>();

        for (Cart cart : carts) {
            Skill skill = skillMapper.selectById(cart.getSkillId());
            if (skill != null && skill.getStatus() == 1) { // 只显示上架商品
                CartDTO dto = new CartDTO();
                dto.setId(cart.getId());
                dto.setSkillId(skill.getId());
                dto.setSkillName(skill.getName());
                dto.setSkillIcon(skill.getIcon());
                dto.setQuantity(cart.getQuantity());
                dto.setPrice(cart.getPrice());
                dto.setTotalPrice(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean addToCart(Long userId, Long skillId, Integer quantity) {
        // 检查技能是否存在且上架
        Skill skill = skillMapper.selectById(skillId);
        if (skill == null || skill.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }

        // 检查购物车中是否已存在该商品
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getSkillId, skillId);
        Cart existingCart = cartMapper.selectOne(wrapper);

        if (existingCart != null) {
            // 已存在，增加数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            return cartMapper.updateById(existingCart) > 0;
        } else {
            // 不存在，新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setSkillId(skillId);
            cart.setQuantity(quantity);
            // 使用当前价格（实际项目可能需要记录历史价格）
            cart.setPrice(skill.getPriceType() == 0 ? BigDecimal.ZERO : skill.getPrice());
            return cartMapper.insert(cart) > 0;
        }
    }

    @Override
    public boolean updateCartQuantity(Long userId, Long cartId, Integer quantity) {
        LambdaUpdateWrapper<Cart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Cart::getId, cartId)
               .eq(Cart::getUserId, userId)
               .set(Cart::getQuantity, quantity);
        return cartMapper.update(null, wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean removeFromCart(Long userId, Long cartId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getId, cartId)
               .eq(Cart::getUserId, userId);
        return cartMapper.delete(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return cartMapper.delete(wrapper) > 0;
    }

    @Override
    public Integer getCartCount(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return Math.toIntExact(cartMapper.selectCount(wrapper));
    }
}
