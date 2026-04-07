-- =====================================================
-- COCO-CLAW 示例数据脚本
-- =====================================================

USE coco_claw;

-- =====================================================
-- 插入分类数据
-- =====================================================
INSERT INTO `category` (`id`, `name`, `icon`, `sort_order`, `status`) VALUES
(1, '办公效率', 'https://cdn-icons-png.flaticon.com/128/3146/3146056.png', 1, 1),
(2, '开发编程', 'https://cdn-icons-png.flaticon.com/128/1055/1055666.png', 2, 1),
(3, '设计创意', 'https://cdn-icons-png.flaticon.com/128/1161/1161388.png', 3, 1),
(4, '数据分析', 'https://cdn-icons-png.flaticon.com/128/2136/2136972.png', 4, 1),
(5, 'AI人工智能', 'https://cdn-icons-png.flaticon.com/128/2103/2103633.png', 5, 1),
(6, '营销推广', 'https://cdn-icons-png.flaticon.com/128/1999/1999355.png', 6, 1),
(7, '视频剪辑', 'https://cdn-icons-png.flaticon.com/128/3736/3736394.png', 7, 1),
(8, '生活娱乐', 'https://cdn-icons-png.flaticon.com/128/2583/2583344.png', 8, 1);
