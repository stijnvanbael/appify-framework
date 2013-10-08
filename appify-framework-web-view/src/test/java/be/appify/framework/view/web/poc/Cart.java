package be.appify.framework.view.web.poc;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<Item> items = Lists.newArrayList();

    public void add(Item item) {
        items.add(item);
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }
}
