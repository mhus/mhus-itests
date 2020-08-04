package de.mhus.examples.vaadin;

import de.mhus.lib.annotations.vaadin.Column;

public class MhuTableItem {

	private int id;
	private String name; 
	
	public MhuTableItem(int i) {
		id = i;
		name = "Item: " + i;
	}

	@Column(order=1,title="Ident", editable=false)
	public int getId() {
		return id;
	}

	@Column(order=2,title="Name", editable=false)
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object in) {
		if (in == null || !(in instanceof MhuTableItem)) return false;
		return id == ((MhuTableItem)in).id;
	}
	
}
