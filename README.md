= Trest Project

This is a test project for a lightweight java agent instrumenting methods.
The instrumentation is done with asm, the events are reported by JFR.

== Usage

```console
> mvn clean verify
> java -javaagent:.\target\agent-x-0.1.0.jar=TestApp:test .\TestApp.java
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
Transforming : class=TestApp loader=com.sun.tools.javac.launcher.Main$MemoryClassLoader@2133814f
trans: test ()V
Sent false false
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@2133814f
Sent false false
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@2133814f
Sent false false
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@2133814f
```

It called TestApp#test 3 times, and every time a (ignored) JFR event was sent

However currently the class loading is not correctly set up, it crashes if JFR recording tries to activate:

```console
java -XX:StartFlightRecording=filename=out.jfr,settings=single.jfc TestApp.java
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@7dc3712
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@7dc3712
hello class class TestApp com.sun.tools.javac.launcher.Main$MemoryClassLoader@7dc3712
```

```console
> java -XX:StartFlightRecording=filename=out.jfr,settings=single.jfc -javaagent:.\target\agent-x-0.1.0.jar=TestApp:test .\TestApp.java
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
[0.064s][error][jfr,startup] java/lang/WeakPairMap$Pair$Weak
Error occurred during initialization of VM
Failure when starting JFR on_create_vm_3
```

The actual error is only visible in debug messages

```text
[0.062s][info ][exceptions] Exception <a 'java/lang/ClassCircularityError'{0x0000000445e3b550}: java/lang/WeakPairMap$Pair$Weak> (0x0000000445e3b550)
thrown [s\src\hotspot\share\classfile\systemDictionary.cpp, line 471]
for thread 0x000002d188252d80
[0.062s][info ][exceptions] Exception <a 'java/lang/ClassCircularityError'{0x0000000445e3b550}: java/lang/WeakPairMap$Pair$Weak>
 thrown in interpreter method <{method} {0x000002d1b8253258} 'weak' '(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/ref/ReferenceQueue;)Ljava/lang/WeakPairMap$Pair;' in 'java/lang/WeakPairMap$Pair'>
 at bci 0 for thread 0x000002d188252d80 (main)
[0.062s][debug][exceptions] Looking for catch handler for exception of type "java.lang.ClassCircularityError" in method "weak"
[0.062s][debug][exceptions] No catch handler found for exception of type "java.lang.ClassCircularityError" in method "weak"
[0.062s][info ][exceptions] Exception <a 'java/lang/ClassCircularityError'{0x0000000445e3b550}: java/lang/WeakPairMap$Pair$Weak>
 thrown in interpreter method <{method} {0x000002d1b8410e20} 'putIfAbsent' '(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;' in 'java/lang/WeakPairMap'>
 at bci 14 for thread 0x000002d188252d80 (main)
[0.062s][debug][exceptions] Looking for catch handler for exception of type "java.lang.ClassCircularityError" in method "putIfAbsent"
[0.063s][debug][exceptions] No catch handler found for exception of type "java.lang.ClassCircularityError" in method "putIfAbsent"
[0.063s][info ][exceptions] Exception <a 'java/lang/ClassCircularityError'{0x0000000445e3b550}: java/lang/WeakPairMap$Pair$Weak>
 thrown in interpreter method <{method} {0x000002d1b8022ce0} 'implAddReads' '(Ljava/lang/Module;Z)V' in 'java/lang/Module'>
 at bci 45 for thread 0x000002d188252d80 (main)
...
```

## License

All code created and copyright by Bernd Eckenfels, Germany with the Help of GitHub Copilot and employer SEEBURGER AG. Licensed unter ASL2.0.


