package cz.profinit.training.springadvanced.mvc.model;

public class ItemDetailForm {

    private Integer id;
    private Integer listId;
    private String name;
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer listId) {
        this.id = listId;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
