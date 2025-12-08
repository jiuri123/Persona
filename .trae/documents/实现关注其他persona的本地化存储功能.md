# 实现关注其他persona的本地化存储功能

## 1. 修改OtherPersona.java
- 添加@Entity注解，指定表名为"other_personas"
- 添加@PrimaryKey注解，设置id为自增主键

## 2. 创建OtherPersonaDao.java
- 与UserPersonaDao类似的接口设计
- 包含插入、删除、获取所有other personas的方法
- 使用@Dao注解标记

## 3. 修改LocalDataSource.java
- 添加OtherPersonaDao的实例
- 添加对OtherPersona的增删改查方法
- 确保所有数据库操作通过线程池执行

## 4. 修改UserFollowedListRepository.java
- 修改为使用LocalDataSource进行数据存储
- 确保仓库只与LocalDataSource打交道，不直接与数据库打交道
- 添加从本地数据源加载数据的功能
- 修改addFollowedPersona和removeFollowedPersona方法，使其通过LocalDataSource进行数据操作

## 5. 修改AppDatabase.java
- 在@Database注解的entities中添加OtherPersona.class
- 添加OtherPersonaDao的抽象方法
- 移除MIGRATION_1_2迁移策略
- 添加fallbackToDestructiveMigration()方法
- 确保数据库配置仅用于开发和测试阶段

## 6. 检查并修改相关组件
- 确保OtherPersonaChatActivity、UserFollowedListAdapter等组件使用OtherPersona模型
- 确保UserPersonaChatActivity、UserPersonaCreateActivity等组件使用UserPersona模型

## 7. 验证实现
- 确保数据模型与组件正确关联
- 确保数据库配置适当调整
- 确保功能按预期工作

## 实现步骤
1. 修改OtherPersona.java文件，添加@Entity和@PrimaryKey注解
2. 创建OtherPersonaDao.java文件
3. 修改LocalDataSource.java文件，添加对OtherPersona的支持
4. 修改UserFollowedListRepository.java文件，使其使用LocalDataSource
5. 修改AppDatabase.java文件，更新数据库配置
6. 验证各组件使用的模型类是否正确
7. 测试功能是否正常工作