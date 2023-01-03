package fr.alkanife.alkabot.music.shortcut;

import java.util.Date;

public class Shortcut {

    private String name, query, creator_id;
    private Date creation_date;

    public Shortcut() {}

    public Shortcut(String name, String query, String creator_id, Date creation_date) {
        this.name = name;
        this.query = query;
        this.creator_id = creator_id;
        this.creation_date = creation_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }
}
