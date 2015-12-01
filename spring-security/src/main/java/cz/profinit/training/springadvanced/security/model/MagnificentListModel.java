package cz.profinit.training.springadvanced.security.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MagnificentListModel implements Serializable {

    private static final int NOT_FOUND = -1;
    private final String principal;
    private Integer id;
    private String name;
    private String description;
    private ArrayList<ItemModel> items = new ArrayList<ItemModel>();

    public MagnificentListModel(String principal) {
        this.principal = principal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
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

    public List<ItemModel> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(ItemModel item) {
        items.add(item);
    }

    public void updateItem(ItemModel item) {
        int idx = getItemIndex(item);
        if (idx != NOT_FOUND) {
            items.set(idx, item);
        }
    }

    public void removeItem(ItemModel item) {
        int idx = getItemIndex(item);
        if (idx != NOT_FOUND) {
            items.remove(idx);
        }
    }

    private int getItemIndex(ItemModel item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return NOT_FOUND;
    }

}
