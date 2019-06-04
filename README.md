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

> 流程：获取请求方法和请求路径，根据请求方法和请求路径获得Handler，根据Handler获取controllerClass
>
> 类名，根据controllerClass类名获取其实例；然后创建请求参数，根据Handler调用真正的Action方法(反
>
> 射)，返回JSP页面或返回数据对象



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

### 

#### 核心4：事务管理

Transaction：事务注解，只能用于方法上

DatabaseHelper：封装事务常用的操作，使用ThreadLocal保证线程和数据库连接的一一对应

TransactionProxy：事务控制代理，实现Proxy接口，使用ThreadLocal保证同一线程中事务控制相关逻辑只会执行一次

AOPHelper：proxyMap添加事务管理addTransactionProxy方法，存放事务代理：目标类键值对

> 流程：AopHelper中会将BeanMap对应的service类的实例设置为代理，IocHelper注入时，会注入代理类



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

![1559634580177](F:/markdownPicture/assets/1559634580177.png)

> 类简介：

1.WebfluxclientApplication：使用FactoryBean实现注册userApi

2.ProxyCreator：创建代理类的接口。技巧：可能为JDK动态代理或CGlib，定义此接口达到扩展性

3.JDKProxyCreator：JDK动态代理创建代理类。需要创建ServerInfo类和MethodInfo类，然后使用Handler获取接口代理对象

4.ServerInfo：封装API服务器信息

5.MethodInfo：封装调用信息

6.RestHandler：调用rest。技巧：可能用webClient，可能用RestTemplate，定义此接口达到扩展性

7.WebClientRestHandler：组合了ServerInfo，在内部就将其初始化，这样handler.invokeRest(methodInfo);即可，不用传ServerInfo，因为ServerInfo不会改变，这样就不用每次创建代理类都初始化，节约资源时间

8.@ApiServer：用来定义服务器



> 流程：

WebfluxclientApplication会将JDKProxyCreator和IUserApi作为bean注入，IUserApi的实现类为JDKProxyCreator#creator生成的代理类，此代理类封装了IUserApi的信息，调用接口方法时，实际是使用基于WebClient的WebClientRestHandler去调用封装好了的信息类，动态代理的真正作用是提供一个桥梁，使用接口时，通过代理类这个bean去封装接口的消息；

设计思路可参考mybatis，同样是使用接口进行操作，核心技术就是动态代理



- 来自慕课网视频