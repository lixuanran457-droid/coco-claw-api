# COCO-CLAW API

> COCO-CLAW 技能服务平台后端 API

## 技术栈

- Java 1.8
- Spring Boot 2.7.18
- MySQL 8.0
- Redis
- Nacos 注册中心
- MyBatis-Plus
- Lombok
- Maven

## 项目结构

```
coco-claw-api/
├── src/main/java/com/cococlown/cococlawservice/
│   ├── controller/          # 控制器层
│   ├── service/            # 服务层
│   │   └── impl/           # 服务实现
│   ├── mapper/             # MyBatis Mapper
│   ├── entity/             # 实体类
│   ├── dto/                # 数据传输对象
│   ├── config/             # 配置类
│   └── common/             # 通用类
├── src/main/resources/
│   ├── application.yml      # 应用配置
│   └── mapper/             # Mapper XML
├── sql/                     # SQL脚本
│   ├── 01_schema.sql       # 建表脚本
│   ├── 02_category_data.sql # 分类数据
│   ├── 03_skill_data.sql   # SKILL数据
│   └── 04_user_data.sql    # 用户和订单数据
└── pom.xml                 # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis
- Nacos

### 2. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行建表脚本
source sql/01_schema.sql
source sql/02_category_data.sql
source sql/03_skill_data.sql
source sql/04_user_data.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coco_claw?useUnicode=true&characterEncoding=utf-8
    username: root
    password: your_password
```

### 4. 编译运行

```bash
# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar target/coco-claw-api-1.0.0.jar

# 或使用Maven运行
mvn spring-boot:run
```

### 5. 访问服务

- API地址: http://localhost:8080
- 健康检查: http://localhost:8080/api/health
- API文档: http://localhost:8080/swagger-ui.html

## API接口

### SKILL管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/skills | 获取SKILL列表（支持分页、分类筛选、搜索） |
| GET | /api/skills/{id} | 获取SKILL详情 |
| POST | /api/skills | 创建SKILL |
| PUT | /api/skills/{id} | 更新SKILL |
| DELETE | /api/skills/{id} | 删除SKILL |
| PUT | /api/skills/{id}/status | 切换上下架状态 |
| GET | /api/skills/featured | 获取精选SKILL |

### 分类管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/categories | 获取全部分类 |
| GET | /api/categories/{id} | 获取分类详情 |

### 用户管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users/{id} | 根据ID获取用户 |
| GET | /api/users/username/{username} | 根据用户名获取用户 |

### 订单管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/orders/{id} | 获取订单详情 |
| POST | /api/orders | 创建订单 |

## API响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## Nacos 配置

- 地址: http://124.243.191.159:8848/nacos
- 用户名: nacos
- 密码: 9Dz1Uz0n_QK9v7jS8_VfDt1Tn9

## 数据库

- 数据库名: coco_claw
- 用户名: root
- 密码: root

## License

MIT License
