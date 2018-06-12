CloverFramework
==

CloverFramework是一个业务层DSL框架，以组装DSL的方式来实现结构化数据查询statement，应用在web开发，符合领域驱动的设计理念，并简化了过程。其直接的作用是可用来代替ORM框架的statement配置文件，如hibernate的<kbd>hql</kbd>，或者是mybatis的<kbd>mapper.xml</kbd>，理论上可以减少80%这样的脚本。而它更有意义的是可以构建一个完整的service层核心，并且不依赖于DAO层接口，开发人员可以更专注和全面的对待业务逻辑，更加专注于DML优化。

在进行web应用开发时，你可能经常需要跟这种xml配置接触：

```xml
<select id="selectByName" parameterType="Integer" resultType="user">
  select id,password,type from user where user.name = #{username}
</select>
```

又或者是hql：

```java
session.createQuery("select user.id,user.password,user.type from user where name like :name")
.setParameter("name", "%张%").list();
```

当service层使用一个dao层接口签名的方法,还同时需要上述的XML或者HQL才能把这个方法实现:
```java
public List<User> getIdAndPasswordAndTypeByName(String username);
```

But，我们原本可以用更简单的方式搞定：<kbd>DSL</kbd>
```java
Master().get(USER.id,USER.password,USER.type).by(USER.name).eq(username);
```

这里的区别是：
- DSL比xml、hql更精简
- DSL比xml、hql的参数设置更直接
- DSL可享有字段和语法校验而另外两者都没法校验
- DSL可同时直接实现接口的方法签名和所需的statement，也就意味着你可以对应的省略一个Dao接口方法，而实际上并不止如此

使用mybatis动态sql，需要更多的标签：
```xml
<select id="findActiveBlogWithTitleLike" resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <if test="title != null">
    AND title like #{title}
  </if>
</select>
```

这对于DSL是小事一桩：
```java
Master().get(BLOG).by(BLOG.state).eq("ACTIVE")
  .IS(blog.getTitle()!=null,(then)->{then.and(BLOG.title).like(blog.getTitle)});
```

又如，对于下面这种xml构建的动态sql，AND关键词的位置是有限制的，你也许还被这种问题困扰过：
```xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG
  <where>
    <if test="state != null">
         state = #{state}
    </if>
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>
```

但是具备语法校验的DSL可以痛快的完成这些构建：
```java
Master().get(BLOG).by(BLOG.state).
  .IS(blog.getState()!=null,(then)->{then.by(BLOG.state).eq(blog.getState())
  .IS(blog.getTitle()!=null,(so)->{so.and(BLOG.title).like(blog.getTitle())
  .IS(blog.getAuthor()!=null && author.getName()!=null,
                            (that)->{that.and(AUTHOR.name).like(author.getName())}});
```
如果你对这些改变抱有想法，尽可能了解下面更多有关CloverFramework的内容:)


features
--

- DSL的领域范围管理
- 支持任意类型DSL自定义
- 支持方法字面值定义
- 支持动态DSL构建
- 可插拔的DSL缓存
- 支持值对象映射
- 支持参数映射
- 支持并发、异步、函数式

Quick begin
--
现在我们可以搞点事情了，使用CloverFramerwork的DSL构建statement，方法很简单，按照如下几步实现即可：

实体类，我们最好称之为领域实体：
```java
public class Demo {
	private String f1;
	private String f2;
	private String f3;
	private String f4;
	private String f5;
	private String f6;
	private String f7;
	private String f8;
	private String f9;
	private String f10;
	public String getF1() {
		return f1;
	}
	public void setF1(String f1) {
		this.f1 = f1;
	}
  ......setter and getter
}
```

对应于实体的字典：
```java
public enum DEMO{
	Demo,
    f1,f2,f3,f4,f5,f6,f7,f8,f9,f10
}
```

对应于实体的领域服务：
```java
@Domain("Demo")
public class ActionTest extends DomainAction<Demo>
```

下面可以开始构建你所需的DSL了，例如，构建一个对应于`select demo.f1,demo.f2 from demo where demo.f3 = 'hello'`的DSL：
```java
public void test(){
    Master().get(DEMO.f1,DEMO.f2).by(DEMO.f3).eq("hello");
    System.out.println(getCurrCourse());
    println(getCurrCourse().getJsonString());
}
```

