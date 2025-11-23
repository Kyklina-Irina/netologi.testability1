package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration; // Убедитесь, что импорт есть
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class DeliveryCardTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        Configuration.holdBrowserOpen = false;
        Configuration.pageLoadStrategy = "eager";
        open("http://localhost:9999");
    }

    @Test
    void shouldSubmitValidDeliveryForm() {
        // Генерация валидной даты: сегодня + 3 дня
        LocalDate deliveryDate = LocalDate.now().plusDays(3);
        String formattedDate = deliveryDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.forLanguageTag("ru")));

        // Ввод данных
        $("[data-test-id=city] input").setValue("Красноярск");

        // Очистка поля даты (если есть значение по умолчанию)
        $("[data-test-id=date] input").doubleClick().sendKeys("Backspace");
        $("[data-test-id=date] input").setValue(formattedDate);

        $("[data-test-id=name] input").setValue("Иванова Мария");
        $("[data-test-id=phone] input").setValue("+79991234567");
        $("[data-test-id=agreement]").click();

        $$("button").find(Condition.text("Забронировать")).click();

        // Проверка появления модального окна успеха
        $(".notification__content")
                .should(Condition.text("Встреча успешно забронирована на " + formattedDate), Duration.ofSeconds(15));
    }
}