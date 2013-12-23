package be.appify.framework.view.web.poc;

import be.appify.framework.view.web.*;
import com.google.common.collect.Sets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceTest {
    private static final Item LEGO_MINECRAFT = new Item("lego-minecraft", "Lego Minecraft Microworld", "lego-minecraft.jpg");
    private static final Item BABY_MANUAL = new Item("baby-manual", "The Baby Owner's Manual", "baby-manual.jpg");
    private RequestDispatcher requestDispatcher;

    @Mock
    private ContextProvider contextProvider;

    private ItemRepository itemRepository;

    @Before
    public void before() {
        requestDispatcher = new DefaultRequestDispatcher("be.appify.framework.view.web", contextProvider);
        itemRepository = new ItemRepository();
        itemRepository.add(LEGO_MINECRAFT);
        itemRepository.add(BABY_MANUAL);

        when(contextProvider.findAll(ItemRepository.class)).thenReturn(Sets.newHashSet(itemRepository));
    }

    @Test
    public void shouldAddItemsToCart() throws URISyntaxException {
        Cart cart = new Cart();
        when(contextProvider.findAll(Cart.class)).thenReturn(Sets.newHashSet(cart));

        requestDispatcher.dispatch(
                SimpleRequest.newBuilder()
                        .uri(new URI("/poc/Item/lego-minecraft/addToCart"))
                        .method(RequestMethod.POST)
                        .build());
        requestDispatcher.dispatch(
                SimpleRequest.newBuilder()
                        .uri(new URI("/poc/Item/baby-manual/addToCart"))
                        .method(RequestMethod.POST)
                        .parameter("times", "2")
                        .build());

        Assert.assertThat(cart.items().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(cart.items().get(0).item(), CoreMatchers.equalTo(LEGO_MINECRAFT));
        Assert.assertThat(cart.items().get(0).quantity(), CoreMatchers.equalTo(1));
        Assert.assertThat(cart.items().get(1).item(), CoreMatchers.equalTo(BABY_MANUAL));
        Assert.assertThat(cart.items().get(1).quantity(), CoreMatchers.equalTo(2));
    }
}
