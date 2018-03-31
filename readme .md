CloverFramework
===============

Summary
-------

CloverFramework是一个领域驱动DSL框架（DDD-DSL），有关领域驱动设计的概念参见：<https://en.wikipedia.org/wiki/Domain-driven_design>。这是一个实现领域驱动模型的系统架构，将相关的概念、组件定义然后固化，根据实际建模和业务实现的需求，一个基于DDD的业务核心，并且持续的扩充特性和接口功能。目前是实验性阶段。

### Change

#### Can you still put up with this?

还在使用冗长的dao接口和service接口吗？

你是否还在重复着做这样的事情？

![](media/a20dc370274baa12a5e740ed9b52644b.png)

方法名如诗和远方，怎么会这样？背后隐藏了太多的无奈和烦恼。

#### Problem

不管是传统的三层架构还是领域驱动模型，都无法回避这个问题，因为业务形式是变化的，而接口是固化的，为了同时满足两者，只能牵一发动全身，业务的复杂度提高以后，就出现了上面的尴尬局面。

传统的三层架构包括DAO层、service层、view层，而《领域驱动设计-软件核心复杂性应对之道》一书中将领域驱动架构定义为四层：仓储、领域、应用、UI。在这当中，领域层最为复杂，又细化为工厂、领域服务、翻译器、值对象等一系列组件，目的是为解决三层架构无法应对诸如**业务多边形的复杂度问题**。

按照上述DDD架构都被设计成如下图：

![](media/58942aee646e8553dee7d0405245d4bc.png)

如果尝试按照上述组件的划分来设计架构，通常实际上没有（甚少）带来业务过程实现的简化，也没有带来其他方面的提升，更不用说应对业务的复杂，这种DDD实现方式的缺点：

-   过程复杂，无法明确组件职责，后期问题明显增多。

-   值对象和不变性的强调使用代价太高。

-   领域内部聚合并不能应对复杂性，反而提高了系统复杂度。在这种模式下，更是为了迎合一些ORM框架多表查询的实体关系，如果把控不当，便是失去领域驱动而回到了数据模型驱动。

### What’s change

可以尝试另一种方式，我们应当保留的内容如下：

-   应用层

-   领域层（实体、工厂、服务）

-   仓储层

-   引入新的模式：依赖倒置（六边形架构）、DSL（Domain-specific
    language领域专用语言）

构成一个简化的DDD模式：

![](media/b8135bce17e54eb4db2576ace9d5246f.png)

上图中，领域服务service、领域实体entity、factory这三大组件构成了运转的核心，其工作流程很简单：

Service通过DSL组织业务所需要的entity字段，而factory识别service和entity的关系后，只有合法的domainentity才可以被service匹配，然后转化为一种数据结构course，course封装了业务过程的一个或多个步骤，这些步骤以一种通用的方式提交至仓储，仓储则是一个接口层，它限定了跟数据层的交互方式：courseRepository通过course交互，classcialRepository则是经典三层方式。

最关键的一点是，业务需求的变化已经被封装在course内，Dao无须再根据service定义一个方法提供业务支持，如：getBookByIdAndTitle，因为它已经写入了course中，我们的业务层也无需针Dao层进行注入，只需要将方法和作用对应即可，如inStorage，delStorage，updateType等等。

下面的图展示了领域服务、实体、工厂的关系：

![](media/7d121b6bd3b6d9f544b08fc45432adb9.png)

下面的图展示了领域内服务的实现方式：

![](media/53edc74af71005b9e28c680df33ea7d4.png)

下面的图展示了course到仓储的关系：

![](media/ba8faf52a97e4f5581255d254bf4cc12.png)

### What does CloverFramework do

-   将简化的DDD模式固化为一个核心，并提供DSL来组织业务需求语言。

-   实现领域内业务过程可编排、可重用、可重组。

-   根据领域树实现对过程和领域的关联和过滤。

-   依赖倒置，提供API与数据层和UI对接。

所有的这些，归纳为即是一个通用领域DSL框架所做的事情。

How to used
-----------

### Let’s create first course

首先我们来一个万金油用例，例如用户资料完善。因此，我们需要准备一些组件。

这是实体类：User，除了给他标记了一个Domain的注解之外，它跟平常并没有什么不同，对于DDD来讲可能就是属于贫血模型，我是提倡贫血模型的。

\@Domain("User")

**public class** User {

**private int** id;

**private** String username;

**private** String nick;

**private** String password;

**private** String email;

**private** String phone;

**private** String question;

**private** String answer;

**private** Date login;

**private** Date logout;

**private int** visit;

**private** String ip;

}

