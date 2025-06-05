// java -javaagent:target/agent-x-0.1.0.jar=TestApp:test TestApp.java
public class TestApp {
    public static void main(String[] args)
    {
        for(int i=0; i < 50; i++) {
            new TestApp().test();
            try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public void test() {
        System.out.print(".");
        //System.out.println("hello class " + this.getClass() + " " + this.getClass().getClassLoader());
        //MethodCallEvent.emit();
    }

}
