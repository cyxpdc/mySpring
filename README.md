# 类简介：

#### 前导工作

ConfigConstant：定义所有配置名，一般业务流程为，若应用程序配置文件没有的，则会使用我们自定义的默认值；若有，则使用应用程序定义的值。

CastUtil：提供了一些类型转换，如Object要转为long、int，需要先转为String，再转为long、int

PropsUtil：加载配置文件；利用CastUtil可提供配置值的不同返回类型

ClassUtil：使用当前线程类上下文加载器来获取、加载指定包下的类。即应用程序包下的所有类，如Controller、Service等

ReflutionUtil：封装反射的相关操作，如newInstance、设置成员变量的值、调用方法

JsonUtil：封装ObjectMapper相关操作

StreamUtil：封装常用的流操作

CodecUtil：封装编码与解码、加密操作

ClassHelper：利用ClassUtil获取包下的指定类集合，如Controller注解的类集合、Service注解的类集合等

ConfigHelper：利用ConfigConstant里定义的配置名和PropsUtil已加载的配置文件来获取相应的配置值；
此类会调用PropsUtil的loadProps来加载配置文件

**而HelperLoader(见下文)初始化的ClassHelper中会调用ConfigHelper.getAppBasePackage();，即加载了配置文件**



#### 核心1：IOC

BeanHelper：利用ClassHelper(getBeanClassSet())和ReflutionUtil(newInstance())来设置和获取Bean实例，可以理解为这就是一个Bean容器(IOC容器)

实现依赖注入最简单的思路(无接口单实现类)：
IocHelper：封装beanMap，这样应用程序启动时，就能自动完成依赖注入

实现策略模式，即应用程序的Controller和Service可以使用接口+实现类的方式来写代码：新增加@Inject2Name注解，然后在IocHelper中获取有@Inject2Name的bean，获取其name，然后从BEAN_MAP取出对应的实例，进行初始化即可，这样的话，如果要用接口，则用@Inject2Name注解，如果不用，则直接用@Inject注解即可

增加了FactoryBean，其逻辑在ClassHelper#getFactoryBeanClassSet中，也就是得到扩展FactoryBean接口的类，然后遍历，调用每个类的getObjectType方法得到真正的bean，然后创建实例，添加到beanMap即可；使用者直接实现FactoryBean接口，就可以通过@Inject注解来使用了，案例在test3的com.pdc.test3.factory和controller.CustomerController#index

Spring的factoryBean：即使我们已经创建出来了对象的实例，还是要走一个方法再去处理下，这里就是对FactoryBean的处理，因为它可以产生对象，所以你getBean的时候取到的不是它本身，而是通过它生成的产品 https://www.cnblogs.com/PengChengLi/p/9233938.html

循环依赖：通过newInstance先创建出所有的bean实例，然后在IocHelper里进行注入；而Spring是创建所有bean的时候顺便注入：https://mp.weixin.qq.com/s/y-lj_PULYP536gurSQyyIw

#### 核心2：请求转发器

从HttpServletRequest对象中获取所有请求参数(如请求方法与请求路径)，通过ControllerHelper的getHandler方

法来获取Handler对象,进而获得Controller类的实例并调用相应的方法

也可以得到方法的返回值：

1.可能为View类型，需要返回JSP页面

2.可能为Data类型的数据对象，需要返回JSON数据



涉及的类：

Param：封装HttpServletRequest的请求参数，利用CastUtil来强制参数名，得到不同的返回类型

View：返回视图对象    设计技巧：善用Map<String,Object>

Data：返回数据对象；封装一个Object，写入到HttpServletResponse对象中，输出到浏览器

ControllerHelper：封装ACTION_MAP，则根据指定的requestMethod, requestPath封装Request就能得到对应Handler的controllerClass, method：

request：封装请求方法和请求路径requestMethod, requestPath

handler：封装Action的Controller类和要响应的方法controllerClass, method

DispatcherServlet：转发器，继承HttpServlet；
核心方法：service()，负责转发请求

> 流程：获取请求方法和请求路径，根据请求方法和请求路径映射的map获得Handler，根据Handler获取controllerClass类名，根据controllerClass类名和BeanHelper获取其实例；然后创建请求参数Param，根据Handler调用真正的Action方法(反射)



#### 核心3：基于注解的AOP(CGlib)

##### 一个简单的AOP：

Aspect：切面注解,有一个Class<?>的属性value

Proxy：代理接口，有一个doProxy方法，执行链式代理，即会调用ProxyChain的doProxyChain方法(可以理解为CGlib的MethodIntercept)

ProxyChain：代理链。其doProxyChain方法执行所有代理方法(**类似Tomcat的管道**)，最后执行目标对象的业务逻辑(注意成员变量的设计)

