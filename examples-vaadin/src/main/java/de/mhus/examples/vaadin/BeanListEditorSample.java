package de.mhus.examples.vaadin;

import java.util.LinkedList;
import java.util.List;

import de.mhus.lib.core.logging.Log;
import de.mhus.lib.vaadin.AbstractBeanListEditor;

public class BeanListEditorSample extends AbstractBeanListEditor<ItemSample> {

    private static final long serialVersionUID = 1L;

    public BeanListEditorSample() {
        super(ItemSample.class, null);
    }

    private Log log = Log.getLog(this);
    private LinkedList<ItemSample> list;

    @Override
    protected List<ItemSample> createBeanDataList() {
        log.i("createBeanDataList");
        
        if (list == null) {
            list = new LinkedList<>();
            for (int i = 0; i < 10; i++)
                list.add(new ItemSample(i));
        }
        return list;
    }

    @Override
    protected ItemSample createTarget() {
        return new ItemSample(list.size());
    }

    @Override
    protected void doCancel(ItemSample entry) throws Exception {
        log.i("cancel",entry);
    }

    @Override
    protected void doDelete(ItemSample entry) throws Exception {
        log.i("delete",entry);
        list.removeIf(i -> i.id.equals(entry.id));
    }

    @Override
    protected void doSave(ItemSample entry) throws Exception {
        log.i("save",entry);
        ItemSample org = getTarget(entry.id);
        if (org == null) return;
        int index = list.indexOf(org);
        list.remove(org);
        list.add(index, entry);
    }

    @Override
    protected ItemSample getEditableTarget(Object id) {
        log.i("editable",id);
        ItemSample org = getTarget(id);
        if (org == null) return null;
        return new ItemSample(org);
    }

    @Override
    protected ItemSample getTarget(Object id) {
        log.i("getTarget",id);
        for (ItemSample i : list)
            if (i.id.equals(id))
                return i;
        return null;
    }

    @Override
    protected Object getId(ItemSample entry) {
        log.i("getId",entry);
        if (entry == null) return null;
        return entry.id;
    }

}
