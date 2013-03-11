havlak-jvm-languages
====================

Sources for Havlak Benchmark for several JVM Languages

For the Havlak paper see the report by Robert Hundt from Google to compare the performance of Google Go with C/C++
and other languages such as Java and Scala: https://days2011.scala-lang.org/sites/days2011/files/ws3-1-Hundt.pdf

The sources for Java and Scala can be found here: http://code.google.com/p/multi-language-bench/source/browse/trunk/src/

#####Make sure you run the Groovy code using JAVA_OPTS=-server -Xss15500k -Xmx1G. Otherwise, you will get a stackoverflow.