或者，用这种更加简单粗暴的方式去做，它不需要上面的字典：
```java
Demo demo = getStaple(Demo.class);
Master().get(demo.getF1(),demo.getF2()).by(demo.getF3()).eq("hello");
```

对应于刚刚构建的DSL字符表达形式输出：
```
root id:
get Demo.f1,Demo.f2
by Demo.f3 eq  values:[hello]
```

以及json格式的输出，根据如下结构，你很容易就可以推导出这个DSL的数据结构：
```json
{
  "type" : "root",
  "types" : [ ],
  "fields" : [ ],
  "optype" : null,
  "values" : null,
  "son" : null,
  "next" : {
    "type" : "get",
    "types" : [ "Demo" ],
    "fields" : [ "Demo.f1", "Demo.f2" ],
    "optype" : null,
    "values" : null,
    "son" : null,
    "next" : {
      "type" : "by",
      "types" : [ "Demo" ],
      "fields" : [ "Demo.f3" ],
      "optype" : "eq",
      "values" : "[hello]",
      "son" : null,
      "next" : null
    }
  }
}
```

接下来要做的事情就是将这个DSL翻译为DAO层statement，尽管已经不是什么难事了，但CloverFramerwork还是提供了一系列的配套组件，可以将这个过程固化，并通过一种规范去维护系统不同层面的架构，在后面的介绍中将会对其进行解析。

Architecture
--

### 改变原有模式的不足

试想一下一个进销存机制是如何工作的，它分为<kbd>销售</kbd>、<kbd>财务</kbd>、<kbd>仓库</kbd>三种角色，大部分的公司和业务，都是按照这种模式来运作的。领域驱动设计基于这种思想，它认为一个完整的业务核心必需由这三个角色组成（<kbd>应用层</kbd>、<kbd>领域层</kbd>、<kbd>仓储层</kbd>），并且独立于数据层面，它的好处是业务层不需要关心数据访问层（DAO）的API，就可以应对复杂的业务逻辑。

然而，领域驱动设计有一个致命的痛点，**那就是如何将变化的业务IO转化为数据操作**。对此，早期的做法是将领域的实体分割为一个一个的值对象（VO），通过值对象的组合来应对业务的变化，但实际上仍然严重依赖于ORM的思想和DAO层API，不客观的讲，这将原本简单的事情复杂化了。

### 引入新的模式

#### DSL
DSL（Domain-specific language）领域特定语言，针对一个特定的领域，具有受限表达性的一种计算机程序语言。可以看做是一种抽象处理的方式。DSL通常分为外部DSL和内部DSL，外部DSL就是如XML、config等文本文件脚本，需要应用程序解析后执，而内部DSL是具有特定的语法，编写的脚本是一段合法的程序，具有特定的风格。这两者的区别是语义模型定义是外部还是内部。CloverFramerwork所使用的DSL属于内部DSL。

使用DSL所得到的好处是：
- 提高开发效率，通过DSL来抽象构建模型，抽取公共的代码，减少重复的劳动；
- 和领域专家沟通，领域专家可以通过DSL来构建系统的功能；
- 执行环境的改变，可以弥补宿主语言的局限性；

所以，采用DSL来应对变化的业务IO，是一个不错的选择。CloverFramerwork使用DSL是一种基础表达，你可以像外部DSL那样编写自己的语义模型，理论上可以应对绝大多数业务变化需求，同时可能支持高级的语法实现，如工作流引擎。

#### 依赖倒置
这是领域驱动设计的基本体现，业务领域驱动出发是主动方，而其他层面如DAO、视图层相对而言则是依赖方，这跟我们经典的三层架构业务层依赖于DAO层API相反，在依赖倒置模式中，DAO层依赖(或者实现)于业务层的API，通过这种方式，业务层就能够维护完全独立和核心运作方式。

仓储层就是用于隔离业务层和DAO层的接口层，它用业务的数据操作描述来定义数据访问操作，例如数据层的CRUD操作，在仓储定义为GET ADD PUT REMOVE，实际上在CloverFramerwork中进一步简化为query和update。

