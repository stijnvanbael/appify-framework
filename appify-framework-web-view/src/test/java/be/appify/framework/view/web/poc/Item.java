package be.appify.framework.view.web.poc;

import be.appify.framework.annotation.Parameter;
import be.appify.framework.annotation.Context;
import be.appify.framework.annotation.Repository;

import javax.imageio.ImageIO;
import javax.validation.constraints.Min;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@Repository(type = ItemRepository.class)
public class Item {
    private final String code;
    private final String name;
    private final Image image;

    public Item(String code, String name, Image image) {
        this.code = code;
        this.name = name;
        this.image = image;
    }

    public Item(String code, String name, String imageFileLocation) {
        this(code, name, readImage(imageFileLocation));
    }

    private static Image readImage(String imageFileLocation) {
        InputStream stream = Item.class.getClassLoader().getResourceAsStream(imageFileLocation);
        if(stream == null) {
            throw new IllegalArgumentException("Image file not found: " + imageFileLocation);
        }
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image: " + imageFileLocation, e);
        }
    }

    public String name() {
        return name;
    }

    public Image image() {
        return image;
    }

    public AddToCartAction addToCart(@Context final Cart cart) {
        Cart.Order order = cart.add(this);
        return new AddToCartAction(order);
    }

    public String code() {
        return code;
    }

    public class AddToCartAction {
        private final Cart.Order order;

        public AddToCartAction(Cart.Order order) {
            this.order = order;
        }

        public void times(@Parameter(defaultValue = "1") @Min(1) int quantity) {
            order.quantity(quantity);
        }
    }
}
