package de.mhus.examples.jms;

import de.mhus.lib.annotations.generic.Public;

public interface LibraryService {

    @Public
    boolean checkOut(String isbn, String member);

    @Public
    boolean giveBack(String isbn);

    @Public
    boolean isAvailable(String isbn);
    
}
