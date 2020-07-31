package de.mhus.examples.jms;

import com.fasterxml.jackson.databind.JsonNode;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.jms.JmsDestination;
import de.mhus.lib.jms.RequestResult;
import de.mhus.lib.jms.ServerJson;

public class TestJsonChannel extends ServerJson {

    public TestJsonChannel() {
        super(new JmsDestination("test.json", false));
    }

    @Override
    public RequestResult<JsonNode> received(IProperties arg0, JsonNode arg1) {
        System.out.println("GET: " + arg1);
        System.out.println("GET-PROP: " + arg0);
        return new RequestResult<JsonNode>(arg1, arg0);
    }

    @Override
    public void receivedOneWay(IProperties arg0, JsonNode arg1) {
        System.out.println("GET ONE WAY: " + arg1);
        System.out.println(arg0);
    }

}
