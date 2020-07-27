package de.mhus.examples.rest;

import org.osgi.service.component.annotations.Component;

import de.mhus.rest.core.annotation.RestNode;
import de.mhus.rest.core.api.Node;
import de.mhus.rest.core.api.RestNodeService;
import de.mhus.rest.core.node.VoidNode;


@RestNode(name = "library", parent = Node.ROOT_PARENT)
@Component(immediate = true, service = RestNodeService.class)
public class LibraryNode extends VoidNode {

}
