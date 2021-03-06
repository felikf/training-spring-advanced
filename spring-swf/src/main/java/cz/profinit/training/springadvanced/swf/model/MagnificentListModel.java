package cz.profinit.training.springadvanced.swf.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MagnificentListModel implements Serializable {

    private static final int NOT_FOUND = -1;

    private Integer id;
    private String principal;
    private String name;
    private String description;
    private final ArrayList<ItemModel> items = new ArrayList<ItemModel>();

    public MagnificentListModel() {
    }

    public MagnificentListModel(final Integer id, final String principal, final String name, final String description) {
        this.id = id;
        this.principal = principal;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(final String principal) {
        this.principal = principal;
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

    public List<ItemModel> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(final ItemModel item) {
        items.add(item);
    }

    public void updateItem(final ItemModel item) {
        final int idx = getItemIndex(item);
        if (idx != NOT_FOUND) {
            items.set(idx, item);
        }
    }

    public void removeItem(final ItemModel item) {
        final int idx = getItemIndex(item);
        if (idx != NOT_FOUND) {
            items.remove(idx);
        }
    }

    private int getItemIndex(final ItemModel item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return NOT_FOUND;
    }

}
