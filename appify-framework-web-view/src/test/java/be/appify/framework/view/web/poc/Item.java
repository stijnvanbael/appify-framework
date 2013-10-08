package be.appify.framework.view.web.poc;

import be.appify.framework.functional.BinaryProcedure;
import be.appify.framework.functional.RepeatableProcedure;
import be.appify.framework.functional.RunAtLeastOnceRepeatableProcedure;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Item {
    private static final BinaryProcedure<Cart, Item> ADD_TO_CART = new BinaryProcedure<Cart, Item>() {
        @Override
        public void run(Cart cart, Item item) {
            cart.add(item);
        }
    };

    private final String name;
    private final Image image;

    public Item(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public Item(String name, String imageFileLocation) {
        this(name, readImage(imageFileLocation));
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

    public RepeatableProcedure addToCart(final Cart cart) {
        return new RunAtLeastOnceRepeatableProcedure() {
            @Override
            public void run() {
                ADD_TO_CART.run(cart, Item.this);
            }
        };
    }
}
