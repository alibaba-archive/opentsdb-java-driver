# 阿里云 Opentsdb Java Driver

&nbsp;&nbsp;&nbsp;&nbsp;阿里云Opentsdb的java sdk可以让用户提高访问Opentsdb的易用性 .您可以花几分钟先了解下面的文档开始；

- [API 文档](https://help.aliyun.com/document_detail/100634.html?spm=a2c4g.11174283.6.603.56d93c2eYwpOnQ )
- [开发者指南]( http://opentsdb.net/docs/build/html/index.html)
- [Issues]( https://github.com/aliyun/hbase-tsdb-java-sdk/issues)
- [Release](https://github.com/aliyun/hbase-tsdb-java-sdk/releases )

&nbsp;&nbsp;&nbsp;&nbsp;当然非常欢迎一起参与到这个项目，给这个项目贡献代码。

## 需要的软件

- Java 1.8 or later
- Maven

## 安装使用

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;有2种方法可以使用阿里云Opentsdb 的sdk：

&nbsp;	&nbsp;&nbsp;&nbsp;1. 第一种方法就是从maven引入相关的阿里云Opentsdb的sdk jar包(暂时不支持，稍后会发布maven仓库). 引入模式如下:

```
<dependency>
    <groupId>com.aliyun.hbase</groupId>
    <artifactId>tsdb-sdk</artifactId>
    <version>${version}</version>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;相关的${version}版本号可以参考我们对应的[Release list](https://github.com/aliyun/hbase-tsdb-java-sdk/releases ) 进行获取;

&nbsp;&nbsp;&nbsp;&nbsp;2. 第二种方法就是把相关的tsdb的jar包进行编译，编译命令参考下面的build项；完事以后下载下面的相关的jar包；

- [httpasyncclient-4.1.3](http://central.maven.org/maven2/org/apache/httpcomponents/httpasyncclient/4.1.3/httpasyncclient-4.1.3.jar?spm=a2c4g.11186623.2.15.5ada3b14kRS3c0&file=httpasyncclient-4.1.3.jar)

- [httpclient-4.5.3](http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar)

- [httpcore-4.4.6](http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.6/httpcore-4.4.6.jar)

- [httpcore-nio-4.4.6](http://central.maven.org/maven2/org/apache/httpcomponents/httpcore-nio/4.4.6/httpcore-nio-4.4.6.jar)

- [fastjson-1.2.54](http://central.maven.org/maven2/com/alibaba/fastjson/1.2.54/fastjson-1.2.54.jar)

- [slf4j-api-1.7.25](http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar)

- [junit-4.12](http://central.maven.org/maven2/junit/junit/4.12/junit-4.12.jar)

- [metric-core-2.2.0](http://central.maven.org/maven2/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar)

- [commons-cli-1.4](http://central.maven.org/maven2/commons-cli/commons-cli/1.4/commons-cli-1.4.jar)

- [guava-27.0.1-jre](http://central.maven.org/maven2/com/google/guava/guava/27.1-jre/guava-27.1-jre.jar)

  &nbsp;&nbsp;&nbsp;put all the jar package to the project lib then you can use this SDK .

  &nbsp;&nbsp;&nbsp;&nbsp;把所有的用到的jar包include到build path，或者放到你自己的lib库文件夹，引入到项目即可；

  


## 编译

&nbsp;&nbsp;&nbsp;&nbsp;可以从github上面checkout出对应的sdk的代码，然后进行编译，可以使用maven进行处理，参考下面的maven命令即可，在target目录下面获取需要的jar包；

```
mvn clean compile package -DskipTests
```

&nbsp;&nbsp;&nbsp;&nbsp;把上面打包的jar包从各个文件夹的target文件夹下取出来，放到build path里面进行引用即可。

## Authors

- [maxwellguo]( https://github.com/cclive1601)

## License

[Apache License 2.0]