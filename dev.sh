# This script uses the dev properties.

CLASSPATH=`mvn dependency:build-classpath | grep -v INFO`

java -classpath $CLASSPATH:target/classes org.center4racialjustice.legup.Main conf/dev.properties
