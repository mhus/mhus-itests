package de.mhus.examples.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import de.mhus.lib.jms.JmsDestination;
import de.mhus.lib.jms.ServerJms;

public class TestJmsChannel extends ServerJms {

    public TestJmsChannel() {
        super(new JmsDestination("test", false));
    }

    @Override
    public Message received(Message msg) throws JMSException {
        System.out.println("GET " + msg);
        return msg;
    }

    @Override
    public void receivedOneWay(Message msg) throws JMSException {
        System.out.println("GET ONE WAY: " + msg);
    }

}
