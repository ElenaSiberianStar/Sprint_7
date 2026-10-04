//package tests.order;

import steps.Service;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.OrderModel;
import model.TrackResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.apache.http.HttpStatus.*;
@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final String[] colors;

    private Service client;
    private int createdTrack;

    public OrderCreateTest(String[] colors, String colorsDescription) {
        this.colors = colors;
        // colorsDescription нужен только для имени прогона (name-атрибут @Parameters)
    }

    @Parameterized.Parameters(name = "Цвет: {1}")
    public static Collection<Object[]> getColorOptions() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}, "BLACK"},
                {new String[]{"GREY"}, "GREY"},
                {new String[]{"BLACK", "GREY"}, "BLACK и GREY"},
                {new String[]{}, "без цвета"}
        });
    }

    @Before
    public void setUp() {
        client = new Service();
        createdTrack = 0;
    }

    @After
    public void tearDown() {
        if (createdTrack != 0) {
            client.cancelOrder(createdTrack);
        }
    }

    @Description("Варианты color: BLACK, GREY, оба цвета, без цвета — код 201 и track в теле")
    @Test
    public void createOrderWithColors() {
        OrderModel order = new OrderModel(
                "Елена", "Нежная", "Москва, ул. Отрыва, 55", "4",
                "+7 913 919 11 11", 5,
                LocalDate.now().plusDays(2).toString(),
                "Заказ с проверкой цвета", colors);

        Response response = client.createOrder(order);

        assertEquals(SC_CREATED, response.statusCode());
        response.then().body("track", notNullValue());
        createdTrack = response.as(TrackResponse.class).getTrack();
    }
}

