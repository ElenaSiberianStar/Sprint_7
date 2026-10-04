package steps;

import data.ConstantsAPI;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.CourierLoginData;
import model.CourierModel;
import model.OrderModel;
import model.OrderCancel;

import static io.restassured.RestAssured.given;

public class Service {

    @Step("Создание курьера")
    public Response createCourier(CourierModel courier) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post(ConstantsAPI.BASE_URL + ConstantsAPI.COURIER_CREATE_PATH);
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierLoginData credentials) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(credentials)
                .when()
                .post(ConstantsAPI.BASE_URL + ConstantsAPI.COURIER_LOGIN_PATH);
    }

    @Step("Создание заказа")
    public Response createOrder(OrderModel order) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ConstantsAPI.BASE_URL + ConstantsAPI.ORDERS_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .log().all()
                .when()
                .get(ConstantsAPI.BASE_URL + ConstantsAPI.ORDERS_PATH);
    }

    @Step("Удаление курьера")
    public Response deleteCourier(int id) {
        return given()
                .log().all()
                .pathParam("id", id)
                .when()
                .delete(ConstantsAPI.BASE_URL + ConstantsAPI.COURIER_DELETE_PATH);
    }

    @Step("Отмена заказа")
    public Response cancelOrder(int track) {
        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(new OrderCancel(track))
                .when()
                .put(ConstantsAPI.BASE_URL + ConstantsAPI.ORDERS_CANCEL_PATH);
    }
}

