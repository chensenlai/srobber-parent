Master Branch: [![Travis CI](https://travis-ci.org/xingePush/xinge-api-java.svg?branch=master)](https://travis-ci.org/xingePush/xinge-api-java)

# xingePushJavaApi
## 概述
[信鸽](http://xg.qq.com) 是腾讯云提供的一款支持**百亿级**消息的移动App推送平台，开发者可以调用Java SDK访问信鸽推送服务。

目前最新的SDK版本是1.1.8，请及时更新SDK版本以获取最新的功能特性。

## 引用SDK
- 方法一：使用maven库(**推荐**)
    - 在项目pom.xml中引入依赖项
    
```xml

	<repositories>
		<repository>
			<id>xingePush</id>
			<url>https://raw.githubusercontent.com/xingePush/maven-repository/snapshot/</url>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>com.github.xingePush</groupId>
			<artifactId>xinge</artifactId>
			<version>1.2.0-SNAPSHOT</version>
		</dependency>
	</dependencies>
```
- 方法二：checkout源代码
    - git clone https://github.com/xingePush/xinge-api-java.git
 
- 方法三：下载库文件
    - 在[信鸽官网](http://xg.qq.com/xg/ctr_index/download)下载最新版本的包
    - 使用时引用XingeApp包即可

## 接口说明
信鸽提供的主要推送和查询接口包括3种

### 创建推送任务
- pushSingleDevice 推送消息给单个设备
- pushSingleAccount 推送消息给单个账号
- pushAccountList 推送消息给多个账号
- pushAllDevice 推送消息给单个app的所有设备
- pushTags 推送消息给tags指定的设备
- createMultipush创建大批量推送消息
- pushAccountListMultiple推送消息给大批量账号(可多次)
- pushDeviceListMultiple推送消息给大批量设备(可多次)

### 异步查询推送状态
- queryPushStatus查询群发消息发送状态
- cancelTimingPush取消尚未推送的定时消息

### 查询/更改账户和标签
- queryDeviceCount查询应用覆盖的设备数
- queryTags 查询应用的tags
- BatchSetTag 批量为token设置标签
- BatchDelTag 批量为token删除标签
- queryTokenTags 查询token的tags
- queryTagTokenNum 查询tag下token的数目
- queryInfoOfToken 查询token的相关信息
- queryTokensOfAccount 查询account绑定的token
- deleteTokenOfAccount 删除account绑定的token
- deleteAllTokensOfAccount 删除account绑定的所有token
