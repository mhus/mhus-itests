package de.mhus.examples.jms;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MStopWatch;
import de.mhus.lib.core.util.Lorem;
import de.mhus.lib.errors.MException;
import de.mhus.lib.jms.ClientJms;
import de.mhus.lib.jms.ClientJson;
import de.mhus.lib.jms.ClientJsonObject;
import de.mhus.lib.jms.ClientService;
import de.mhus.lib.jms.JmsConnection;
import de.mhus.lib.jms.JmsDestination;
import de.mhus.lib.jms.PojoServiceDescriptor;
import de.mhus.lib.jms.RequestResult;
import de.mhus.osgi.api.jms.JmsUtil;
import de.mhus.osgi.api.karaf.AbstractCmd;

@Command(scope = "itest", name = "jms", description = "Jms itest tool")
@Service
public class CmdTestJms extends AbstractCmd {

    @Argument(
            index = 0,
            name = "cmd",
            required = true,
            description =
                    "Command to execute"
            ,
            multiValued = false)
    String cmd;

    @Argument(
            index = 1,
            name = "paramteters",
            required = false,
            description = "Parameters",
            multiValued = true)
    String[] parameters;
    
    @Override
    public Object execute2() throws Exception {
        
        if (cmd.equals("client"))
            return onClient();
        if (cmd.equals("json"))
            return onJson();
        if (cmd.equals("book"))
            return onBook();
        if (cmd.equals("service.checkout"))
            return onServiceCheckOut();
        if (cmd.equals("stress"))
            return onStress();
        return null;
    }

    @SuppressWarnings("resource")
    private Object onStress() throws MException, JMSException {
        String load = Lorem.createWithSize(M.to(parameters[2], 1024));
        int rounds = M.to(parameters[3], 1000);
        System.out.println(parameters[4] + ": Load: " + load.length());
        System.out.println(parameters[4] + ": Rounds: " + rounds);
        JmsConnection con = JmsUtil.getConnection(parameters[0]);
        if (con == null) throw new MException("connection not found",parameters[0]);
        
        try (ClientJms client = new ClientJms(new JmsDestination(parameters[1]).setConnection(con))) {
            MStopWatch watch = new MStopWatch("jms").start();
            for (int i = 0; i < rounds; i++) {
                if (i % 1000 == 0)
                    System.out.println(parameters[4] + ": " + i + " / " + rounds);
                TextMessage msg = client.createTextMessage(load);
                msg.setBooleanProperty("quiet", true);
                Message res = client.sendJms(msg);
                if (!load.equals(((TextMessage)res).getText()))
                    throw new MException("result is invalid",i);
            }
            watch.stop().print();
        }
        return null;
    }
    
    @SuppressWarnings("resource")
    private Object onServiceCheckOut() throws MException {
        JmsConnection con = JmsUtil.getConnection(parameters[0]);
        if (con == null) throw new MException("connection not found",parameters[0]);
        try (ClientService<LibraryService> client = new ClientService<LibraryService>(
                new JmsDestination(parameters[1]).setConnection(con), 
                new PojoServiceDescriptor(LibraryService.class))
            ) {
            boolean ret = client.getObject().checkOut(parameters[2], parameters[3]);
            return ret;
        }
    }

    @SuppressWarnings("resource")
    private Object onBook() throws MException, IllegalAccessException, JMSException, IOException {
        JmsConnection con = JmsUtil.getConnection(parameters[0]);
        if (con == null) throw new MException("connection not found",parameters[0]);
        try (ClientJsonObject client = new ClientJsonObject(new JmsDestination(parameters[1]).setConnection(con))) {

            MProperties arg0 = new MProperties();
            arg0.setString("name", "Sniffy");
            Object[] arg1 = new Object[] {new Book()};
            ((Book)arg1[0]).setName(parameters[2]);
            RequestResult<Object> res = client.sendObject(arg0, arg1);
            if (res == null) {
                System.out.println("RES IS NULL");
            } else {
                System.out.println("RES-PROP: "+res.getProperties());
                System.out.println("RES: " + res.getResult());
            }
            return res;
        }
    }

    @SuppressWarnings("resource")
    private Object onJson() throws IOException, JMSException, MException {
        JmsConnection con = JmsUtil.getConnection(parameters[0]);
        if (con == null) throw new MException("connection not found",parameters[0]);
        try (ClientJson client = new ClientJson(new JmsDestination(parameters[1]).setConnection(con))) {
            
            MProperties arg0 = new MProperties();
            arg0.setString("name", "Sniffy");
            ObjectNode arg1 = MJson.createObjectNode();
            arg1.put("value", parameters[2]);
            RequestResult<JsonNode> res = client.sendJson(arg0, arg1);
            System.out.println("RES-PROP: "+res.getProperties());
            System.out.println("RES: " + MJson.toString(res.getResult()));
            return res;
        }
    }

    @SuppressWarnings("resource")
    private Object onClient() throws JMSException, MException {
        JmsConnection con = JmsUtil.getConnection(parameters[0]);
        if (con == null) throw new MException("connection not found",parameters[0]);
        try (ClientJms client = new ClientJms(new JmsDestination(parameters[1]).setConnection(con))) {
            TextMessage msg = client.createTextMessage(parameters[2]);
            Message res = client.sendJms(msg);
            return res;
        }
    }

}
