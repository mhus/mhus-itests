package de.mhus.examples.rest;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mhus.rest.core.CallContext;
import de.mhus.rest.core.annotation.RestNode;
import de.mhus.rest.core.api.Node;
import de.mhus.rest.core.api.RestNodeService;
import de.mhus.rest.core.node.JsonRestNode;
import de.mhus.rest.core.result.JsonResult;

@RestNode(name = "info", parent = Node.PUBLIC_PARENT)
@Component(immediate = true, service = RestNodeService.class)
public class PublicInfo extends JsonRestNode {

    @Override
    protected void doRead(JsonResult result, CallContext context) {
        ObjectNode out = result.createObjectNode();
        out.put("ping", "pong");
    }

}
