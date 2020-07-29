package de.mhus.examples.adb;

import java.util.UUID;

import de.mhus.lib.adb.DbComfortableObject;
import de.mhus.lib.adb.relation.RelSingle;
import de.mhus.lib.annotations.adb.DbPersistent;
import de.mhus.lib.annotations.adb.DbPrimaryKey;
import de.mhus.lib.annotations.adb.DbRelation;
import de.mhus.lib.annotations.adb.DbType.TYPE;

public class Book extends DbComfortableObject {

    @DbPrimaryKey
    private UUID id;
    @DbPersistent
    private String title;
    @DbPersistent(type = TYPE.BLOB)
    private String description;
    @DbRelation(target = Author.class)
    private RelSingle<Author> author;
    @DbRelation(target = Member.class)
    private RelSingle<Member> lender;

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RelSingle<Member> getLender() {
        return lender;
    }

}