ProxyManager：创建所有的代理对象，由切面类来调用

AspectProxy：切面抽象代理类，实现Proxy接口，提供一个模板方法doProxy和一些钩子方法(before、after等)；应用程序使用时，只需要继承这个接口，即可实现自己想要的before、after等方法(如chapter4的ControllerAspect)，可以理解为代理对象的抽象类；代理类需要扩展此类，并带有@Aspect

AopHelper：获取所有的目标类及其被拦截的切面类实例，并通过ProxyManager的createProxy方法来创建代理对象，最后将代理对象放入BeanHelper的BeanMap中(即IOC容器)，才能注入到其他对象中**(在BeanMap中目标类的value设置为代理类，在DispatcherServlet中获取目标类对应的实例的时候就变成了代理类)**
利用了ClassHelper和BeanHelper

Advisor：将额外的功能委托给它，DefaultAdvisor为默认实现

> 设计：
>
> 需要先建立代理对象和目标类的联系，如@Aspect(Controller.class)的代理类对应所有带有@Controller的类；
>
> 再建立目标类与代理对象列表之间的映射关系，如每个@Controller可能有多个@Aspect(Controller.class)
>
> 这样就能直接设置BeanMap中目标类的实例为代理对象了

> 动态代理流程：在DispatcherServlet中BeanHelper.getBean(controllerClass)会得到代理对象，调用方法时，调用intercept，即调用ProxyChain的doProxyChain方法，然后doProxyChain方法会判断代理对象列表的代理个数，执行所有的Proxy代理对象，直到所有的代理对象结束后，才调用目标对象方法(此处为管道操作，Proxy的doProxy和ProxyChain的doProxyChain来回调用)
>
> AopHelper的addAspectProxy方法中，可能有多个@Aspect(Controller.class)代理类，即proxyMap中有不同的proxyClass对应相同的targetClassSet，所以在createTargetProxylistMap方法中，对每个proxyMap键值对，需要判断是否有相同的targetClass，如果有，则需要将代理类加到其代理列表中去

SpringAOP的advice：spring aop首先从IOC容器中获取Advice或者MethodInterceptor等，将其封装为Advisor（这一步主要目的是将Advice与PointCut相结合），最后在JDK Proxy和CGLIB Proxy的回调方法中应用PointCut匹配具体需要调用Advice的方法，并将Advisor中的Advice按类型封装为MethodInterceptor应用到目标方法的调用MethodInvocation上去（使用list保存MethodInterceptor，https://blog.csdn.net/john_lw/article/details/80925657），使用看https://blog.csdn.net/u010890358/article/details/80640433的前面部分

#### 核心4：事务管理

Transaction：事务注解，只能用于方法上

DatabaseHelper：封装事务常用的操作，使用ThreadLocal保证线程和数据库连接的一一对应

TransactionProxy：事务控制代理，实现Proxy接口，使用ThreadLocal保证同一线程中事务控制相关逻辑只会执行一次

AOPHelper：proxyMap添加事务管理addTransactionProxy方法，存放事务代理：目标类键值对

> 流程：AopHelper中会将BeanMap对应的service类的实例设置为代理，IocHelper注入时，会注入代理类

##### 插件：授权认证服务：mySpring-plugin-security

此处提供基于Shiro二次封装的认证、授权功能

###### 开发者如何使用此框架的认证授权功能?

1.提供配置文件smart-security.ini，描述登录请求路径是什么，哪些请求路径可以匿名访问，哪些请求路径必须通过身份认证才能访问。这个配置文件的本质就是Shiro所需要的配置文件，不过为了不让开发者知道底层实现而已，方便开发者使用

```ini
[main]
authc.loginUrl = /login

[urls]
/ = anon
/login = aono
/customer/** = authc
```

因此，我们需要识别配置文件，且实现Shiro提供的相关安全控制接口，将实现类配置在smart.properties，由Shiro来读取这些配置项

2.实现安全控制接口

SmartSecurity：提供认证与授权的三个核心功能：根据用户名获取密码、根据用户名获取角色名集合、根据角色名获取权限名集合
有如下两种使用方法：
一.应用程序中提供实现类，完成相应的数据库操作，如test的AppSecurity；
二.或在smart.properties中提供相关SQL配置项
区别在于realms为jdbc还是custom

第一种方式的配置方式：

```properties
smart.plugin.security.realms=custom
smart.plugin.security.custom.class=org.smart4j.test.AppSecurity
```

第二种方式的配置方式：

