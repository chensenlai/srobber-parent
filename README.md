# srobber-parent 项目脚手架
	1. 对一些框架代码进行定制化二次封装增强, 达到规范约束和开箱即用目的。
    2. 代码属于实验性质, 不对正确性做保证。
    3. 代码持续更新完善,文档会持续更新,有任何问题请邮件沟通。
## core
>项目内核 
### common
1. config 提供系统默认配置。
2. entity,enums,status,envent,result 提供基类约束规范。
3. exception 提供业务常见异常类型, 异常处理器会针对特殊异常类型返回响应。
4. util 提供如加密,签名,日期格式化,字符串操作等常见工具。
5. validator 提供如手机号码,身份证等常见校验器。
6. trace 利用logger MDC标记用户, 追踪用户请求。
7. spring 提供跟spring和springMVC等相关定制化增强。
### swagger
### excel
### log
### mybatis
### database
### auth
### jump
### redis
### cache
### ratelimter
### rocketmq

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