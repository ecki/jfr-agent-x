package seediag;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        if (agentArgs == null || agentArgs.isEmpty()) {
            System.err.println("agent-x: Skipping instrumentation");
            return;
        }
        String[] args = agentArgs.split(":");
        if (args.length != 2) {
            System.err.println("agent-x: Expected format: <className>:<methodName>");
            return;
        }
        if (!inst.isRetransformClassesSupported()) {
            System.err.println("agent-x: Cant retransform, jar manifest?");
            return;
        }
        String className = args[0].replace('.', '/'); // internal name
        String methodName = args[1];

        try {
            inst.appendToSystemClassLoaderSearch(new JarFile("target\\agent-x-0.1.0.jar")); // todo: from codebase
            //inst.appendToBootstrapClassLoaderSearch(new JarFile("target\\agent-x-0.1.0.jar")); // todo: from codebase
        } catch (Exception e) {
            System.err.println("agent-x: Failed to append jar to classloader: " + e);
            return;
        }
        inst.addTransformer(new MethodCallTransformer(className, methodName), true);
    }
}
