package be.appify.framework.view.web.poc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Cart {
    private final Map<Item, Order> items = Maps.newLinkedHashMap();

    public Order add(Item item) {
        Order order;
        if (items.containsKey(item)) {
            order = items.get(item);
            order.add(1);
        } else {
            order = new Order(item, 1);
            items.put(item, order);
        }
        return order;
    }

    public List<Order> items() {
        return Lists.newArrayList(items.values());
    }

    public static class Order {
        private Item item;
        private int quantity;

        Order(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public Item item() {
            return item;
        }

        public int quantity() {
            return quantity;
        }

        public Order add(int quantity) {
            this.quantity += quantity;
            return this;
        }

        public Order quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
    }
}
