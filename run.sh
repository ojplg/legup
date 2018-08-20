# To generate classpath use 
# mvn dependency:build-classpath -Dmdep.outputFile=cp.txt

MVNPATH=\
/home/oliver/.m2/repository/org/apache/pdfbox/pdfbox/2.0.4/pdfbox-2.0.4.jar:\
/home/oliver/.m2/repository/org/apache/pdfbox/fontbox/2.0.4/fontbox-2.0.4.jar:\
/home/oliver/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:\
/home/oliver/.m2/repository/junit/junit/4.12/junit-4.12.jar:\
/home/oliver/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:\
/home/oliver/.m2/repository/org/eclipse/jetty/jetty-server/9.4.11.v20180605/jetty-server-9.4.11.v20180605.jar:\
/home/oliver/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:\
/home/oliver/.m2/repository/org/eclipse/jetty/jetty-http/9.4.11.v20180605/jetty-http-9.4.11.v20180605.jar:\
/home/oliver/.m2/repository/org/eclipse/jetty/jetty-util/9.4.11.v20180605/jetty-util-9.4.11.v20180605.jar:\
/home/oliver/.m2/repository/org/eclipse/jetty/jetty-io/9.4.11.v20180605/jetty-io-9.4.11.v20180605.jar:\
/home/oliver/.m2/repository/org/projectlombok/lombok/1.16.4/lombok-1.16.4.jar:\
/home/oliver/.m2/repository/org/projectlombok/lombok-maven/1.18.0.0/lombok-maven-1.18.0.0.pom:\
/home/oliver/.m2/repository/org/postgresql/postgresql/42.2.4/postgresql-42.2.4.jar:\
/home/oliver/.m2/repository/org/jsoup/jsoup/1.11.3/jsoup-1.11.3.jar:\
/home/oliver/.m2/repository/org/apache/velocity/velocity/1.7/velocity-1.7.jar:\
/home/oliver/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:\
/home/oliver/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar:\
/home/oliver/.m2/repository/org/apache/logging/log4j/log4j-core/2.7/log4j-core-2.7.jar:\
/home/oliver/.m2/repository/org/apache/logging/log4j/log4j-api/2.7/log4j-api-2.7.jar

java -classpath $MVNPATH:target/classes org.center4racialjustice.legup.Main $1
