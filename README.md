# mymvc-spring-boot-starter

一个轻量级的 Spring Boot MVC 增强 Starter，提供统一响应封装、全局异常处理、增强参数校验、隐私字段脱敏等功能。

[![Maven Central](https://img.shields.io/badge/maven--central-spring3-blue)](https://search.maven.org/artifact/io.github.mocanjie/mymvc-spring-boot-starter)
[![License](https://img.shields.io/badge/license-Apache%202.0-green)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java](https://img.shields.io/badge/java-17+-orange)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.2.0+-brightgreen)](https://spring.io/projects/spring-boot)

## 特性

- 🚀 **统一响应封装** - 提供 `MyResponseResult<T>` 统一返回格式
- 🛡️ **全局异常处理** - 继承 `MyBaseController` 自动处理常见异常
- ✅ **增强参数校验** - 提供 5 种开箱即用的自定义校验器
- 🔒 **隐私字段脱敏** - `@Privacy` 注解自动遮蔽响应 DTO 中的敏感字段，支持 Jackson / Fastjson / Fastjson2
- 🎯 **零侵入集成** - 基于 Spring Boot 自动装配，引入即可使用
- ⚡ **轻量灵活** - optional 依赖设计，容器中立，依赖注入优化

## 快速开始

### Maven 依赖

```xml
<!-- 1. 添加本 Starter -->
<dependency>
    <groupId>io.github.mocanjie</groupId>
    <artifactId>mymvc-spring-boot-starter</artifactId>
    <version>spring3</version>
</dependency>

<!-- 2. 确保项目中有 spring-boot-starter-web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

> **说明**：本 Starter 使用 `optional` 依赖，不会强制引入 Spring 相关依赖，避免版本冲突。请确保你的项目中已有 `spring-boot-starter-web` 依赖。

### 基本使用

#### 1. 统一响应格式

继承 `MyBaseController` 使用便捷的响应方法：

```java
@RestController
@RequestMapping("/api/users")
public class UserController extends MyBaseController {

    // 返回数据
    @GetMapping("/{id}")
    public MyResponseResult<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return doJsonOut(user);
    }

    // 返回分页数据
    @GetMapping("/page")
    public MyResponseResult<Page<User>> listUsers(@RequestParam int page) {
        Page<User> userPage = userService.findByPage(page);
        return doJsonPagerOut(userPage);
    }

    // 返回消息
    @PostMapping
    public MyResponseResult saveUser(@RequestBody User user) {
        userService.save(user);
        return doJsonMsg("用户创建成功");
    }

    // 自定义状态码和消息
    @DeleteMapping("/{id}")
    public MyResponseResult deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return doJsonMsg(200, "删除成功");
    }
}
```

**响应格式示例：**

```json
{
  "code": 200,
  "msg": "OK",
  "data": {
    "id": 1,
    "name": "张三"
  }
}
```

#### 2. 自定义异常处理

继承 `MyBaseController` 后自动处理常见异常，也可以抛出自定义业务异常：

```java
import io.github.mocanjie.base.mycommon.exception.BusinessException;

@Service
public class UserService {

    public User findById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            // 抛出业务异常，会被自动捕获并返回友好的错误信息
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
```

**错误响应示例：**

```json
{
  "code": 500,
  "msg": "用户不存在",
  "data": null
}
```

#### 3. 隐私字段脱敏

在响应 DTO 的字段上添加 `@Privacy` 注解，序列化时自动遮蔽敏感数据，无需改动业务代码。

```java
public class UserVO {

    @Privacy(type = PrivacyType.PHONE)
    private String phone;      // 138****8888

    @Privacy(type = PrivacyType.NAME)
    private String name;       // 张*明

    @Privacy(type = PrivacyType.EMAIL)
    private String email;      // zh***@qq.com

    @Privacy(type = PrivacyType.ID_CARD)
    private String idCard;     // 110101****2345

    @Privacy(type = PrivacyType.BANK_CARD)
    private String bankCard;   // ****1234

    @Privacy(type = PrivacyType.ADDRESS)
    private String address;    // 北京市朝阳区****

    @Privacy(left = 3, right = 4)
    private String custom1;    // 固定保留前3后4位

    @Privacy(percent = 0.6)
    private String custom2;    // 遮蔽中间60%

    @Privacy(maskChar = '#')
    private String custom3;    // 自定义遮蔽字符
}
```

**预设策略说明：**

| 类型 | 保留规则 | 示例 |
|------|---------|------|
| `PHONE` | 前3后4 | `138****8888` |
| `NAME` | 首尾各1 | `张*明` |
| `EMAIL` | 遮蔽本地部分中间 | `zh***@qq.com` |
| `ID_CARD` | 前6后4 | `110101****2345` |
| `BANK_CARD` | 仅保留后4 | `****1234` |
| `ADDRESS` | 保留前6 | `北京市朝阳区****` |
| `CUSTOM` | 由 `left`/`right`/`percent` 决定 | 自定义 |

**序列化框架支持：**

| 框架 | 检测条件 | 说明 |
|------|---------|------|
| Jackson | classpath 含 `jackson-databind` | Spring Boot Web 默认集成，自动生效 |
| Fastjson | classpath 含 `com.alibaba:fastjson` | 自动注册 `ValueFilter` |
| Fastjson2 | classpath 含 `com.alibaba.fastjson2:fastjson2` | 自动注册 `ValueFilter` |

三个框架可同时共存，各自处理自己的序列化路径。

#### 4. 增强参数校验

##### 4.1 实体类校验

```java
public class UserDTO {

    @IdCard(required = true, message = "请输入有效的身份证号")
    private String idCard;

    @Number(required = true, min = 0, max = 150, message = "年龄必须在0-150之间")
    private String age;

    @ValIn(value = {"male", "female"}, message = "性别只能是male或female")
    private String gender;

    @Date(format = "yyyy-MM-dd", message = "请输入正确的日期格式(yyyy-MM-dd)")
    private String birthday;

    @LimitLength(min = 2, max = 50, chineseLength = 2, message = "姓名长度必须在2-50字符之间")
    private String name;
}
```

```java
@RestController
public class UserController extends MyBaseController {

    // 使用 @Valid 触发校验
    @PostMapping("/users")
    public MyResponseResult createUser(@Valid @RequestBody UserDTO userDTO) {
        // 校验失败会自动返回错误信息
        userService.save(userDTO);
        return doJsonDefaultMsg();
    }
}
```

##### 4.2 方法参数校验

使用 `@Validated` 注解在方法上启用参数级别的校验：

```java
@RestController
public class UserController extends MyBaseController {

    @GetMapping("/user/{id}")
    @Validated // 启用方法参数校验
    public MyResponseResult getUser(
        @Number(min = 1, message = "用户ID必须大于0") @RequestParam String id
    ) {
        User user = userService.findById(Long.parseLong(id));
        return doJsonOut(user);
    }
}
```

## 自定义校验器详解

### @IdCard - 身份证号校验

校验中国大陆 15 位或 18 位身份证号。

```java
@IdCard(required = true, message = "请输入有效的身份证号")
private String idCard;
```

**参数：**
- `required`: 是否必填，默认 `false`
- `message`: 自定义错误消息

**校验规则：**
- 支持 15 位和 18 位身份证
- 验证地区码、出生日期、校验码
- 年龄必须在 0-150 岁之间

---

### @Number - 数字范围校验

校验字符串是否为数字，并支持范围和整数限制。

```java
@Number(required = true, min = 0, max = 100, integer = true, message = "分数必须是0-100的整数")
private String score;
```

**参数：**
- `required`: 是否必填，默认 `false`
- `min`: 最小值，默认 `Long.MIN_VALUE`
- `max`: 最大值，默认 `Long.MAX_VALUE`
- `integer`: 是否必须为整数，默认 `false`
- `message`: 自定义错误消息

---

### @ValIn - 值域校验

校验字符串是否在指定的值域范围内，支持枚举类型。

```java
// 简单值域
@ValIn(value = {"PENDING", "APPROVED", "REJECTED"}, message = "状态值无效")
private String status;

// 枚举值域
@ValIn(
    enumType = @EnumType(type = UserStatus.class, valKey = "code"),
    message = "用户状态无效"
)
private String userStatus;

// 忽略大小写
@ValIn(value = {"true", "false"}, ignoreCase = true, message = "布尔值无效")
private String boolValue;
```

**参数：**
- `value`: 允许的字符串值数组
- `enumType`: 枚举类型配置（支持通过枚举字段值校验）
- `ignoreCase`: 是否忽略大小写，默认 `false`
- `required`: 是否必填，默认 `false`
- `message`: 自定义错误消息

**EnumType 参数：**
- `type`: 枚举类
- `valKey`: 枚举中用于匹配的字段名
- `include`: 包含的枚举值（默认全部）
- `exclude`: 排除的枚举值

**示例枚举：**

```java
public enum UserStatus {
    ACTIVE("1", "激活"),
    INACTIVE("0", "停用");

    private String code;
    private String desc;

    UserStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

---

### @Date - 日期格式校验

校验字符串是否符合指定的日期格式。

```java
@Date(format = "yyyy-MM-dd HH:mm:ss", message = "请输入正确的日期时间格式")
private String createTime;

@Date(format = "yyyy-MM-dd", required = true, message = "出生日期不能为空")
private String birthday;
```

**参数：**
- `format`: 日期格式（符合 `SimpleDateFormat` 规范）
- `required`: 是否必填，默认 `false`
- `message`: 自定义错误消息

**常用格式：**
- `yyyy-MM-dd`: 2024-01-01
- `yyyy-MM-dd HH:mm:ss`: 2024-01-01 12:30:45
- `yyyy/MM/dd`: 2024/01/01

---

### @LimitLength - 字符串长度校验

校验字符串长度（支持中英文混合，可自定义中文字符长度）。

```java
@LimitLength(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
private String password;

@LimitLength(min = 2, max = 50, chineseLength = 2, message = "姓名长度必须在2-50字符之间")
private String name;
```

**参数：**
- `min`: 最小长度，默认 `0`
- `max`: 最大长度，默认 `Long.MAX_VALUE`
- `chineseLength`: 一个中文字符计为几个长度，默认 `1`
- `required`: 是否必填，默认 `false`
- `message`: 自定义错误消息

**长度计算规则：**
- 英文、数字、符号：长度为 1
- 中文字符：长度为 `chineseLength` 设置的值

**示例：**

```java
// chineseLength = 2
"hello"    -> 长度 5
"你好"      -> 长度 4
"hello你好" -> 长度 9
```

---

## 编程式校验

除了注解式校验，还可以使用工具类进行编程式校验：

```java
import io.github.mocanjie.base.mymvc.validator.MyValidatorUtils;
import io.github.mocanjie.base.mycommon.exception.BusinessException;

public class UserService {

    public void saveUser(UserDTO userDTO) {
        // 手动触发校验，校验失败会抛出 BusinessException
        MyValidatorUtils.validateEntity(userDTO);

        // 业务逻辑...
    }
}
```

## 全局异常处理

继承 `MyBaseController` 后，以下异常会被自动处理：

| 异常类型 | HTTP状态码 | 说明 |
|---------|-----------|------|
| `BaseException` | 自定义 | 业务异常基类 |
| `BusinessException` | 500 | 业务异常 |
| `BindException` | 400 | 参数绑定异常（Bean Validation） |
| `HttpMessageNotReadableException` | 400 | JSON 解析异常 |
| `HttpRequestMethodNotSupportedException` | 405 | 请求方法不支持 |
| `DuplicateKeyException` | 500 | 数据库唯一键冲突 |
| `AccessDeniedException` | 403 | 权限不足 |
| `AuthenticationException` | 403 | 认证失败 |
| `HttpMediaTypeNotSupportedException` | 422 | 媒体类型不支持 |
| 其他异常 | 500 | 未知异常 |

### 自定义异常错误码

```java
public static final String LOGIN_ERROR_MSG = "非法授权,请先登录";
public static final String PERMISSION_ERROR_MSG = "您没有权限，请联系管理员授权";
public static final String REQUEST_ERROR_MSG = "请求参数格式错误";
public static final String DUPLICATEKEY_ERROR_MSG = "系统已经存在该记录";
```

可以通过继承 `MyBaseController` 并重写常量来自定义：

```java
@RestController
public class CustomController extends MyBaseController {
    static {
        LOGIN_ERROR_MSG = "请先登录系统";
        PERMISSION_ERROR_MSG = "权限不足";
    }
}
```

## 技术栈

- **Java 17+**
- **Spring Boot 3.2.0+**
- **Spring Framework 6.1.0+**
- **Jakarta Validation 3.0.2**
- **Hibernate Validator 8.0+**
- **Apache Commons Lang3 3.12.0**
- **AspectJ 1.9.21**

## 兼容性

- ✅ Spring Boot 3.x
- ✅ Spring Framework 6.x
- ✅ Java 17+
- ✅ Jakarta EE 9+
- ✅ 容器中立（Tomcat / Jetty / Undertow）

> **注意**：
> 1. 本项目使用 `jakarta.*` 命名空间，不兼容 Spring Boot 2.x 及以下版本
> 2. 本 Starter 不强制指定 Web 容器，你可以自由选择 Tomcat、Jetty 或 Undertow

## 配置项

目前 Starter 采用零配置设计，所有功能自动启用。如需禁用某些功能，可以通过排除自动配置类：

```java
@SpringBootApplication(exclude = {
    MyMvcAutoConfiguration.class  // 禁用参数校验切面
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 最佳实践

### 1. Controller 层

```java
@RestController
@RequestMapping("/api/products")
public class ProductController extends MyBaseController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public MyResponseResult<List<Product>> list() {
        return doJsonOut(productService.findAll());
    }

    @PostMapping
    public MyResponseResult create(@Valid @RequestBody ProductDTO dto) {
        productService.create(dto);
        return doJsonDefaultMsg();
    }

    @GetMapping("/{id}")
    @Validated
    public MyResponseResult<Product> get(
        @Number(min = 1, message = "ID必须大于0") @PathVariable String id
    ) {
        return doJsonOut(productService.findById(Long.parseLong(id)));
    }
}
```

### 2. Service 层

```java
@Service
public class ProductService {

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("商品不存在"));
    }

    public void create(ProductDTO dto) {
        // 编程式校验
        MyValidatorUtils.validateEntity(dto);

        // 业务逻辑
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        productRepository.save(product);
    }
}
```

### 3. DTO 层

```java
@Data
public class ProductDTO {

    @LimitLength(min = 2, max = 100, message = "商品名称长度必须在2-100字符之间")
    private String name;

    @Number(required = true, min = 0, message = "价格必须大于等于0")
    private String price;

    @ValIn(value = {"NEW", "HOT", "SALE"}, message = "标签值无效")
    private String tag;

    @Date(format = "yyyy-MM-dd", message = "上架日期格式错误")
    private String publishDate;
}
```

## 常见问题

### Q: 为什么校验不生效？

**A:** 请确保：
1. Controller 继承了 `MyBaseController`
2. 方法参数上添加了 `@Valid`（实体校验）或方法上添加了 `@Validated`（参数校验）
3. Maven 依赖正确引入
4. 项目中有 `spring-boot-starter-web` 或 `spring-boot-starter-validation` 依赖

### Q: 如何自定义错误消息格式？

**A:** 重写 `MyBaseController` 中的 `handleException` 方法：

```java
@RestController
public class CustomController extends MyBaseController {

    @Override
    @ExceptionHandler(BaseException.class)
    protected MyResponseResult handleException(BaseException e) {
        // 自定义错误处理逻辑
        return doJsonMsg(e.getCode(), "【错误】" + e.getMessage());
    }
}
```

### Q: 校验失败的错误消息是什么格式？

**A:** 参数名 + 空格 + 错误消息。例如：

```
age 年龄必须在0-150之间
idCard 请输入有效的身份证号
```

如果有多个字段校验失败，会按字母顺序排列：

```
[age 年龄必须在0-150之间, name 姓名长度必须在2-50字符之间]
```

### Q: 如何禁用某个校验器？

**A:** 校验器是按需使用的，不使用对应注解即不会触发校验。如需禁用整个参数校验功能，可排除 `MyMvcAutoConfiguration`。

### Q: 如何自定义 Jackson 配置？

**A:** 本 Starter 不包含 Jackson 配置，你可以在项目中自行配置 `ObjectMapper` Bean：

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }
}
```

### Q: 为什么依赖标记为 optional？

**A:** 这是 Spring Boot Starter 的最佳实践：
- **避免版本冲突**：不会覆盖你项目中已有的 Spring 版本
- **减小体积**：只在需要时才引入依赖
- **灵活性**：你可以选择不使用某些功能（如校验）

通常情况下，你的项目已经有 `spring-boot-starter-web`，它会提供所有必需的 optional 依赖。

### Q: 如何选择 Web 容器？

**A:** 本 Starter 不强制容器，你可以自由选择：

```xml
<!-- 使用 Tomcat（默认） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 使用 Undertow -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

## 依赖说明

本 Starter 采用**最少依赖原则**设计，核心依赖均标记为 `optional`，不会污染你的项目依赖树。

### 必需依赖（会传递到你的项目）

```xml
<!-- 业务异常基类 -->
<dependency>
    <groupId>io.github.mocanjie</groupId>
    <artifactId>mycommon</artifactId>
</dependency>

<!-- 工具类 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

### 可选依赖（需要你的项目提供）

以下依赖标记为 `optional`，通常由 `spring-boot-starter-web` 提供：

- `spring-boot-autoconfigure` - 自动配置
- `spring-webmvc` - MVC 支持
- `jakarta.servlet-api` - Servlet 规范
- `spring-aop` - AOP 支持
- `aspectjweaver` - 切面编织
- `jakarta.validation-api` - 校验 API
- `hibernate-validator` - 校验实现
- `slf4j-api` - 日志接口
- `jackson-databind` - 隐私脱敏 Jackson 适配（有则激活）
- `fastjson` - 隐私脱敏 Fastjson v1 适配（有则激活）
- `fastjson2` - 隐私脱敏 Fastjson2 适配（有则激活）

### 为什么这样设计？

1. **避免版本冲突** - 不强制依赖版本，由你的项目统一管理
2. **减小体积** - 不引入冗余的 starter 和传递依赖
3. **容器中立** - 不强制 Undertow，你可以选择 Tomcat / Jetty / Undertow
4. **灵活性高** - 可以精确控制每个依赖的版本

## 更新日志

### 1.0-jdk21 版本（最新）
- ✅ **隐私脱敏**：新增 `@Privacy` 注解，支持 Jackson / Fastjson / Fastjson2 自动脱敏
- ✅ 升级 JDK 至 21

### spring3 版本
- ✅ 升级支持 Spring Boot 3.x 和 Jakarta EE
- ✅ **架构优化**：移除所有 Hibernate Validator 内部 API 反射调用
- ✅ **依赖优化**：采用最少依赖原则，核心依赖标记为 optional
- ✅ **性能优化**：ValidatorFactory 改为 Spring Bean 注入
- ✅ **Bug 修复**：修复 ValInValidator 的校验逻辑错误
- ✅ **代码质量**：移除 Hashtable、修复空指针风险、添加 final 修饰符
- ✅ **容器中立**：移除强制 Undertow 依赖，支持任意容器
- ✅ **配置简化**：移除 Jackson 自动配置，减少干预

## 许可证

本项目采用 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) 许可证。

## 联系方式

- **作者**: mocanjie
- **邮箱**: mocanjie@qq.com
- **GitHub**: [https://github.com/mocanjie/mymvc-spring-boot-starter](https://github.com/mocanjie/mymvc-spring-boot-starter)

## 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

**如果这个项目对你有帮助，请给个 Star ⭐️ 支持一下！**