#### 翻译器
DSL所构建的是一种领域内通用的语言，需要将其转换为数据访问实现所采用的结构化语言，因此你需要实现一个翻译器，根据所采用的ORM、DB，你可以自行实现翻译器。在Quick begin中已经说明一点，CloverFramerwork构建的DSL是具有简单数据结构的格式（链表），你只需要进行简单的递归即可将其映射为所需的结构化语言。我后续会首先实现用于MYSQL的sql翻译器。

Working
--

也许此时你更加感兴趣的是如何构建各种DSL，but，我想此时还是先对CloverFramerwork的工作方式进行了解，这有助于你更快的确定它是否适用于你的情况，从而为你节省时间。

### A simple work
你可以按照经典三层架构的方式来实现一个业务过程，但你无须太多改变，并且节省更多代码。例如我们简单的实现一个用户注册的过程：

service方法实现：

```java
//如果用户已存在，则提示用户名重复，否则保存新的用户
//User_d是User实体的字典
@Test
	public void register() {
    User user = new User();
    		user.setUsername("jackson");
    		user.setEmail("wing@hao123.com");
    		user.setPassword("123456");

    		User result = null;
    		int result2 = 0;

    		Master("checkName").get(User_d.id,User_d.password).by(User_d.username).eq(user.getUsername());

    		if((result = execute($("checkName")))!=null) {
    			println("username is repeated");
    		}else {
    			result2 = Master("addUser").add(User_d.username,User_d.password,User_d.email)
    			.setValues(user.getUsername(),user.getPassword(),user.getEmail()).commit();
    		}
            println("result:"+result);
    		println("result2:"+result2);
    		println(toString());

	}
```

Dao的实现：
```java
public class UserDao implements CourseMode<User>{

	@Override
	public User query(Swaper<User> swaper) {
		String id = swaper.open().id();
		System.out.println("Dao got the course:"+id);
		//do something
		return new User();
	}

	@Override
	public int update(Swaper<User> swaper) {
		String id = swaper.open().id();
		System.out.println("Dao got the course:"+id);
        //do something
		return 0;
	}
```

用户重复的结果：
```
===================== register
Dao got the course:checkName
username is repeated
result:com.entity.User@735f7ae5
result2:0

root id:checkName
get User.id,User.password
by User.username eq  values:[jackson]
```

用户添加成功的结果：
```
===================== register
Dao got the course:checkName
Dao got the course:addUser
result:null
result2:1

root id:addUser
add User.username,User.password,User.email values:[jackson, 123456, wing@hao123.com]

root id:checkName
get User.id,User.password
by User.username eq  values:[jackson]
```

就这么简单，算一下我们节省了什么：
- 一个Dao接口方法：User getIdAndPasswordByUsername()；
- 一个Dao接口方法：int addUsernameAndPasswordAndEmail(String username,Sring password,String email);
- User getIdAndPasswordByUsername()方法的实现；
- int addUsernameAndPasswordAndEmail(String username,Sring password,String email)方法的实现；
- 这些方法里面的参数描述；
- 还有这些方法的注释说明；

我们所做的仅仅是用DSL的方式将这些Dao接口方法抄袭了一遍而已:)

接下来你要做的事情我想也是水到渠成了，你只需将DSL转为SQL或其他statement，然后提交数据库查询，最后返回结果，对了，尽量翻译成prepareStatement，相信这对于你而言不是什么难事。CloverFramerwork提供了一系列的DSL访问接口和迭代器，帮助你顺利的完成翻译。详见<kbd>API</kbd>。

同时，这并不意味着全盘放弃经典的DAO接口方式，你仍然可以利用经典Dao仓储按以前的方式操作：
```java
//for example use Mybatis sql mapper
public class ClassicalGeneralDaoImpl implements IClassicalMode{

	@Override
	public <E> E get(Class<E> Class,Integer key) {
		SqlSession session = factory.openSession();
		E e = session.selectOne(Tools.pre(namespace, Class)+"selectOne", key);
		session.close();
		return e;
	}
......
```

```java
ClassicalRepository repository = new ClassicalRepository();
repository.setMode(new ClassicalGeneralDaoImpl());//your daoimpl
blog = repository.get(Blog.class, id, this);
System.out.println(blog.getContent());
```


### Working model
不用着急，DSL构建工作模式，了解一下。

