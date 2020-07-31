package de.mhus.examples.jms;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.jms.JmsDestination;
import de.mhus.lib.jms.RequestResult;
import de.mhus.lib.jms.ServerJsonObject;

public class TestJsonObjectChannel extends ServerJsonObject {

    public TestJsonObjectChannel() {
        super(new JmsDestination("test.obj", false));
    }

    @Override
    public RequestResult<Object> received(IProperties arg0, Object... arg1) {
        System.out.println("GET-PROP: " + arg0);
        for (int i = 0;i < arg1.length; i++)
            System.out.println("GET" + i + ": " + arg1[i]);
        return new RequestResult<Object>(arg1[0], arg0);
    }

    @Override
    public void receivedOneWay(IProperties arg0, Object... arg1) {
        System.out.println("GET ONE WAY-PROP: " + arg0);
        for (int i = 0;i < arg1.length; i++)
            System.out.println("GET ONE WAY" + i + ": " + arg1[i]);
    }

}