```properties
smart.plugin.security.realms=jdbc
smart.plugin.security.jdbc.authc_query="SELECT password FROM user WHERE username = ?"
smart.plugin.security.jdbc.roles_query="SELECT r.role_name FROM user u, user_role ur, role r WHERE u.id = ur.user_id AND r.id = ur.role_id AND u.username = ?"
smart.plugin.security.jdbc.roles_query="SELECT p.permission_name FROM role r, role_permission rp, permission p WHERE r.id = rp.role_id AND p.id = rp.permission_id AND r.role_name = ?"
```

3.测试

提供的测试功能：登录认证

a.JSP页面使用security:guest验证游客；security:user验证登录用户；security:principal获取当前用户username；在这些标签里面进行url的重定向和显示

b.除了JSP标签，也可以使用SecurityHelper的API进行这些操作，如登录

c.SystemController：登录注销功能测试类，调用SecurityHelper的API来实现具体方法

###### 实现

1.加入依赖：Servlet、JSP、Shiro、Smart Framework

2.添加EnvironmentLoaderListener和ShiroFilter，由SmartSecurityPlugin实现
EnvironmentLoaderListener用来初始化SecurityManager，即读取classpath中的shiro.ini文件，并加载其中的相关配置到内存中，以便ShiroFilter获取；
ShiroFilter用来拦截客户端请求，获取url与shiro.ini的相关配置项进行比较，完成认证与授权；
使用servlet3.0来省略web.xml，并将默认读取shiro.ini文件改为读取smart-security.ini文件，这样就封装了Shiro的相关细节，包括配置文件

3.实现ServletContainerInitializer接口(也由SmartSecurityPlugin实现)，并在classpath下的META-INF/services/javax.servlet.ServletContainerInitializer下添加需要初始化的类(如org.smart4j.security.SmartSecurityPlugin)，使Web应用初始化时就完成2的操作

4.创建表：RBAC模型

- 认证部分类简介

SmartSecurity：安全框架接口，由使用者扩展此类实现上述的三个方法，或在smart.properties中配置实现

SecurityHelper：封装安全控制API，如登陆和退出，用户使用登陆退出时，使用这个类的方法

AuthcException：受检异常，登陆失败时做一些事情，用于非法访问时抛出的异常

AuthzException：当前用户无权限访问某个操作时抛出此异常

SmartSecurityPlugin：注册Shiro
实现javax.servlet.ServletContainerInitializer接口，在META-INF/services/javax.servlet.ServletContainerInitializer文件中添加此实现类的全限定名，通过如上操作，servlet容器才能读取jar包中的ServletContainerInitializer文件，并加载SmartSecurityPlugin
注册EnvironmentLoaderListener和SmartSecurityFilter

SmartSecurityFilter：扩展了ShiroFilter，**为最核心的对象**，给WebSecurityManager提供了SmartJdbcRealm和SmartCustomRealm，解决"提供认证与授权的三个核心方法"的问题
SmartJdbcRealm：基于基于sql配置文件实现的JDBCRealm，用于认证+授权
SmartCustomRealm：基于编程接口SmartSecurity的CustomRealm，用于认证+授权

Md5CredentialsMatcher：MD5密码匹配器，实现了CredentialsMatcher

SecurityConfig：使用ConfigHelper获取smart.properties文件中的配置项

SecurityConstand：安全配置相关常量类

> 流程：使用者在controller层使用SecurityHelper的login、logout来实现登录和注销
> 登录本质上会使用shiro来获取当前用户和账号密码进行登录，账号密码配置在了SmartJdbcRealm/SmartCustomRealm中，会通过SmartSecurityFilter拦截后使用自定义Realm进行判断

- 授权部分类简介

> 1.使用JSP标签

security.tld：重新定义Shiro提供的JSP标签类，这样可以自己写类作为标签类，只需定义在这个文件里面即可
此处定义如下三个标签类：

HasAllRolesTag：subject.hasAllRoles(Arrays.asList(roleNames.split(ROLE_NAMES_DELIMITER)))；对应的标签为security:hasAllRoles，即判断当前用户是否拥有其中所有的角色

HasAnyPermissionsTag：subject.isPermitted(permissionName.trim())；对应的标签为security:hasAnyPermissions，即判断当前用户是否拥有其中其中一种权限

HasAllPermissionsTag：subject.isPermittedAll(permNames.split(PERMISSION_NAMES_DELIMITER))；对应的标签为security:hasAllPermissions，即判断当前用户是否拥有其中所有的权限

这三个类实现的本质都是直接调用shiro的API

使用时，在JSP页面这些标签内部写业务逻辑即可

> 2.使用注解

提供的注解：

@User：判断当前用户是否已登录，包括已认证与已记住

@Guest：判断当前用户是否未登录，包括未认证或未记住，即身份为访客

@Authenticated：判断当前用户是否已认证

@HasRoles：判断当前用户是否拥有某种角色

@HasPermissions：判断当前用户是否拥有某种权限