由于CloverFramerwork提供了DSL的缓存，可以重复利用构建好的DSL，大大加快了工作效率。也因此，你所构建的DSL可能是共享的，也可能是线程独立的，对于并发性的web service，这都是必需面对的。

#### 创建模式

如上面的例子，你已经知道使用Master(...)方法可以创建一条DSL语句，而这仅仅是其中一种模式，现在你有多种模式来创建不同状态的DSL：

创建而不缓存,通常用于一次性执行：
```java
Master().get(DEMO.f1,DEMO.f2,DEMO.f3,DEMO.f4).execute();//查询
Master().get(DEMO.f1,DEMO.f2,DEMO.f3,DEMO.f4).commit();//提交修改
```

创建并缓存,key是A，那么你可以在需要的时候提取出来并执行之：
```java
Master("A").get(DEMO.f1,DEMO.f2,DEMO.f3,DEMO.f4);
$("A").execute();
$("A").commit();
```

创建分支,使用github的你一定对此不陌生吧,你可以通过分支来进行主副DSL的元素过滤、合并、交叉、互补、追加节点等操作，并且无须担心这些分支是否会对Master造成影响，因为创建分支是全新的DSL：
```java
//首个元素是模式标识，如 M
Branch("A").get(M).by(DEMO.f5).eq(100);//不缓存
BranchM("A").get(M).by(DEMO.f5).eq(100);//缓存
BranchM("A").get(CB,DEMO.f5,DEMO.f6,DEMO.f7,DEMO.f8,DEMO.f1,DEMO.f2);
BranchM("A").get(MA,DEMO.f5,DEMO.f6,DEMO.f7);

```
```
Master
root id:A
get Demo.f1,Demo.f2,Demo.f3,Demo.f4

M：
root id:A_FM_1528702643674
get Demo.f1,Demo.f2,Demo.f3,Demo.f4
by Demo.f5 eq  values:[100]

CB：
root id:A_FM_1528703365964
get Demo.f6,Demo.f5,Demo.f7,Demo.f8

MA：
root id:A_FM_1528703479785
get Demo.f1,Demo.f2,Demo.f3,Demo.f4,Demo.f5,Demo.f6,Demo.f7
```
- 并集：U
- 交集：I
- 补集：C
- 前置并集：UB
- 后置并集：UA
- 前置混合：MB
- 后置混合：MA
- 正交：M
- 反交：RM
- 左补：CB
- 右补：CA

#### Values
CloverFramerwork实现的DSL是具有参数映射效果，或者说，支持某种参数映射规则，你可以自行实现这个规则，而在默认情况下，所采用的是目前通用的参数映射形式，多数情况下你无须改变，CloverFramerwork的DSL只会检查参数个数，但是对于参数类型和顺序以及合法性不会检查，你可以利用ORM框架增强合法性校验，同时可能付出一些性能代价，你也可以自己实现以取得均衡效果：

当且仅当节点字段大于一个时，value的个数必需和字段个数一致：
```java
Master("A").add(User_d.username,User_d.password,User_d.email).setValues("jackson","888888","168@dd.com");
```

如果你的字段数量较多，使用值对象方式，不过在翻译器或者DAO中你需要自行处理映射：
```java
Master("A").add(User_d.username,User_d.password,User_d.email).setObject(user);
```

当节点字段仅有一个时，value的个数满足1个或1个以上即可，翻译的时候你可以对应为in(,,,)：
```java
Master("getUser").get(User_d.username,User_d.password,User_d.email).by(User_d.username).eq("jackson","Tomcat");
```

如果使用基本数据类型，可以通过基本类型来设置value而无需隐含的转换：
```java
Master("A").get(count(DEMO.f2),DEMO.f1,DEMO.f4,count(DEMO.f3))
.by(DEMO.f10).eq(20).setInt(30).setString("hello");//最后的值覆盖新的值
```

#### Result
Result是DSL语句执行后返回数据的封装，规定了返回的数据格式，你可以通过各种getter来得到相关数据，包括如下几种类型：
```java
//T 是领域实体泛型变量
List<T> getList();
List<Object> getObjectList();
Map<String, Object> getMap();
byte getByte();
short getShort();
int getInt();
float getFloat();
long getLong();
double getDouble();
boolean getBoolean();
String getString();
```

