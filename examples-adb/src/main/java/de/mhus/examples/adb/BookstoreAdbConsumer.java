package de.mhus.examples.adb;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import de.mhus.db.osgi.api.adb.AbstractCommonAdbConsumer;
import de.mhus.db.osgi.api.adb.CommonDbConsumer;
import de.mhus.db.osgi.api.adb.ReferenceCollector;
import de.mhus.lib.xdb.XdbService;

@Component(service = CommonDbConsumer.class,property = "commonService=common_adb",immediate = true)
public class BookstoreAdbConsumer extends AbstractCommonAdbConsumer {

    @Override
    public void registerObjectTypes(List<Class<? extends Object>> list) {
        list.add(Book.class);
        list.add(Author.class);
        list.add(Member.class);
    }

    @Override
    public void doDestroy() {
        
    }

    @Override
    public void collectReferences(Object object, ReferenceCollector collector, String reason) {
        
    }

    @Override
    public void doCleanup() {
        
    }

    @Override
    public void doPostInitialize(XdbService manager) throws Exception {
        
    }

    @Override
    protected void doInitialize() {
        
    }

}
