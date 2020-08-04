package de.mhus.examples.vaadin;

import java.util.Comparator;
import java.util.LinkedList;

import com.vaadin.event.Action;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table.TableDragMode;

import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;
import de.mhus.lib.vaadin.ExpandingTable;
import de.mhus.lib.vaadin.ExpandingTable.RenderListener;
import de.mhus.lib.vaadin.MhuTable;

@SuppressWarnings("deprecation")
public class MhuTableTab extends VerticalLayout implements Component {

	private static final long serialVersionUID = 1L;

    private MhuTable table;
	private String sortByDefault = "id";
	private boolean sortAscDefault = true;
    private MhuTableItemContainer data;

	private int lastExtend;
	private int page;

    final Action actionDetails = new Action("Details");
    final Action actionEven = new Action("Even");
    final Action actionOdd  = new Action("Odd");

	public MhuTableTab() {
		data = getItems(0);
        table = new MhuTable();
        table.setSizeFull();
        table.addStyleName("borderless");
        table.setSelectable(true);
        table.setTableEditable(false);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSortContainerPropertyId(sortByDefault);
        table.setSortAscending(sortAscDefault);
        if (data != null) {
        	data.removeAllContainerFilters();
        	table.setContainerDataSource(data);
        	MNls nls = new MNlsFactory().create(this);
        	data.configureTableByAnnotations(table, null, nls);
        }
        sortTable();
        
        table.addItemClickListener(new ItemClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					Notification.show("DoubleClick: " + ((MhuTableItem)event.getItemId()).getName());
				}
			}
		});
        
        table.addActionHandler(new Action.Handler() {
			private static final long serialVersionUID = 1L;
			@Override
            public Action[] getActions(final Object target, final Object sender) {
				if (target != null) {
					return new Action[] {actionDetails};
				}
				return new Action[0];
			}
            @Override
            public void handleAction(final Action action, final Object sender,
                    final Object target) {
            	
            }
        });
        
        table.setDragMode(TableDragMode.NONE);
        table.setMultiSelect(false);
        table.renderEventHandler().register(new RenderListener() {
			@Override
			public void onRender(ExpandingTable mhuTable, int first, int last) {
				doExtendTable(mhuTable, first, last);
			}
		});

        addComponent(table);
        setExpandRatio(table, 1);

        table.setImmediate(true);

	}
	
	protected void doExtendTable(ExpandingTable mhuTable, int first, int last) {
		int size = mhuTable.getItemIds().size() - 1;
		if (lastExtend < last && last == size) {
			lastExtend = last;
			doRefresh(++page);
		}
	}

	protected void doRefresh(int page_) {
		
		
		MhuTableItemContainer updatedData = getItems(page_);
		if (updatedData != null) {
			if (data == null)
				data = updatedData;
			
			data.mergeAll(updatedData.getItemIds(), page_ == 0 ? true : false, new Comparator<MhuTableItem>() {
				@Override
				public int compare(MhuTableItem o1, MhuTableItem o2) {
					return MSystem.equals(o1, o2) ? 0 : 1;
				}
			});
		}
		else {
			Notification.show("Daten konnten nicht abgefragt werden",Notification.Type.WARNING_MESSAGE);
			if (data == null)
				data = new MhuTableItemContainer();
			return;
		}
//		sortTable();
		if (page_ == 0) {
			lastExtend = 0;
			page = 0;
			table.setCurrentPageFirstItemIndex(0);
			Notification.show("Liste aktualisiert",Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private MhuTableItemContainer getItems(int page_) {
		System.out.println("Request Items: " + page_);
		MhuTableItemContainer out = new MhuTableItemContainer();
		
		LinkedList<MhuTableItem> list = new LinkedList<>();
		for (int i = 0; i < 200; i++)
			list.add(new MhuTableItem(page_*200+i));
		out.addAll(list);

		return out;
	}

	private void sortTable() {
        table.sort(new Object[] { table.getSortContainerPropertyId() }, new boolean[] { table.isSortAscending() });
    }

}
