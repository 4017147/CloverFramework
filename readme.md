CloverFramework
==

CloverFramework是一个领域层框架，以组装DSL的方式来实现结构化数据查询statement，应用在web开发,符合领域驱动的设计理念，并简化了过程。其直接的作用是可用来代替ORM框架的statement配置文件，如hibernate的<kbd>hql</kbd>，或者是mybatis的<kbd>mapper.xml</kbd>，理论上可以减少80%这样的脚本。而它更有意义的是可以构建一个完整的service层核心，并且不依赖于DAO层接口，开发人员可以更专注和全面的对待业务逻辑，更专注于DML优化。

在进行web应用开发时，你可能时候经常需要跟这种xml配置接触：

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
  .te(blog.getTitle()!=null,(then)->{then.and(BLOG.title).like(blog.getTitle)});
```

又如，对于下面这种xml构建的动态sql,AND关键词的位置是有限制的，你可能被这种问题困扰过：
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
  .te(blog.getState()!=null,(then)->{then.by(BLOG.state).eq(blog.getState())
  .te(blog.getTitle()!=null,(so)->{so.and(BLOG.title).like(blog.getTitle())
  .te(blog.getAuthor()!=null && author.getName()!=null,
                            (that)->{that.and(AUTHOR.name).like(author.getName())}});
```
如果你对这些改变抱有想法，尽可了解下面更多有关CloverFramework的内容:)


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
一个实体类，我们最好称之为领域实体
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
}```
一个对应于实体的字典
```java
public enum DEMO{
	Demo,
    f1,f2,f3,f4,f5,f6,f7,f8,f9,f10
}```
一个对应于实体的领域服务
```java
@Domain("Demo")
public class ActionTest extends DomainAction<Demo>```

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

>对应于刚刚构建的DSL字符表达形式输出：
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
