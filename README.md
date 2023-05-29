## 雨滴网上预约挂号平台

### 项目简介：

雨滴网上预约挂号平台是一项致力于方便人们就医的服务，旨在为患者提供轻松、便捷、高效的挂号服务。该平台采用了Spring Cloud微服务架构和前后端分离技术，管理员系统支持医院管理、会员管理、订单管理等多种功能，用户系统则实现了注册登录、预约挂号等服务。通过该平台，患者可以更加便捷地安排就医时间，同时也为医院提供了更有效率的管理手段。  

### 技术架构

SpringBoot+SpringCloud+MyBatis-Plus+Redis+RabbitMQ+HTTPClient+Swagger2+Nginx+Lombok+MySQL+MongoDB

### 业务流程

![image](https://github.com/Rain-code-J/Raindrops-Booking-Platform/assets/91371623/4b55248e-6699-4693-8259-2b9e3d5e74c6)


### 系统架构

![image](https://github.com/Rain-code-J/Raindrops-Booking-Platform/assets/91371623/f28db67d-9220-4ae6-bab1-a36aa9ce04a4)


## 快速开始

### 前置

运行 SQL 文件夹中的 sql 脚本文件

### 运行

- 运行 Nacos
- 运行 Redis 
- 运行 MongoDB
- 运行 RabbitMQ 
- 使用 IDEA 一键运行`service`
- 运行 前端 

### 后端

- 修改每个模块下的 application.yml 文件
- 修改其中的 MySQL 数据库地址、 Redis 、RabbitMQ 、 Nacos 、 MongoDB 
- 访问 http://localhost:9998/ 给医院、科室、排班添加数据（数据在 `数据`文件夹中）

### 前端

- 管理员系统

- - npm install 
  - 查看 .env.development 文件中的地址是否和后端 `gateway`模块中的地址所匹配
  - npm run dev
  - 如果 node 版本过高 ，将 package.json 中 dev 那行改为 `"dev": "set NODE_OPTIONS=--openssl-legacy-provider && vue-cli-service serve",`
  - 请看`运行`
  - 访问  http://localhost:9528/ 

- 用户系统

- - npm install 
  - npm run dev
  - 请看`运行`
  - 访问 http://localhost:9528/
