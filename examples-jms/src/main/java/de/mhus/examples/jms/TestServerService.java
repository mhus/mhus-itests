package de.mhus.examples.jms;

import java.util.HashMap;

import javax.jms.JMSException;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.jms.JmsChannel;
import de.mhus.lib.jms.JmsDestination;
import de.mhus.lib.jms.PojoServiceDescriptor;
import de.mhus.lib.jms.ServerService;
import de.mhus.osgi.api.jms.AbstractJmsDataChannel;
import de.mhus.osgi.api.jms.JmsDataChannel;
import de.mhus.osgi.api.jms.JmsDataConnection;

@Component(immediate = true, service = JmsDataChannel.class)
@JmsDataConnection("test")
public class TestServerService extends AbstractJmsDataChannel {

    private LibraryService service = new LibraryServiceImpl();

    @Override
    protected JmsChannel createChannel() throws JMSException {
        return new ServerService<LibraryService>(
                new JmsDestination("test.service"), 
                new PojoServiceDescriptor(LibraryService.class, service) ) {
        };
    }

    static class LibraryServiceImpl implements LibraryService {

        public HashMap<String,String> lent = new HashMap<>();
        
        @Override
        public boolean checkOut(String isbn, String member) {
            System.out.println("CHECK OUT: " + isbn + " " + member);
            synchronized (lent) {
                if (lent.containsKey(isbn)) return false;
                lent.put(isbn, member);
                return true;
            }
        }
        
        @Override
        public boolean giveBack(String isbn) {
            System.out.println("GIVE BACK: " + isbn);
            synchronized (lent) {
                if (!lent.containsKey(isbn)) return false;
                lent.remove(isbn);
                return true;
            }
        }
        
        @Override
        public boolean isAvailable(String isbn) {
            return !lent.containsKey(isbn);
        }
        
    }

}
