# 用户服务接口说明 v1.0.0

编写说明：

1. 文档版本号请与服务版本号保持一致；
2. 概述和主要功能部分由产品或架构编写；
3. 接口部分(三级目录)按模块(二级目录)进行归类，由开发人员按模板要求的格式编写。

## 目录

- [概述](#概述)
  - [主要功能](#主要功能)
  - [通讯方式](#通讯方式)
- [用户管理](#用户管理)
  - [查询用户列表](#查询用户列表)
  - [获取用户详情](#获取用户详情)
  - [新增用户](#新增用户)
  - [编辑用户](#编辑用户)
  - [删除用户](#删除用户)
  - [禁用用户](#禁用用户)
  - [启用用户](#启用用户)
  - [重置用户密码](#重置用户密码)
  - [邀请用户](#邀请用户)
  - [获取日志列表](#获取日志列表)
  - [获取日志详情](#获取日志详情)
- [ToC模块](#ToC模块)
  - [注册用户](#注册用户)
  - [获取我的详情](#获取我的详情)
  - [更新昵称](#更新昵称)
  - [更新手机号](#更新手机号)
  - [更新Email](#更新Email)
  - [更新头像](#更新头像)
  - [更新备注](#更新备注)
  - [修改密码](#修改密码)
  - [重置密码](#重置密码)
  - [设置支付密码](#设置支付密码)
  - [验证支付密码](#验证支付密码)
- [DTO类型说明](#DTO类型说明)

## 概述

Insight 用户服务是 Insight 基础服务的组成部分之一，提供用户管理及用户数据维护的相关服务。

### 主要功能

1. 用户管理模块，提供平台/租户的用户管理接口。
2. ToC模块，提供用户注册、用户数据维护相关接口。

### 通讯方式

接口支持 **HTTP/HTTPS** 协议的 **GET/POST/PUT/DELETE** 方法，支持 **URL Params** 、 **Path Variable** 或 **BODY** 传递接口参数。如使用 **BODY** 传参，则需使用 **Json** 格式的请求参数。接口 **/URL** 区分大小写，请求以及返回都使用 **UTF-8** 字符集进行编码，接口返回的数据封装为统一的 **Json** 格式。格式详见：[Reply数据类型](#Reply)。

>注：文档中所列举的类型皆为 **Java** 语言的数据类型，其它编程语言的的数据类型请自行对应。

建议在HTTP请求头中设置以下参数：

|参数名|参数值|
| ------------ | ------------ |
|Accept|application/json|
|Content-Type|application/json|

## 用户管理

### 查询用户列表

根据关键词(可选)分页查询与租户关联的用户列表。关键词可精确匹配用户编码/登录账号/手机号，模糊匹配姓名/昵称。如作为平台管理员登录，则可查询平台全部用户。

请求方法：**GET**

接口URL：**/base/user/manage/v1.0/users**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词,如查询全部用户,则必须制定查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|
|Boolean|all|是|是否查询全部用户,如为false,则只查询租户关联的用户.平台管理端此参数为false|

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|id|用户ID|
|String|code|用户编码|
|String|name|姓名/昵称|
|String|account|登录账号|
|String|mobile|手机号|
|String|remark|备注|
|Boolean|isBuiltin|是否内置|
|Boolean|isInvalid|是否失效|

请求示例：

```bash
curl "http://192.168.236.8:6200/base/user/manage/v1.0/users?all=false" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "815ee690d6a443cb938d207bbd1f489b",
      "code": "0001",
      "name": "测试",
      "account": "test",
      "mobile": null,
      "remark": null,
      "builtin": false,
      "invalid": false
    },
    {
      "id": "bb82f6bdfc5211e99bc30242ac110005",
      "code": null,
      "name": "系统管理员",
      "account": "admin",
      "mobile": null,
      "remark": null,
      "builtin": true,
      "invalid": false
    }
  ],
  "option": 2
}
```

[回目录](#目录)

### 获取用户详情

获取用户详情信息。

请求方法：**GET**

接口URL：**/base/user/manage/v1.0/users/{id}**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|用户ID|

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|id|用户ID|
|String|code|用户编码|
|String|name|姓名/昵称|
|String|account|登录账号|
|String|mobile|手机号|
|String|unionId|微信UnionID|
|Map|openId|关联微信OpenID|
|String|headImg|头像URL|
|String|remark|备注|
|Boolean|isBuiltin|是否内置|
|Boolean|isInvalid|是否失效|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求示例：

```bash
curl "http://192.168.236.8:6200/base/user/manage/v1.0/users/815ee690d6a443cb938d207bbd1f489b" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "cc790eaff0b740989649db46e3e3d88b",
    "code": "IU94934938",
    "name": "86-13867891234",
    "account": "3982dc4ffed645f1888d18062ccf1688",
    "mobile": "86-13867891234",
    "email": null,
    "unionId": null,
    "openId": null,
    "headImg": null,
    "remark": null,
    "creator": "86-13867891234",
    "creatorId": "cc790eaff0b740989649db46e3e3d88b",
    "createdTime": "2019-11-10 11:43:23",
    "builtin": false,
    "invalid": false
  },
  "option": null
}
```

[回目录](#目录)

### 新增用户

新增一个用户，并关联到创建人所登录的租户。

请求方法：**POST**

接口URL：**/base/user/manage/v1.0/users**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|name|是|姓名/昵称|
|String|account|是|登录账号|
|String|password|是|登录密码(MD5)|
|String|mobile|否|手机号|
|String|headImg|否|头像URL|
|String|remark|否|备注|

请求示例：

```bash
curl -X "POST" "http://192.168.236.8:6200/base/user/manage/v1.0/users" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "测试",
  "account": "test",
  "password": "c4ca4238a0b923820dcc509a6f75849b"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "299b7d142d624238a7b66300ab3b4f5a",
  "option": null
}
```

[回目录](#目录)

### 编辑用户

更新用户的姓名/昵称/登录账号/手机号/Email/头像URL和备注。属性为空表示该属性更新为NULL。

请求方法：**PUT**

接口URL：**/base/user/manage/v1.0/users**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|name|是|姓名/昵称|
|String|account|是|登录账号|
|String|mobile|否|手机号|
|String|email|否|邮箱|
|String|headImg|否|头像URL|
|String|remark|否|备注|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/manage/v1.0/users" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "email": "xan.brian@gmail.com",
  "id": "299b7d142d624238a7b66300ab3b4f5a",
  "account": "test",
  "mobile": "13958085908",
  "name": "测试",
  "remark": "测试账号"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 删除用户

删除用户，同时在租户、角色、用户组、组织机构中删除与该用户的关系。

请求方法：**DELETE**

接口URL：**/base/user/manage/v1.0/users**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|用户ID|

请求示例：

```bash
curl -X "DELETE" "http://192.168.236.8:6200/base/user/manage/v1.0/users" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d "299b7d142d624238a7b66300ab3b4f5a"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 禁用用户

将用户的状态标示为禁用，被禁用的用户不能获取Token，且不能访问需要验证用户身份的接口。

请求方法：**PUT**

接口URL：**/base/user/manage/v1.0/users/disable**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|用户ID|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/manage/v1.0/users/disable" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d "299b7d142d624238a7b66300ab3b4f5a"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 启用用户

将用户的状态标示为启用，被启用的用户可以正常获取Token和访问需要验证用户身份的接口。

请求方法：**PUT**

接口URL：**/base/user/manage/v1.0/users/enable**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|用户ID|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/manage/v1.0/users/enable" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d "299b7d142d624238a7b66300ab3b4f5a"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 重置用户密码

重置用户的登录密码。

请求方法：**PUT**

接口URL：**/base/user/manage/v1.0/users/password**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|用户ID|
|String|password|否|登录密码(MD5),如未设置登录密码,则自动使用默认密码(123456)|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/manage/v1.0/users/password" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "id": "299b7d142d624238a7b66300ab3b4f5a"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 邀请用户

建立用户与租户的关系。仅供登录到租户的管理员使用。

请求方法：**POST**

接口URL：**/base/user/manage/v1.0/users/relation**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|用户ID|

请求示例：

```bash
curl -X "POST" "http://192.168.236.8:6200/base/user/manage/v1.0/users/relation" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json' \
     -d "299b7d142d624238a7b66300ab3b4f5a"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 获取日志列表

通过关键词查询接口配置数据变更记录。查询关键词作用于操作类型、业务名称、业务ID、部门ID、操作人ID和操作人姓名。该接口支持分页，如不传分页参数，则返回最近添加的20条数据。

请求方法：**GET**

接口URL：**/base/user/manage/v1.0/users/logs**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|keyword|否|查询关键词|
|Integer|page|否|分页页码|
|Integer|size|否|每页记录数|

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|id|日志ID|
|String|type|操作类型|
|String|business|业务名称|
|String|businessId|业务ID|
|String|deptId|创建人登录部门ID|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求示例：

```bash
curl "http://192.168.236.8:6200/base/user/manage/v1.0/users/logs?keyword=insert" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": "b0c781a91ed448ddbb2d8791d80d6ff6",
      "tenantId": null,
      "type": "INSERT",
      "business": "用户管理",
      "businessId": "52fdf8884f6c46609fe67668731236cb",
      "content": null,
      "deptId": null,
      "creator": "系统管理员",
      "creatorId": "bb82f6bdfc5211e99bc30242ac110005",
      "createdTime": "2019-11-04 15:10:41"
    }
  ],
  "option": 12
}
```

[回目录](#目录)

### 获取日志详情

获取指定ID的日志详情。

请求方法：**GET**

接口URL：**/base/user/manage/v1.0/users/logs/{id}**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|id|是|日志ID|

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|id|日志ID|
|String|type|操作类型|
|String|business|业务名称|
|String|businessId|业务ID|
|Object|content|日志内容|
|String|deptId|创建人登录部门ID|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求示例：

```bash
curl "http://192.168.236.8:6200/base/user/manage/v1.0/users/logs/b0c781a91ed448ddbb2d8791d80d6ff6" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6IjY3MTg2ZDc4OTNjZTQ0Y2NiODBiN2Q3MGNmYWY1NTFiIiwic2VjcmV0IjoiYzk2MjJiMTM0NTI3NDQ2YWFkODU1MDM3OWFlOGM1MjYifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "b0c781a91ed448ddbb2d8791d80d6ff6",
    "tenantId": null,
    "type": "INSERT",
    "business": "用户管理",
    "businessId": "52fdf8884f6c46609fe67668731236cb",
    "content": {
      "id": "52fdf8884f6c46609fe67668731236cb",
      "code": null,
      "name": "测试",
      "email": null,
      "mobile": null,
      "remark": null,
      "account": "test",
      "builtin": false,
      "creator": "测试",
      "headImg": null,
      "invalid": false,
      "unionId": null,
      "password": "e10adc3949ba59abbe56e057f20f883e",
      "creatorId": "52fdf8884f6c46609fe67668731236cb",
      "createdTime": "2019-11-04 15:10:40",
      "payPassword": null
    },
    "deptId": null,
    "creator": "系统管理员",
    "creatorId": "bb82f6bdfc5211e99bc30242ac110005",
    "createdTime": "2019-11-04 15:10:41"
  },
  "option": null
}
```

[回目录](#目录)

## ToC模块

### 注册用户

描述接口能力(必须)、主要业务逻辑、涉及哪些数据、调用哪些服务(可选)

请求方法：**POST**

接口URL：**/base/user/v1.0/users**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|name|是|姓名/昵称|
|String|account|否|登录账号,如登录账号为空则手机号不可为空|
|String|password|是|登录密码(MD5)|
|String|mobile|否|手机号,如手机号为空则登录账号不可为空|
|String|headImg|否|头像URL|
|String|remark|否|备注|

请求示例：

```bash
curl -X "POST" "http://192.168.236.8:6200/base/user/v1.0/users" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "name": "测试用户",
  "mobile": "13767891234",
  "password": "c4ca4238a0b923820dcc509a6f75849b"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 201,
  "message": "创建数据成功",
  "data": "84e06693bafc4d58a06fe6907c1de8e5",
  "option": null
}
```

[回目录](#目录)

### 获取我的详情

获取当前用户的详情信息。

请求方法：**GET**

接口URL：**/base/user/v1.0/users/myself**

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|id|用户ID|
|String|code|用户编码|
|String|name|姓名/昵称|
|String|account|登录账号|
|String|mobile|手机号|
|String|unionId|微信UnionID|
|Map|openId|关联微信OpenID|
|String|headImg|头像URL|
|String|remark|备注|
|Boolean|isBuiltin|是否内置|
|Boolean|isInvalid|是否失效|
|String|creator|创建人|
|String|creatorId|创建人ID|
|Date|createdTime|创建时间|

请求示例：

```bash
curl "http://192.168.236.8:6200/base/user/v1.0/users/myself" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "id": "cc790eaff0b740989649db46e3e3d88b",
    "code": "IU94934938",
    "name": "86-13867891234",
    "account": "3982dc4ffed645f1888d18062ccf1688",
    "mobile": "86-13867891234",
    "email": null,
    "unionId": null,
    "openId": null,
    "headImg": null,
    "remark": null,
    "creator": "86-13867891234",
    "creatorId": "cc790eaff0b740989649db46e3e3d88b",
    "createdTime": "2019-11-10 11:43:23",
    "builtin": false,
    "invalid": false
  },
  "option": null
}
```

[回目录](#目录)

### 更新昵称

更新当前用户的姓名/昵称。

请求方法：**PUT**

接口URL：**/base/user/v1.0/users/name**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|姓名/昵称|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/name" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d "xbg"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 更新手机号

通过短信验证码，绑定或解除绑定当前用户的手机号。如用户已绑定手机号，需先解除绑定，然后绑定新的手机号。

请求方法：**PUT**

接口URL：**/base/user/v1.0/users/mobile**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|key|是|验证参数,MD5(2 + 绑定手机号 + 短信验证码)|
|String|mobile|否|手机号,解除绑定时手机号为空|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/mobile" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "key": "6063daa7b85b6bfccdcc6c157c67d4a7",
  "mobile": "86-13958085908"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 更新Email

更新当前用户的Email。

请求方法：**PUT**

接口URL：**/base/user/users/email**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|Email|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/email" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d "xbg@insight.com"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 更新头像

更新当前用户的头像。

请求方法：**PUT**

接口URL：**/base/user/v1.0/users/head**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|头像URL|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/head" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d "/xbg.gng"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 更新备注

更新当前用户的备注信息。

请求方法：**PUT**

接口URL：**/base/user/v1.0/users/remark**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|-|是|备注信息|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/remark" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d "test"
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 修改密码

通过验证原密码，为用当前户设置新密码。

请求方法：**PUT**

接口URL：**/base/userv1.0/users/password**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|old|是|原密码(MD5)|
|String|password|是|新密码(MD5)|

请求示例：

```bash
curl -X "PUT" "http://192.168.236.8:6200/base/user/v1.0/users/password" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "old": "e10adc3949ba59abbe56e057f20f883e",
  "password": "c4ca4238a0b923820dcc509a6f75849b"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 重置密码

通过验证短信验证码，重置手机号对应用户的登录密码，并直接获取Token。

请求方法：**POST**

接口URL：**/base/userv1.0/users/password**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|appId|是|应用ID|
|String|tenantId|否|租户ID|
|String|deptId|否|登录部门ID|
|String|key|是|验证参数,MD5(2 + 绑定手机号 + 短信验证码)|
|String|password|是|密码(MD5)|

接口返回数据类型：

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|String|accessToken|访问用令牌|
|String|refreshToken|刷新用令牌|
|Integer|expire|令牌过期时间(毫秒)|
|Integer|failure|令牌失效时间(毫秒)|
|[UserInfo](#UserInfo)|userInfo|用户信息|

请求示例：

```bash
curl -X "POST" "http://192.168.236.8:6200/base/user/v1.0/users/password" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "appId": "9dd99dd9e6df467a8207d05ea5581125",
  "key": "179d43d8c32c9cfbba4e2a471f3e8476",
  "password": "e10adc3949ba59abbe56e057f20f883e"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": {
    "accessToken": "eyJpZCI6ImE4OGIyZTM3NzQyYzQwMGJiZTg2NWI4NmI4ZjIyMGQ4Iiwic2VjcmV0IjoiODY4NDVmMTJmNzA2NGRmM2I1MWQxMmE5ZjJmMmEwODYifQ==",
    "refreshToken": "eyJpZCI6ImE4OGIyZTM3NzQyYzQwMGJiZTg2NWI4NmI4ZjIyMGQ4Iiwic2VjcmV0IjoiMjAyNWVmZjFjZDkzNDBlNDg5MzM5NjVkMzdkMzMxZjIifQ==",
    "expire": 7200000,
    "failure": 86400000,
    "userInfo": {
      "id": "cc790eaff0b740989649db46e3e3d88b",
      "tenantId": null,
      "deptId": null,
      "code": "",
      "name": "86-13867891234",
      "account": "3982dc4ffed645f1888d18062ccf1688",
      "mobile": "86-13867891234",
      "email": "",
      "headImg": "https://images.insight.com/head_default.png",
      "builtin": false,
      "createdTime": "2019-11-10 11:43:22"
    }
  },
  "option": null
}
```

[回目录](#目录)

### 设置支付密码

为当前用户设置支付密码，如已设置支付密码，则更新为新的支付密码。需要通过短信验证码的验证。

请求方法：**POST**

接口URL：**/base/user/v1.0/users/password/pay**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|key|是|验证参数,MD5(3 + 绑定手机号 + 短信验证码)|
|String|password|是|支付密码(MD5)|

请求示例：

```bash
curl -X "POST" "http://192.168.236.8:6200/base/user/v1.0/users/password/pay" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json' \
     -d $'{
  "key": "61c0ab4a031cce049cc4c02a71463e95",
  "password": "e10adc3949ba59abbe56e057f20f883e"
}'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

### 验证支付密码

验证支付密码是否为当前用户的支付密码，仅限服务调用。

请求方法：**GET**

接口URL：**/base/user/v1.0/users/password/pay**

请求参数如下：

|类型|属性|是否必需|属性说明|
| ------------ | ------------ | ------------ | ------------ |
|String|key|是|支付密码(MD5)|

请求示例：

```bash
curl -X "GET" "http://192.168.236.8:6200/base/user/v1.0/users/password/pay?key=61c0ab4a031cce049cc4c02a71463e95" \
     -H 'Accept: application/json' \
     -H 'Accept-Encoding: gzip, identity' \
     -H 'Authorization: eyJpZCI6ImUzYTg3N2MyZTE3ZDQ1NjM5ZGQ0YzZjMjA3NmNlZTIwIiwic2VjcmV0IjoiNWYxMjJjNjViZDFmNGNlNzllYjkzMzc0NWRiMjAxYjAifQ==' \
     -H 'Content-Type: application/json'
```

返回结果示例：

```json
{
  "success": true,
  "code": 200,
  "message": "请求成功",
  "data": null,
  "option": null
}
```

[回目录](#目录)

## DTO类型说明

### Reply

|类型|属性|属性说明|
| ------------ | ------------ | ------------ |
|Boolean|success|接口调用是否成功，成功：true；失败：false|
|Integer|code|错误代码，2xx代表成功，4xx或5xx代表失败|
|String|message|错误消息，描述了接口调用失败原因|
|Object|data|接口返回数据|
|Object|option|附加数据，如分页数据的总条数|

[回目录](#目录)

### UserInfo

|类型|字段|字段说明|
|----|----|----|
|String|id|用户ID|
|String|tenantId|用户当前登录租户ID|
|String|deptId|用户当前登录部门ID|
|String|code|用户编码|
|String|name|用户姓名|
|String|account|用户登录账号|
|String|mobile|用户绑定手机号|
|String|email|用户绑定邮箱|
|String|headImg|用户头像|
|Boolean|builtin|是否内置用户|
|String|createdTime|用户创建时间|

[回目录](#目录)
