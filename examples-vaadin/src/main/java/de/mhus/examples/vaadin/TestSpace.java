package de.mhus.examples.vaadin;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TestSpace extends TabSheet {

    private static final long serialVersionUID = 1L;

    public TestSpace() {
        initUi();
    }

    private void initUi() {
        setSizeFull();
        addTab(formEditor(), "FormEditor");
        addTab(tableTab(), "Table");
    }
    
    private Component tableTab() {
        MhuTableTab tab = new MhuTableTab();
        
        return tab;
    }

    private Component formEditor() {
        BeanListEditorSample form = new BeanListEditorSample();
        form.initUI();
        form.setSizeFull();
        
        VerticalLayout layout = new VerticalLayout();
        
        Panel panel = new Panel(form);
        panel.setSizeFull();

        layout.addComponent(panel);
        
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.setSizeFull();
        layout.setExpandRatio(panel, 1);

        return layout;
    }

}
