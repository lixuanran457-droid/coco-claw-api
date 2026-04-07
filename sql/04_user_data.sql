USE coco_claw;

-- =====================================================
-- 插入用户示例数据
-- =====================================================
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138000', 'admin@cococlaw.com', 1),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13900139000', 'test@cococlaw.com', 1);

-- =====================================================
-- 插入订单示例数据
-- =====================================================
INSERT INTO `order` (`user_id`, `order_no`, `total_amount`, `status`) VALUES
(1, 'ORD20240101100001', 298.00, 1),
(1, 'ORD20240101100002', 99.00, 2),
(2, 'ORD20240102100003', 199.00, 1);

-- =====================================================
-- 插入订单项示例数据
-- =====================================================
INSERT INTO `order_item` (`order_id`, `skill_id`, `price`, `quantity`) VALUES
(1, 1, 199.00, 1),
(1, 2, 99.00, 1),
(2, 1, 99.00, 1),
(3, 5, 199.00, 1);
