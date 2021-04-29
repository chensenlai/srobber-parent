# srobber项目脚手架
	针对业务开发中一些实践对框架代码二次封装增强, 代码属于实验性质, 不对正确性做保证。
	代码持续更新完善,有任何问题请邮件沟通。

## core
1. config 提供系统默认配置。
2. entity,enums,status,envent,result 提供基类约束规范。
3. exception 提供业务常见异常类型, 异常处理器会针对特殊异常类型返回响应。
4. util 提供如加密,签名,日期格式化,字符串操作等常见工具。
5. validator 提供如手机号码,身份证等常见校验器。
6. trace 利用logger MDC标记用户, 追踪用户请求。
7. jump 定义统一跳转协议, 用于APP根据跳转协议跳到指定类型的指定页面。
8. mybatis 提供对mybatis插件,类型结果集等功能增强。
9. spring 提供跟spring和springMVC等相关定制化增强。

## excel

## log

## swagger

## database

## rocketmq

## ratelimter

## redis

## cache

## auth
