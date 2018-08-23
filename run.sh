# To generate classpath use 

CLASSPATH=`mvn dependency:build-classpath | grep -v INFO`

java -classpath $CLASSPATH:target/classes org.center4racialjustice.legup.Main $1
