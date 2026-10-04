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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.apache.http.HttpStatus.*;
public class CourierLoginTest {

    private Service client;
    private CourierModel courier;
    private int courierId;

    @Before
    public void setUp() {
        client = new Service();
        courier = DataGenerator.createUniqueCourier();
        client.createCourier(courier);   // предусловие: курьер существует
        courierId = client.loginCourier(
                        new CourierLoginData(courier.getLogin(), courier.getPassword()))
                .as(LoginResponse.class).getId();
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            client.deleteCourier(courierId);
        }
    }

    @DisplayName("Успешная авторизация курьера")
    @Description("Верные логин и пароль: код 200, в теле id")
    @Test
    public void loginCourierSuccess() {
        Response response = client.loginCourier(
                new CourierLoginData(courier.getLogin(), courier.getPassword()));
        courierId = response.jsonPath().getInt("id");
        assertEquals(SC_OK, response.statusCode());
        response.then().body("id", notNullValue());   // id каждый раз новый — проверяем только наличие
    }

    @DisplayName("Авторизация с неверным паролем")
    @Description("Существующий логин, неверный пароль: код 404 и сообщение об ошибке")
    @Test
    public void loginCourierWithWrongPasswordFails() {
        Response response = client.loginCourier(
                new CourierLoginData(courier.getLogin(), courier.getPassword() + "wrong"));

        assertEquals(SC_NOT_FOUND, response.statusCode());
        response.then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Авторизация с неверным логином")
    @Description("Неверный логин, существующий пароль: код 404 и сообщение об ошибке")
    @Test
    public void loginCourierWithWrongLoginFails() {
        Response response = client.loginCourier(
                new CourierLoginData(courier.getLogin() + "wrong", courier.getPassword()));

        assertEquals(SC_NOT_FOUND, response.statusCode());
        response.then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Авторизация под несуществующим пользователем")
    @Description("Логин и пароль отсутствуют в базе: код 404 и сообщение об ошибке")
    @Test
    public void loginCourierWithNonexistentUserFails() {
        Response response = client.loginCourier(new CourierLoginData(
                "nonexistent" + System.currentTimeMillis(),
                "pass" + System.currentTimeMillis()));

        assertEquals(SC_NOT_FOUND, response.statusCode());
        response.then().body("message", equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Авторизация без логина")
    @Description("Нет обязательного поля login: код 400 и сообщение об ошибке")
    @Test
    public void loginCourierWithoutLoginFails() {
        Response response = client.loginCourier(
                new CourierLoginData(null, courier.getPassword()));

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        response.then().body("message", equalTo("Недостаточно данных для входа"));
    }


    @DisplayName("Авторизация без пароля")
    @Description("Нет обязательного поля password: код 400 и сообщение об ошибке")
    @Test
    public void loginCourierWithoutPasswordFails() {
        Response response = client.loginCourier(
                new CourierLoginData(courier.getLogin(), null));

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        response.then().body("message", equalTo("Недостаточно данных для входа"));
    }
}
// Не проходит проверку.
// Expected :400
//Actual   :504

