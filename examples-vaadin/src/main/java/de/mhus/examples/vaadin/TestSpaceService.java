package de.mhus.examples.vaadin;


import org.osgi.service.component.annotations.Component;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar.MenuItem;

import de.mhus.lib.vaadin.annotations.GuiSpaceDefinition;
import de.mhus.lib.vaadin.desktop.AbstractGuiSpace;
import de.mhus.lib.vaadin.desktop.GuiSpaceService;

@Component(service = GuiSpaceService.class,immediate = true)
@GuiSpaceDefinition(name = "Test",description = "Test Space", spaceClass = TestSpace.class)
public class TestSpaceService extends AbstractGuiSpace {

    @Override
    public void createMenu(AbstractComponent space, MenuItem[] menu) {
    }


}
