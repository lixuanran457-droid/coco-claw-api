package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cococlown.cococlawservice.dto.OrderCreateDTO;
import com.cococlown.cococlawservice.dto.OrderDTO;
import com.cococlown.cococlawservice.dto.OrderQueryDTO;
import com.cococlown.cococlawservice.entity.Order;
import com.cococlown.cococlawservice.entity.Skill;
import com.cococlown.cococlawservice.mapper.OrderMapper;
import com.cococlown.cococlawservice.mapper.SkillMapper;
import com.cococlown.cococlawservice.service.AddressService;
import com.cococlown.cococlawservice.service.CouponService;
import com.cococlown.cococlawservice.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    /**
     * 订单过期时间（分钟）
     */
    private static final int ORDER_EXPIRE_MINUTES = 30;

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateDTO dto) {
        List<Order> orders = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // 处理收货地址
        String receiverName = null;
        String receiverPhone = null;
        String deliveryAddress = null;
        
        if (dto.getAddressId() != null) {
            var address = addressService.getAddressDetail(userId, dto.getAddressId());
            if (address != null) {
                receiverName = address.getReceiverName();
                receiverPhone = address.getPhone();
                deliveryAddress = address.getFullAddress();
            }
        }

        // 处理优惠券
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (dto.getCouponId() != null) {
            couponService.useCoupon(userId, dto.getCouponId(), null);
        }

        // 为每个技能创建订单
        for (Long skillId : dto.getSkillIds()) {
            Skill skill = skillMapper.selectById(skillId);
            if (skill == null || skill.getStatus() != 1) {
                throw new RuntimeException("商品[" + skillId + "]不存在或已下架");
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setOrderNo(generateOrderNo());
            order.setSkillId(skillId);
            order.setSkillName(skill.getName());
            order.setSkillIcon(skill.getIcon());
            order.setQuantity(1);
            order.setPrice(skill.getPriceType() == 0 ? BigDecimal.ZERO : skill.getPrice());
            order.setTotalAmount(skill.getPriceType() == 0 ? BigDecimal.ZERO : skill.getPrice());
            
            // 使用优惠券
            if (dto.getCouponId() != null && discountAmount.compareTo(BigDecimal.ZERO) == 0) {
                order.setCouponId(dto.getCouponId());
                order.setDiscountAmount(discountAmount);
            } else {
                order.setDiscountAmount(BigDecimal.ZERO);
            }
            
            order.setPayAmount(order.getTotalAmount().subtract(order.getDiscountAmount()));
            order.setPaymentMethod(dto.getPaymentMethod());
            order.setStatus(0); // 待支付
            order.setAddressId(dto.getAddressId());
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setDeliveryAddress(deliveryAddress);
            order.setRemark(dto.getRemark());
            
            LocalDateTime now = LocalDateTime.now();
            order.setCreateTime(now);
            order.setExpireTime(now.plusMinutes(ORDER_EXPIRE_MINUTES));
            
            orderMapper.insert(order);
            orders.add(order);
        }

        return orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    public IPage<OrderDTO> getUserOrderPage(Long userId, OrderQueryDTO query) {
        Page<Order> page = new Page<>(query.getPage(), query.getPageSize());
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        
        // 关键词搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Order::getOrderNo, query.getKeyword())
                             .or()
                             .like(Order::getSkillName, query.getKeyword()));
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        Page<OrderDTO> dtoPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderDTO> dtoList = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            dtoList.add(convertToDTO(order));
        }
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public OrderDTO getOrderDetail(Long userId, Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getUserId, userId);
        
        Order order = orderMapper.selectOne(wrapper);
        return order != null ? convertToDTO(order) : null;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long userId, Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getUserId, userId);
        
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有待支付状态才能取消
        if (order.getStatus() != 0) {
            throw new RuntimeException("只有待支付的订单才能取消");
        }
        
        order.setStatus(4); // 已取消
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean confirmReceive(Long userId, Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getUserId, userId);
        
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有已支付状态才能确认收货
        if (order.getStatus() != 2) {
            throw new RuntimeException("只有已支付的订单才能确认收货");
        }
        
        order.setStatus(3); // 已完成
        order.setCompleteTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean applyRefund(Long userId, Long orderId, String reason) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getUserId, userId);
        
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有已支付或已完成的订单才能申请退款
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new RuntimeException("只有已支付或已完成的订单才能申请退款");
        }
        
        order.setStatus(5); // 退款中
        order.setRefundReason(reason);
        order.setRefundApplyTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long userId, Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getUserId, userId);
        
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只能删除已取消、已退款、已完成的订单
        if (order.getStatus() != 3 && order.getStatus() != 4 && order.getStatus() != 6) {
            throw new RuntimeException("只能删除已取消、已完成或已退款的订单");
        }
        
        return orderMapper.delete(wrapper) > 0;
    }

    @Override
    public IPage<OrderDTO> getAdminOrderPage(OrderQueryDTO query) {
        Page<Order> page = new Page<>(query.getPage(), query.getPageSize());
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        
        // 关键词搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Order::getOrderNo, query.getKeyword())
                             .or()
                             .like(Order::getSkillName, query.getKeyword())
                             .or()
                             .like(Order::getReceiverName, query.getKeyword()));
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        IPage<Order> orderPage = orderMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        Page<OrderDTO> dtoPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderDTO> dtoList = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            dtoList.add(convertToDTO(order));
        }
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        // 特殊状态处理
        if (status == 2) { // 已支付
            order.setPayTime(LocalDateTime.now());
        } else if (status == 6) { // 已退款
            order.setRefundTime(LocalDateTime.now());
        }
        
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean processRefund(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 5) {
            throw new RuntimeException("订单不存在或不在退款状态");
        }
        
        // TODO: 调用支付平台退款接口
        
        order.setStatus(6); // 已退款
        order.setRefundTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "COCO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 转换为DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        dto.setStatusName(getStatusName(order.getStatus()));
        return dto;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待支付";
            case 1: return "支付中";
            case 2: return "已支付";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "退款中";
            case 6: return "已退款";
            default: return "未知";
        }
    }
}
