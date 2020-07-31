package de.mhus.examples.jms;

import javax.jms.JMSException;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.jms.JmsChannel;
import de.mhus.osgi.api.jms.AbstractJmsDataChannel;
import de.mhus.osgi.api.jms.JmsDataChannel;
import de.mhus.osgi.api.jms.JmsDataConnection;

@Component(immediate = true, service = JmsDataChannel.class)
@JmsDataConnection("test")
public class TestJsonObjectServer extends AbstractJmsDataChannel {

    @Override
    protected JmsChannel createChannel() throws JMSException {
        return new TestJsonObjectChannel();
    }

}