以@User为例进行讲解：
AuthzAnnotationAspect切面类：**作为Controller的代理对象之一，在执行controller层时，会执行此对象**，实现前置增强机制，即覆盖before方法，判断是否有相关注解来调用shiro的API做相应的操作

> 流程：使用者在controller层的类或方法上打上这些注解，DispatcherServlet到controller层时，执行代理，AuthzAnnotationAspect为代理对象之一，执行before方法来判断是否有相关注解来做相应处理

#### 插件之SOAP服务：mySpring-plugin-soap

使用者：
1.定义测试接口及其实现类，实现类需要加上@Soap、@Service，表示该类需要发布为SOAP服务，@Service表示该类需要注入到IOC容器；注入一个自己编写的如Service对象，使用其方法即可；如CustomerSoapService和CustomerSoapServiceImpl
2.调用SOAP服务：使用SoapHelper，根据WSDL来创建接口代理对象，如CustomerSoapService接口，就可以调用此代理对象的任意方法，等于调用实现类中的方法
CustomerSoapServiceTest为测试类

类简介：

@Soap：打上此注解的类表示需要发布为SOAP服务

SoapHelper：基于CXF实现，可以根据WSDL和类的class来创建此类的代理，然后就能调用方法了;发布SOAP服务；调用Web服务时，可开启调用日志，方便调试

SoapConfig：从配置文件smart.properties中获取相关属性

SoapConstant：定义SOAP常量，如请求路径和log日志目录

SoapServlet：拦截所有的SOAP请求，发布ws服务

流程：

> 出现**/soap/ *请求时，会到达SoapServlet，发布所有打上@Soap注解的服务类，然后客户端调用的createClient会创建使用者传入的接口class的代理类，然后就可以调用此接口的方法了



#### 插件之REST服务：mySpring-plugin-rest

使用者：
1.定义服务：比SOAP服务简单，不需要定义一个接口，只需要定义类及其方法即可，使用JAX-RS API，打上@Rest、@Service、@Consumes(定义输入的数据类型)、@Produces(定义输出的数据类型)，方法则打上@Get、@Post等注解；如CustomerRestService
2.调用服务：CustomerRestServiceTest

需要添加CXF依赖

类简介：

@Rest：打上此注解的类表示需要发布为REST服务

RestHelper：类似SoapHelper，额外支持了JSONP与CORS

RestConfig：类似SoapConfig

RestConstant：类似SoapConstant

RestServlet：类似SoapServlet

> 流程与SOAP服务流程相同

- 来自《架构探险 从零开始写javaweb框架》

# WebClient框架开发

开发一个类似Feign的声明式WebService客户端，我们可以将它作为HTTP客户端调用远程HTTP服务，替代HttpClient

使用：

定义接口，如IUserApi，打上ApiServer注解，就能在业务代码中，通过@Autowired注入IUserApi，调用其方法，就等于调用其方法所对应的url，即远程服务

服务端：webfulx、webflux2代码

客户端：webfluxClient，端口改为8081

> 测试代码

1.User：测试domain

2.IUserApi：定义测试方法接口

3.TestController：测试Controller



> 设计思路：

流程：通过JDK动态代理生成代理类，此代理类获取API信息，交给InvokeHandler去处理请求，然后将结果返回给调用此方法的业务代码即可



> 类简介：

1.WebfluxclientApplication：使用FactoryBean实现注册userApi

2.ProxyCreator：创建代理类的接口。技巧：可能为JDK动态代理或CGlib，定义此接口达到扩展性

3.JDKProxyCreator：JDK动态代理创建代理类。需要创建ServerInfo类和MethodInfo类，然后使用WebClientRestHandler获取接口代理对象；一个代理类对应一个WebClientRestHandler

4.ServerInfo：封装API服务器信息

5.MethodInfo：封装调用信息

6.RestHandler：调用rest。技巧：可能用webClient，可能用RestTemplate，定义此接口达到扩展性

7.WebClientRestHandler：真正地调用。此处传入了ServerInfo，在内部就将WebClient初始化，这样handler.invokeRest(methodInfo);即可，不用传ServerInfo，因为ServerInfo不会改变，这样就不用每次创建代理类都初始化，节约资源时间

8.@ApiServer：用来定义服务器



> 流程：

WebfluxclientApplication会将JDKProxyCreator和IUserApi作为bean注入，IUserApi的实现类为JDKProxyCreator#creator生成的代理类，此代理类封装了IUserApi的信息，调用接口方法时，实际是使用基于WebClient的WebClientRestHandler去调用封装好了的信息类，动态代理的真正作用是提供一个桥梁，使用接口时，通过代理类这个bean去封装接口的消息；

设计思路可参考mybatis，同样是使用接口进行操作，核心技术就是动态代理



- 来自慕课网视频