同时创建对应的枚举类型，因为这样更符合我们的习惯，不过并不是必需的。类似spring
data
jpa以及queryDSL，我们可以利用EntityGenerator自动生成字典，<https://github.com/LoongYou/EntityGenerator>，目前这个工具并未集成到框架中：

**public enum** User_d{

*User //字典的第一个元素必须是对应的类型名称*

,*id*

,*username*

,*nick*

,*password*

,*email*

,*phoner*

,*question*

,*answer*

,*login*

,*logout*

,*visit*

,*ip*

}

这是服务类：UserService，同样的，除了给它标记一个Domain的注解以及实现DomainService接口之外，它跟平常并没有什么不同。

\@Domain("User")

**public class** UserService **implements** DomainService{

}

接下来实现业务的第一步，在service中创建一个方法，例如创建一个login的方法，在方法中实现查询和校验，在这个演示中，我们只需要实现查询过程即可，例如首先查询用户的用户名和密码：

**public int** login(String username) {

*CourseProxy* proxy = **new** *CourseProxy*(**this**);

proxy.START().get(User_d.*id*).by(User_d.*password*).eq(username).END();

System.*out*.println(proxy.getCurrCourse());

//do something

**return** 0;

}

我们在login方法中创建一个CourseProxy，通过它可以更方便的操作course，然后START()开启一条DSL任务语句，END()为结束语句，最后输出刚刚创建的Course：

Course id:1221555852 get:

Get User.id , User.password by:

By User.username

这相当于你在原来的Dao层编写了一道方法如：getIdAndPasswordByUsername(),但是现在你已经无需这么做了，开始解放你的接口吧。

### Submit Course to repository and execute course

刚才我们已经创建了一条DSL语句，接下来只需将它交给仓储处理，这里，创建一个用于User领域的通用Dao：

**public class** GeneralDao **implements** ICourseMode\<User\>{

\@Override

**public** User get(Course course) {

System.*out*.println("Dao got the course:"+course.getId());

//do database query

**return new** User();

}

\@Override

**public int** add(Course course) {

**return** 0;

}

\@Override

**public int** put(Course course) {

**return** 0;

}

\@Override

**public int** remove(Course course) {

**return** 0;

}

}

你可以看到，这个Dao实现于ICourseMode接口，只需要实现GET\\ADD\\PUT\\REMOVE四种通用操作即可，仓储会根据course的类型调用对应的接口方法，**无须再操心漫长的方法名了。**

现在为了让course和repository进行类型检查，在刚刚的login方法中，我们需要作对应的操作：

**public int** login(String username) {

CourseProxy\<User\> proxy = **new** CourseProxy\<User\>(**this**);

proxy.setRepository(**new** CourseRepository\<User\>() {{setMode(**new**
GeneralDao());}});

proxy.START().get(User_d.*id*).by(User_d.*password*).eq(username).END();

User u = proxy.executeOne();

System.*out*.println(proxy.getCurrCourse());

System.*out*.println(u);

//do something

**return** 0;

}

只需要给CourseProxy加上User的泛型，并且设置所使用的仓储接口即可，在实际中可以将这些工作交给IOC框架来做。

下面展示了仓储调用Dao执行DSL的结果：

Dao got the course:1221555852

Course id:1221555852 get:

Get User.id , User.password by:

By User.username

<com.entity.User@5ccd43c2>

### No dictionary

之前说到，不一定需要实体类的字典也可以实现：

proxy.START().get(*user*.getId(),*user*.getPassword()).by(*user*.getUsername()).eq(username).END();

所需要的只是通过工厂得到一个User类型的样板*Staple：*

**private static final** User *user* =
EntityFactory.*getStaple*(User.**class**);

在实际中，如果没有通过容器加载工厂，那么需要先得到工厂实例：

**private static final** User *user* =
*EntityFactory.getInstance().getStaple(User.class)*;

这种方式无需字典，采用哪一种方式取决于你。

### Data structure

目前，course支持两种toString方式：

proxy.getCurrCourse().toString()

proxy.getCurrCourse().getJsonString()

**toString：**一种简明的方式输出course的摘要信息

Get User.id , User.password by:

By User.username

**JsonString：**具体的节点和属性，这些属性可以通过course的getter得到：

{

"type" : "get", -----------------------------------------------------\>节点类型

"optype" : null, ---------------------------------------------------\>操作类型

"fields" : [ "User.id", "User.password" ], --------------------\>操作字段

"types" : [ "User" ],
----------------------------------------------\>字段实体类型

"values" : null, ---------------------------------------------------\>输入值

"son" : null, -------------------------------------------------------\>子节点

"next" : {-----------------------------------------------------------\>后节点

"type" : "by",

"optype" : "eq",

"fields" : [ "User.username" ],

"types" : [ "User" ],

"values" : [ "jackson" ],

"son" : null,

"next" : null

}

}
