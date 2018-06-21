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
- 开放的DSL-SQL优化接口

Dependence
--

- cglib-nodep-3.2.5.jar
- jackson-annotations-2.8.0.jar
- jackson-core-2.8.10.jar
- jackson-databind-2.8.9.jar

More information see wiki：[CloverFramework Wiki][4804b337]

  [4804b337]: https://github.com/LoongYou/CloverFramework/wiki "CloverFramework Wiki"
