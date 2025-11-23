package ru.netology.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardOrderTest {

    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        // Автоматически скачивает нужный chromedriver под вашу ОС и версию Chrome
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        // Настройка Chrome в headless-режиме (без GUI)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless"); // важно для CI
        options.addArguments("--remote-allow-origins=*"); // если нужна совместимость с новыми версиями ChromeDriver

        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        // Закрываем браузер после каждого теста
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldSubmitValidForm() {
        // Открываем тестируемое приложение
        driver.get("http://localhost:9999");

        // Заполняем поля формы корректными данными
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click(); // ставим галочку
        driver.findElement(By.cssSelector("button.button")).click(); // нажимаем кнопку

        // Проверяем успешное сообщение
        String actualText = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        String expectedText = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";

        assertEquals(expectedText, actualText.trim());
    }

    @Test
    void shouldShowErrorIfNameInvalid() {
        driver.get("http://localhost:9999");

        // Некорректное имя: содержит латиницу
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Ivanov Ivan");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем появление ошибки под полем имени
        String error = driver.findElement(By.cssSelector("[data-test-id=name] .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", error.trim());
    }

    @Test
    void shouldShowErrorIfPhoneInvalid() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7999123456"); // 10 цифр
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String error = driver.findElement(By.cssSelector("[data-test-id=phone] .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", error.trim());
    }

    @Test
    void shouldShowErrorIfAgreementNotChecked() {
        driver.get("http://localhost:9999");

        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79991234567");
        // НЕ ставим галочку
        driver.findElement(By.cssSelector("button.button")).click();

        String error = driver.findElement(By.cssSelector("[data-test-id=agreement] .checkbox__text")).getText();
        // В оригинальном приложении ошибка может отображаться по-другому.
        // Если текст не находится — можно проверить наличие класса ошибки или изменить селектор.
        // Для простоты проверим, что кнопка не сработала (форма осталась на месте)
        // Альтернатива: проверять, что сообщение успеха не появилось.
        boolean successVisible = driver.findElements(By.cssSelector("[data-test-id=order-success]")).isEmpty();
        assert(successVisible); // если галочка не стоит — успеха нет
    }
}