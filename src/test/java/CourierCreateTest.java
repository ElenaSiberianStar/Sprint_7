//package tests.courier;

import steps.Service;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CourierLoginData;
import model.CourierModel;
import model.LoginResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import data.DataGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.apache.http.HttpStatus.*;
public class CourierCreateTest {
    private Service client;
    private CourierModel createdCourier;

    @Before
    public void setUp() {
        client = new Service();
        createdCourier = null;
    }

    @After
    public void tearDown() {
        if (createdCourier != null) {
            Response login = client.loginCourier(
                    new CourierLoginData(createdCourier.getLogin(), createdCourier.getPassword()));
            int id = login.as(LoginResponse.class).getId();
            client.deleteCourier(id);
        }
    }

    @DisplayName("Создание курьера со всеми полями")
    @Description("Успешное создание курьера: код 201, в теле ok: true")
    @Test
    public void createCourierWithAllFields() {
        CourierModel courier = DataGenerator.createUniqueCourier();
        createdCourier = courier;

        Response response = client.createCourier(courier);

        assertEquals(SC_CREATED, response.statusCode());
        response.then().body("ok", equalTo(true));
    }

    @DisplayName("Создание курьера с уже существующим логином")
    @Description("Дубликат логина: код 409 и сообщение об ошибке")
    @Test
    public void createDuplicateCourierFails() {
        CourierModel courier = DataGenerator.createUniqueCourier();
        createdCourier = courier;
        assertEquals(SC_CREATED, client.createCourier(courier).statusCode());

        Response duplicate = client.createCourier(courier);
        assertEquals(SC_CONFLICT, duplicate.statusCode());
        duplicate.then().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @DisplayName("Создание курьера без логина")
    @Description("Нет обязательного поля login: код 400 и сообщение об ошибке")
    @Test
    public void createCourierWithoutLoginFails() {
        CourierModel courier = DataGenerator.createUniqueCourier();
        courier.setLogin(null);

        Response response = client.createCourier(courier);

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        response.then().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Создание курьера без пароля")
    @Description("Нет обязательного поля password: код 400 и сообщение об ошибке")
    @Test
    public void createCourierWithoutPasswordFails() {
        CourierModel courier = DataGenerator.createUniqueCourier();
        courier.setPassword(null);

        Response response = client.createCourier(courier);

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        response.then().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Создание курьера с пустым телом")
    @Description("Пустое тело запроса: код 400 и сообщение об ошибке")
    @Test
    public void createCourierWithEmptyBodyFails() {
        CourierModel courier = new CourierModel(null, null, null);

        Response response = client.createCourier(courier);

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        response.then().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

