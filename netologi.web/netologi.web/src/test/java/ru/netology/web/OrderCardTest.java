package ru.netology.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderCardTest {

    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // <-- используй НОВЫЙ headless-режим
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*"); // иногда нужно для localhost
        options.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void shouldSubmitValidForm() {
        // Заполняем поле "Фамилия и имя"
        WebElement nameField = driver.findElement(By.cssSelector("[name='name']"));
        nameField.sendKeys("Иван Петров");

        // Заполняем поле "Мобильный телефон"
        WebElement phoneField = driver.findElement(By.cssSelector("[name='phone']"));
        phoneField.sendKeys("+79991234567");

        // Ставим галочку "Я соглашаюсь..."
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id='agreement']"));
        agreementCheckbox.click();

        // Нажимаем кнопку "Продолжить"
        WebElement submitButton = driver.findElement(By.cssSelector(".button"));
        submitButton.click();

        // Проверяем, что появилось сообщение об успешной отправке
        WebElement successMessage = driver.findElement(By.cssSelector("[data-test-id='order-success']"));
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", successMessage.getText());
    }
}