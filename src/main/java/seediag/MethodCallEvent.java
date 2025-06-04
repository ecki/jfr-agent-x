package seediag;

import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.Category;
import jdk.jfr.StackTrace;
import jdk.jfr.Event;


@Name("see.methodcall")
@Label("SEEBURGER Method Call Event")
//@Category("CustomEvents")
@StackTrace(true)
public class MethodCallEvent extends Event
{
    public static void emit()
    {
        MethodCallEvent event = new MethodCallEvent();
        event.begin();
        event.commit();
        System.out.println("Sent " + event.isEnabled() + " " + event.shouldCommit());
    }
}