package cz.profinit.training.springadvanced.security.service.impl;

import cz.profinit.training.springadvanced.domain.Item;
import cz.profinit.training.springadvanced.domain.MagnificentList;
import cz.profinit.training.springadvanced.security.model.ItemModel;
import cz.profinit.training.springadvanced.security.model.MagnificentListModel;
import cz.profinit.training.springadvanced.security.service.MagnificentListModelService;
import cz.profinit.training.springadvanced.service.MagnificentListService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO Add security annotations
public class MagnificentListModelServiceImpl implements MagnificentListModelService {

    private final MagnificentListService magnificentListService;

    public MagnificentListModelServiceImpl(final MagnificentListService magnificentListService) {
        this.magnificentListService = magnificentListService;
    }

    @Override
    @Transactional(readOnly = true)
    // TODO Allowed for both roles
    public List<MagnificentListModel> getLists() {
        return magnificentListService.getLists().stream()
                .map(ml -> domainToModel(ml, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    // TODO Allowed for both roles
    public MagnificentListModel getModel(final int listId) {
        return domainToModel(magnificentListService.getList(listId), magnificentListService.getListItems(listId));
    }

    @Override
    @Transactional
    // TODO Phase1: Allowed for ADMIN
    // TODO Phase2: Allowed for ADMIN and the user who created the list
    public void saveModel(final MagnificentListModel model) {
        final Map<Integer, Item> dbItemsMap = getDbItemsMap(model);

        final MagnificentList domain = new MagnificentList(model.getId(), model.getName(), model.getDescription(), model.getPrincipal());

        magnificentListService.saveList(domain);

        for (final ItemModel m : model.getItems()) {
            dbItemsMap.remove(m.getId());
            magnificentListService.saveItem(new Item(m.getId(), model.getId(), m.getName(), m.getDescription(), m.getPrincipal()));
        }

        for (final Item removedItem : dbItemsMap.values()) {
            magnificentListService.deleteItem(removedItem);
        }
    }

    private Map<Integer, Item> getDbItemsMap(final MagnificentListModel model) {
        final Map<Integer, Item> dbItemsMap = new HashMap<>();
        if (model.getId() != null) {
            for (final Item i : magnificentListService.getListItems(model.getId())) {
                dbItemsMap.put(i.getId(), i);
            }
        }
        return dbItemsMap;
    }

    private MagnificentListModel domainToModel(final MagnificentList m, final List<Item> items) {
        final MagnificentListModel ret = new MagnificentListModel(m.getPrincipal());
        ret.setId(m.getId());
        ret.setDescription(m.getDescription());
        ret.setName(m.getName());

        for (final Item item : items) {
            ret.addItem(domainToItem(item));
        }

        return ret;
    }

    private ItemModel domainToItem(final Item m) {
        final ItemModel ret = new ItemModel();
        ret.setId(m.getId());
        ret.setName(m.getName());
        ret.setDescription(m.getDescription());
        ret.setPrincipal(m.getPrincipal());
        return ret;
    }
}