关键一点，你和DAO层需要约定一个规则，例如查询必需返回一个List<T>到结果中如果成功，或者如果仅有一个字段有值，则优先返回到对应的value中，否则认为失败等等。

在DAO实现中，通过swaper交换接口将查询的数据转换为result，你只需要将返回的数据根据List、Map、或者某个值组织好传入交换接口即可：
```java
public User query(Swaper<User> swaper) {
		String id = swaper.open().id();
		System.out.println("Dao got the course:"+id);
		//do something
		swaper.setResult(list<T>, list<Object>, map<String,Object>, "hello");
		return new User();
	}
```

或者实现DataSet接口的对象，传给swaper，这样以把各种查询结果用通用的形式将结果转换为额定的格式：
```java
public interface Swaper<T> extends Iterable<Wrapper>{
	Wrapper open();
	void setResult(DataSet<T> data);
	void setResult(List<T> list, List<Object> objectList, Map<String, Object> map, Object value);
	void setResult(Result<T> result);
	void close();
}

public interface DataSet<T> {
	/**返回领域类型List*/
	List<T> toList();
	/**返回对象类型List*/
	List<Object> toObjectList();
	/**返回一个对象类型map*/
	Map<String, Object> toMap();
	/**返回一个值*/
	Object value();
}
```

#### DSL状态和共享
当你的应用程序是多线程的时候，就存在共享使用的DSL的情况，在任何情况下，CloverFramerwork都保证对应每一个线程使用的value和result是本地线程中的，不存在线程安全问题。但是DSL的节点结构在多数情况下是共享的，也就是意味着存在修改扩散的问题。因此，你可以通过修改DSL的状态来维护DSL节点结构的可变状态：
```java
//给这条DSL上锁,也就是关闭共享，那么它将不能被修改和追加节点
Master("A").get(count(DEMO.f2),DEMO.f1,DEMO.f4,count(DEMO.f3)).by(DEMO.f10).eq(20).LOCK();

//A 已经上锁，因此这一条DSL是全新的，并且不会被缓存
Master("A").get(DEMO.f1,DEMO.f4).by(DEMO.f10).eq(20);

//UNLOCK是DSL的默认状态，可任意修改添加节点
$("A").UNLOCK();

//关闭这条DSL，那么在LOCK的基础上，它的value和result不可被修改，通常用于提交存储过程
$("A").END();

//加入工作区
$("A").READY();
```
如果你确定个DSL是用于共享的，那么应当确保在服务内它的创建语句只会出现一次，而非多处修改，否则无法保证共享安全性。对于上锁的DSL尽可能在初始化方法中，尽可能通过$操作方法来提取，然后你可以进行Branch或者执行之，避免无谓的重复新建。除了READY，其他修改DSL状态的操作都需要获得锁，所以你应当避免频繁的修改状态。

现在允许你可以通过lambda表达式的方式来构建DSL，尤其对于LOCK状态的DSL的构建语句，可以避免重复新建：
```java
//如果DSL已存在，则不会执行lambda
Master("A",(a)->{
			Master(a).get(count(DEMO.f2),DEMO.f1,DEMO.f4,count(DEMO.f3))
      .by(DEMO.f10,DEMO.f8).eq(k,b).and(demo.getF5()).eq(user.getId(),2);
			});
```

### Execute DSL
DSL的执行分为query和commit两种类型，query返回的是查询结果，commit返回的是修改行数。
```java
T execute(C c);
int commit(C c);
```

你可以在DSL节点上调用执行方法，也可以在service方法中提取并执行，如果DSL的类型和执行类型不一致，会抛出异常，例如：
```java
Master("A").get(DEMO.f1,DEMO.f2,DEMO.f3,DEMO.f4).commit();//会导致异常
```

在service中提供了几种重载的执行方法和提取DSL的方法：
```java
//从缓存中获取key对应的course，优先从共享区中获取
$("A");
//根据给定的范围从范围内获取key对应的course
//service范围
$("A",domain);
//本地线程范围
$("A",local);
//执行最后操作的course语句
execute();
//执行一个DSL
execute($("A"));
//提取并执行DSL
execute("A");
//自动根据DSL类型执行或提交操作
executeOrCommit($("A"));
```

