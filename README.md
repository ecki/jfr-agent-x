= Agent-x: Simple JFR/Premain-Agent Testground

This is a test project for a lightweight java pre-main Agent, instrumenting methods to collect call traces.
The instrumentation is done with asm, the events are reported by custom JFR events.

== Usage

```console
> mvn clean verify
> java -javaagent:.\target\agent-x-0.1.0.jar=TestApp:test .\TestApp.java
trans: test ()V
......
```

It called `TestApp#test` multiple times, and every time a (ignored) JFR event was submitted.


However, when the Agent redefines all classes (returns `classFileBuffer` instead of `null` in `transform()`) it will make JFR crash when JFR recording and Agent is used:

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

With `null` returned it works:

```console
> jfr summary .\out.jfr
 Version: 2.1
 Chunks: 1
 Start: 2025-06-05 19:27:36 (UTC)
 Duration: 6 s

 Event Type                              Count  Size (bytes)
==============================================
 see.methodcall                             50           500
 jdk.Checkpoint                             39         13231
 jdk.Metadata                                1        103537
 jdk.FileWrite                               0             0
...

> jfr print .\out.jfr
see.methodcall {
  startTime = 21:27:37.156 (2025-06-05)
  eventThread = "main" (javaThreadId = 1)
  stackTrace = [
    seediag.MethodCallEvent.emit() line: 21
    TestApp.test()
    TestApp.main(String[]) line: 6
    jdk.internal.reflect.DirectMethodHandleAccessor.invoke(Object, Object[]) line: 103
    java.lang.reflect.Method.invoke(Object, Object[]) line: 580
  ]
}
...
```

## License

All code created and copyright by Bernd Eckenfels, Germany with the Help of GitHub Copilot and employer SEEBURGER AG. Licensed unter ASL2.0.


