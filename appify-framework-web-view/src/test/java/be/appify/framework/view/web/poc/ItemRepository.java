package be.appify.framework.view.web.poc;

import com.google.common.collect.Maps;

import java.util.Map;

public class ItemRepository {
    private Map<String, Item> items = Maps.newHashMap();

    public ItemRepository add(Item item) {
        items.put(item.code(), item);
        return this;
    }

    public Item find(String code) {
        return items.get(code);
    }
}
