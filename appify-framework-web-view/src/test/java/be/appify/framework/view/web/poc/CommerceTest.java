package be.appify.framework.view.web.poc;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class CommerceTest {
    private static final Item LEGO_MINECRAFT = new Item("Lego Minecraft Microworld", "lego-minecraft.jpg");
    private static final Item BABY_MANUAL = new Item("The Baby Owner's Manual", "baby-manual.jpg");

    @Test
    public void shouldAddItemsToCart() {
        Cart cart = new Cart();
        LEGO_MINECRAFT.addToCart(cart);
        BABY_MANUAL.addToCart(cart).times(2);

        Assert.assertThat(cart.items().size(), CoreMatchers.equalTo(3));
        Assert.assertThat(cart.items().get(0), CoreMatchers.equalTo(LEGO_MINECRAFT));
        Assert.assertThat(cart.items().get(1), CoreMatchers.equalTo(BABY_MANUAL));
        Assert.assertThat(cart.items().get(2), CoreMatchers.equalTo(BABY_MANUAL));
    }
}
