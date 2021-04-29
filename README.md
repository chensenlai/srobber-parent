# srobber-parent 项目脚手架
	对一些框架代码进行定制化二次封装增强, 达到规范约束和开箱即用目的。
	代码持续更新完善,有任何问题请邮件沟通。
    代码属于实验性质, 不对正确性做保证。
## core
>项目内核 
### common
1. config 提供系统默认配置。
2. entity,enums,status,envent,result 提供基类约束规范。
3. exception 提供业务常见异常类型, 异常处理器会针对特殊异常类型返回响应。
4. util 提供如加密,签名,日期格式化,字符串操作等常见工具。
5. validator 提供如手机号码,身份证等常见校验器。
6. trace 利用logger MDC标记用户, 追踪用户请求。
7. jump 定义统一跳转协议, 用于APP根据跳转协议跳到指定类型的指定页面。
8. spring 提供跟spring和springMVC等相关定制化增强。
### excel
### log
### swagger
### database
### rocketmq
### ratelimter
### redis
### cache
### mybatis

### auth

## manager
> 依赖第三方能力封装, 可屏蔽接口差异, 无缝切换第三方能力提供商。
### sms
### push
### phone
### realname
### pay
### oss
### im

## third
> 依赖第三方平台接口封装, 开箱即用。 
### aliyun
### wechat
### apple
### agora