havlak-jvm-languages
====================

Sources for Havlak Benchmark for several JVM Languages


For the Havlak paper see the report by Robert Hundt from Google to compare the performance of Google Go with C/C++
and other languages such as Java and Scala: https://days2011.scala-lang.org/sites/days2011/files/ws3-1-Hundt.pdf

The sources for Java and Scala can be found here: http://code.google.com/p/multi-language-bench/source/browse/trunk/src/

#####Make sure you run the Groovy code using JAVA_OPTS=-server -Xss15500k -Xmx1G. Otherwise, you will get a stackoverflow.

#####Same thing for Kotlin: VM args -server -Xss15500k required 

Measurements on my machine (Intel Core2 Duo CPU E8400 3.00 GHz, JDK1.7.0_06):

- Java: 52070 ms
- Kotlin: 20246 ms (including performance improvements by bashor)
- Kotlin initial: 52070 ms (my initial port to Kotlin)
- Kotlin cosmetics bashor: 56672 ms
- Scala: 27024 ms
- Scala ArrayList: 47276 ms (ArrayList instead of Scala's functional list)
- Scala ArrayList & HashSet: 76820 ms (ArrayList instead of Scala's functional list + HashSet instead of Scala's Set)
- Groovy static with indy: 59814 ms
- Groovy static without indy: 62309 ms
- Groovy dynamic without indy: 84566 ms (without warmup!)
