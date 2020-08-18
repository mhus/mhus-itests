package de.mhus.examples.rest;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.core.M;
import de.mhus.rest.core.RestSocket;
import de.mhus.rest.core.annotation.RestNode;
import de.mhus.rest.core.api.Node;
import de.mhus.rest.core.api.RestApi;
import de.mhus.rest.core.api.RestNodeService;
import de.mhus.rest.core.node.VoidNode;

// websocat -vvv "ws://localhost:32797/restsocket/library?_ticket=admin:secret"
@RestNode(name = "library", parent = Node.ROOT_PARENT)
@Component(immediate = true, service = RestNodeService.class)
public class LibraryNode extends VoidNode {
    
    private boolean feedbackEnabled;
    private Timer timer;

    public LibraryNode() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (feedbackEnabled)
                        M.l(RestApi.class).forEachSocket(LibraryNode.this, s -> s.sendString("Hello " + s.getId() + " " + System.currentTimeMillis() + "\n") );
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            
        }, 1000, 1000);

    }
    
    @Override
    public boolean streamingAccept(RestSocket socket) {
        return true;
    }

    @Override
    public void streamingText(RestSocket socket, String message) {
        System.out.println("Streaming.Text:" + message);
        feedbackEnabled = M.to(message.trim(), false);
    }

}
