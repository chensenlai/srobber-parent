### 接口签名

签名算法描述:
1. 接口带上时间参数(参数名: _timestamp, 参数值: 当前时间)
2. 所有请求参数(注意body参数不参与签名), 按字典排序
3. 再加上签名key(参数名: _signKey, 参数值: 服务端提供), 格式化成key1=value1&key2=value2字符串格式
4. 32位MD5大写2点描述字符串, 生成签名.
5. 接口参数不要带上_signKey参数, 带上签名(参数名: _sign, 参数值: 第3步签名结果)

举个例子:
请求参数: userId=1&nickName=test  APP签名key: myappkey
1. userId=1&nickName=test&_timestamp=123456
2. _timestamp=123456&nickName=test&userId=1
3. _timestamp=123456&nickName=test&userId=1&_signKey=myappkey
4. DC6ECC6E695D44941EBB885CB30329AF
5. userId=1&nickName=test&_timestamp=123456&_sign=DC6ECC6E695D44941EBB885CB30329AF

### 自定义头部说明

|  header   | value  |
|  ----  | ----  |
| Auth-Token  | 用户登录token,客户端登录后统一把登录token放到自定义头部上 |
| Auth-User-Agent  | 客户端系统iOS/Android/H5/PC#应用市场#运营商#客户端信息#app版本号(int) |
| Device  | 客户端设备唯一码 |

说明:

1. token 30分钟有效, 客户端自行换token, 续延登录状态.
调用自动登录接口, 接口返回非200,则踢出登录,不再重试.
2. 客户端系统iOS/Android/H5/PC#应用市场#运营商#客户端信息#app版本号(int) 
* 客户端os-区分iOS和Android
* 应用市场-客户端自行定义(32字符长度限制)
* 运营商-CMCC(移动)/CUCC(联通)/CTCC(电信)等, 未知传unknown
* 客户端信息-手机型号等信息
* app版本号(int) - 客户端自行维护, APP升级就加1

举个例子:
Android#xiaoMi#CUCC#wifi#RedMi K30#101

表示红米K30的android手机从小米应用市场下载APP, 联通用户,使用wifi流量

### 客户端资源缓存管理
APP打开获取资源缓存定义列表, 客户端根据缓存版本号决定是否更新本地缓存.

### 统一跳转

### webview约束
禁用缓存

### 接口响应说明
单个处理:  

|  字段   | 含义  |
|  ----  | ----  |
| code  | 响应码 (见接口文档-系统管理响应码定义) |
| msg  | 操作提示 |
| data  | 业务数据(json格式) |

```
{
  "code": 200,
  "msg": "",
  "data" {
    "userId" : 10000001,
    "nickName" : "测试"
   } 
}
```
批量处理:

|  字段   | 含义  |
|  ----  | ----  |
| code  | 响应码 (见接口文档-系统管理响应码定义) |
| msg  | 操作提示 |
| total  | 记录处理数 |
| success  | 记录成功处理数 |
| dataList  | 处理数据列表 |

```
{
  "code": 200,
  "msg": "",
  "total": 100,
  "success": 100,
  "dataList" [
    {
        "userId" : 10000001,
        "nickName" : "测试"
    },
    ...
  ]
}
```