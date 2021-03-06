package cz.profinit.training.springadvanced.swf.model;

import java.io.Serializable;

public class ItemModel implements Serializable {

    private Integer id;
    private String name;
    private String description;
    private String principal;

    public ItemModel() {
    }

    public ItemModel(final Integer id, final String name, final String description, final String principal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.principal = principal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(final String principal) {
        this.principal = principal;
    }
}
