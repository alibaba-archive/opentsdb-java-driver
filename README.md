# Aliyun Opentsdb Driver for Java

&nbsp;&nbsp;&nbsp;&nbsp;The Aliyun Opentsdb driver for Java enables Java developers to easily work with Aliyun Opentsdb . You can get started in minutes using **\*Maven*** or by downloading 

- [API Docs](https://help.aliyun.com/document_detail/100634.html?spm=a2c4g.11174283.6.603.56d93c2eYwpOnQ)
- [Developer Guide]( http://opentsdb.net/docs/build/html/index.html)
- [Issues]( https://github.com/aliyun/hbase-tsdb-java-sdk/issues)
- [Release](https://github.com/aliyun/hbase-tsdb-java-sdk/releases )

&nbsp;&nbsp;&nbsp;&nbsp;Welcome to participate in this project an contribute your code.

## Requirements

- Java 1.8 or later
- Maven

## Install

&nbsp;&nbsp;&nbsp;&nbsp;There is two ways to use Aliyun Opentsdb SDK :

&nbsp;&nbsp;&nbsp;&nbsp;1. The first way to use the Aliyun Opentsdb SDK for Java in your project is to consume it from Maven (This method will be support latter ). Import as follows:

```
<dependency>
    <groupId>com.aliyun.hbase</groupId>
    <artifactId>tsdb-sdk</artifactId>
    <version>${version}</version>
</dependency>
```

&nbsp;&nbsp;&nbsp;&nbsp;The recommend release version number can get from the git Release list;

&nbsp;&nbsp;&nbsp;&nbsp;2. Second way is Download the SDK jar package, you can first build the jar package of the tsdb jar, or just download the java code and build the code. Then you should download the  jar list :

- [httpasyncclient-4.1.3](http://central.maven.org/maven2/org/apache/httpcomponents/httpasyncclient/4.1.3/httpasyncclient-4.1.3.jar?spm=a2c4g.11186623.2.15.5ada3b14kRS3c0&file=httpasyncclient-4.1.3.jar)

- [httpclient](http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.3/httpclient-4.5.3.jar)

- [httpcore](http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.6/httpcore-4.4.6.jar)

- [httpcore-nio](http://central.maven.org/maven2/org/apache/httpcomponents/httpcore-nio/4.4.6/httpcore-nio-4.4.6.jar)

- [fastjson](http://central.maven.org/maven2/com/alibaba/fastjson/1.2.35/fastjson-1.2.35.jar)

- [slf4j-api](http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.21/slf4j-api-1.7.21.jar)

- [junit](http://central.maven.org/maven2/junit/junit/4.12/junit-4.12.jar)

- [metric-core](http://central.maven.org/maven2/com/yammer/metrics/metrics-core/2.2.0/metrics-core-2.2.0.jar)

- [commons-cli](http://central.maven.org/maven2/commons-cli/commons-cli/1.4/commons-cli-1.4.jar)

  &nbsp;&nbsp;&nbsp;put all the jar package to the project lib then you can use this SDK .

## Build

&nbsp;&nbsp;&nbsp;&nbsp;Once you check out the code from GitHub, you can build it using Maven. Use the following command to build:

```
mvn clean compile package -DskipTests
```

&nbsp;&nbsp;&nbsp;&nbsp;You can set the package jar from different files like java-driver-core/java-driver-example/java-driver-test and set them into the build path. Then you can use them to put data to opentsdb .

## Authors

- [maxwellguo]( https://github.com/cclive1601)

## License

[Apache License 2.0]