对于上述的提取和执行方法，你还可以用类似的方式进行异步执行,你不必阻塞的等待DAO返回result，此时你可以干别的事情，当你调用getResult(...)的时候，如果异步结果已经返回，则直接返回result，否则根据给定的等待超时、是否取消任务来工作：
```java
$("A").resultAsync();

$("A").getResult(4000,true).getString();
//等待超时4秒，如果没有完成，则取消异步的任务，如果输入0，那么会一直阻塞直到返回，
//如果你没有提供这些数值，那么默认会一直阻塞
```

Workspace
--

工作区实际上是一个本地线程执行的DSL的上下文，多线程环境下，每个线程持有和需要执行的DSL是不一样，这些DSL会有条件的被加入工作区，有点像一个FIFO类型的栈：
```java
@Test
	public void workSpace() {
		startWork();
		Master("A").get(User_d.type).groupBy(User_d.type).READY();
		Master("B").get(count(User_d.id)).READY();
		Master("C").get(count(User_d.id)).by(User_d.type).eq(1).READY();
		System.out.println("WorkSize:"+getWorkSize());
		push();
		endWork();
		System.out.println("WorkSpace:"+getWorkSpace());
	}
```

通过READY()将DSL标记后，这些DSL会被加入工作区，然后可以使用push推送到仓储，便可一次性顺序执行这些DSL：
```
===================== workSpace
WorkSize:20
Dao got the course:A
Dao got the course:B
Dao got the course:C
WorkSpace:[]
```

Dynamic DSL
--

动态是一个语言灵活性的灵魂，内部DSL结合宿主语言，就能够轻易实现动态构建，然而一些ORM框架，如mybatis，通过xml来构建动态的sql，编写体验可以说是相当拙劣的，不客观的讲，XML应该被用于数据交换，编写脚本不是它的擅长。又如mybatis以及hibernate等延迟加载功能，实际上就是业务IO和statement无法调和的补救措施，通过构建动态DSL，你会发现所谓的延迟加载在这里只是一个非常寻常的简单动态DSL而已。

CloverFramerwork内部DSL提供一种宿主语言风格的动态构建手段，无须记忆额外的关键词，与DSL语义没有冲突的方式来构建动态DSL。理论上来说，只要宿主语言能实现的算法逻辑，动态DSL都能实现。

> **if...**

```java
Master().get(BLOG).IS(
    blog.getTitle()!=null,
    (then)->{then.by(BLOG.title).eq(blog.getTitle());}
    );

// OR like this
Master().get(BLOG).IS(blog.getTitle(),null,
    (then)->{then.by(BLOG.title).eq(blog.getTitle());}
    );
```

如上面的if单分支例子，有两种条件书写方式，第一种直接提供一个boolean的判断结果，第二种省略了==判断符，而它门的顺序是没有影响的。如果你比较的是字符串，建议用第一种方式。同样的，后面的各种分支判定都具有这两种书写方式。

> **if...else...**

```java
Master().get(BLOG).IS(blog.getTitle(),null,
    (then)->{then.by(BLOG.title).eq(blog.getTitle());},
    (orthen)->{orthen.by(AUTHOR.name).eq(author.getName());}
    );
```

> **choose...** 相当于switch case break;只要有其中一条满足即结束，没有满足的case则执行default

```java
Master().get(BLOG).choose(
    Case(),
    Case(),
    Case()
);

//for example
Master().get(BLOG).choose(
    Case(blog.getTitle()!=null,(then)->{then.by(BLOG.title).eq(blog.getTitle());}),
    Case(author.getName()!=null,(then)->{then.by(AUTHOR.name).eq(author.getName());}),
    Case(blog.getState()!=null,(then)->{then.by(BLOG.state).eq(blog.getState());})
);

Master().get(BLOG).choose(
    (def)->{def.by(BLOG.featured).eq(1)},  //default optional
    Case(blog.getTitle()!=null,(then)->{then.by(BLOG.title).eq(blog.getTitle());}),
    Case(author.getName()!=null,(then)->{then.by(AUTHOR.name).eq(author.getName());}),
    Case(blog.getState()!=null,(then)->{then.by(BLOG.state).eq(blog.getState());})
);

```

> **Switch...** 相当于switch case  no break;不进行break直到语句结束，节点相同则会替换值，否则被后面的节点替换，如果所有case都为false，就会执行default

