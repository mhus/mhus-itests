package de.mhus.examples.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.core.pojo.PojoModelFactory;
import de.mhus.lib.errors.MException;
import de.mhus.rest.core.CallContext;
import de.mhus.rest.core.annotation.RestNode;
import de.mhus.rest.core.api.RestNodeService;
import de.mhus.rest.core.node.ObjectListNode;

@RestNode(name = "book", parentNode = LibraryNode.class)
@Component(immediate = true, service = RestNodeService.class)
public class BookNode extends ObjectListNode<Book,Book> {

    private HashMap<String, Book> books = new HashMap<>();

    public BookNode() {
        {
            Book book = new Book();
            book.setIsbn("978-0345391803");
            book.setTitle("The Hitchhiker`s Guide to the Galaxy");
            book.setDescription("Seconds before Earth is demolished to make way for a galactic freeway, "
                    + "Arthur Dent is plucked off the planet by his friend Ford Prefect, a researcher "
                    + "for the revised edition of The Hitchhiker’s Guide to the Galaxy who, for the last "
                    + "fifteen years, has been posing as an out-of-work actor.");
            book.setAuthor("Douglas Adams");
            book.setCreatedYear(2005);
            books.put(book.getIsbn(), book);
        }
    }
    @Override
    protected List<Book> getObjectList(CallContext callContext) throws MException {
        return new ArrayList<Book>(books.values());
    }

    @Override
    protected Book getObjectForId(CallContext context, String id) throws Exception {
        return books.get(id);
    }

    @Override
    protected Book doCreateObj(CallContext context) throws IOException {

        Book book = new Book();
        PojoModelFactory schema = getPojoModelFactory();
        MPojo.propertiesToPojo(context.getParameters(), book, schema, null, true);

        books.put(book.getIsbn(), book);

        return book;
    }

    @Override
    protected void doUpdateObj(Book book, CallContext context) throws IOException {
        PojoModelFactory schema = getPojoModelFactory();
        MPojo.propertiesToPojo(context.getParameters(), book, schema);
    }

    @Override
    protected void doDeleteObj(Book book, CallContext context) {
        books.remove(book.getIsbn());
    }

}
