## API Auth Framework

> 基于 Spring Boot 的一款 API 接口鉴权插件

### 使用
在启动类注解上标记 `@EnableApiAuth` 即可
```java
@EnableApiAuth
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
添加上 `@EnableApiAuth` 后，即可对所有接口按照默认规则进行接口鉴权