```java
//for example
Master().get(BLOG).Switch(
    (def)->{def.by(BLOG.featured).eq(1)},  //default optional
    Case(blog.getTitle(),null,(then)->{then.by(BLOG.title).eq("notitle");}),
    Case(blog.getTitle()!=null,(then)->{then.by(BLOG.title).eq(blog.getTitle());}),
    Case(blog.getState(),"",(then)->{then.by(BLOG.title).eq("temp");})
);

```

Complex DSL
--

欲构建复杂的DSL，首先确保你真的需要复杂的DSL来完成，否则尽可能拆分或者用简单的DSL来完成，因为当中的嵌套、子节点、类型判断将会变得复杂多变，对可读性有一定的影响，也可能增加犯错的几率。不过在CloverFramerwork中，构建复杂DSL并非难事。如果存在难处，那一般是这些复杂DSL对于翻译器可能有较大的考验吧。

首先来看一个纯方法字面值构造的DSL的粗暴方式，此方式用于应对那些比较变态的编码需求：
> $$() 清除无效字面值，你可以看到，在DSL节点构造以外执行的方法是不被当做元素加进去的。当然，如果你确定没有这些无效的方法，是无须$$()的。

```java
Demo demo= getStaple(Demo.class);
demo.getF10();//invalid
demo.getF9();//invalid

Master("a").get($$(),demo.getF1(),demo.getF2(),demo.getF3(),demo.getF4())
.by(demo.getF5(),demo.getF6()).and(demo.getF5());

Get get = Master("b").get($$(),demo.getF1(),demo.getF2(),demo.getF3(),demo.getF4());

demo.getF10();//invalid
demo.getF9();//invalid

get.by(demo.getF5(),demo.getF6()).LIMIT(0, 10);
```

```
root id:a
get Demo.f1,Demo.f2,Demo.f3,Demo.f4
by Demo.f5,Demo.f6
and Demo.f5

root id:b
get Demo.f1,Demo.f2,Demo.f3,Demo.f4
by Demo.f10,Demo.f9,Demo.f5,Demo.f6 Limit 0,10
```

现在来一个正常复杂的DSL：
> count 聚合函数，你可以自定义所需的聚合函数

> $$(DEMO.f5) 相当于增设一个字段，例如下面的语句中相当于 where DEMO.f10 = DEMO.f5，其实它们跟count等聚合函数一样，都是增加子节点的方式，只是名称不一样而已。你可以在字段部分或者value的部分插入子节点，前提是你的翻译器支持这种语法。

```java
Master("a")
.get(DEMO.f1,DEMO.f4,count(DEMO.f2),count(DEMO.f3))
.by(DEMO.f10).eq($$(DEMO.f5)).and(DEMO.f6).eq(1,2);
```

下面是对应的字符串表达，我想你一定在想象如何跟sql对应起来，然而并不太一样是吧？一般而言，DSL的语义模型限制要比具体的目标语言宽松，而非严格的映射，不然就无法满足通用化、扩展和灵活性的需要。但这并不意味着它跟具体语言之间存在必然的失联，仔细观察你会发现这只是一种宽松状态的sql，稍作加工就可以变为合法的sql，总而言之，目前所使用的DSL基本上都是基于结构化查询模式的，和mysqlsql hql等等都是相通的，翻译器只需要根据差别修正即可重用。
```
root id:a
get Demo.f1,Demo.f4,count Demo.f2,count Demo.f3
by Demo.f10 eq  values:[con Demo.f5]
and Demo.f6 eq  values:[1, 2]
```

当然，我并不推荐书写这样一种混搭风格的DSL，反正如果你喜欢就好:)
```java
Master("a")
		.get(demo.getF5(),count(DEMO.f2),DEMO.f1,DEMO.f4,count(DEMO.f3))
		.by(DEMO.f10,DEMO.f8).eq(40,$$(demo.getF5())).and(demo.getF5()).eq(1,2)
		.END();
```

MORE AND MORE
--

CloverFramework的大致内容前面已经阐述了一些，后期我将继续增加更多新功能特性，如领域树、缓存架构、日志模块、消息模块、默认结果、存储过程支持等等，同时感谢您的支持和参与。
