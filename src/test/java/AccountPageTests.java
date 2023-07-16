import jdk.jfr.Description;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class AccountPageTests {
    private WebDriver driver;
    private WebDriverWait wait;
    JavascriptExecutor js;
    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver","drivers/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        js = (JavascriptExecutor) driver;
    }
    @After
    public void tearDown() {
        driver.quit();
    }
    String mainPageUrl = "http://intershop5.skillbox.ru/";
    @Test
    @Description("Проверка регистрации пользователя")
    public void registrationTest1 () {
        driver.get(mainPageUrl);
        String name = "AnotherTester11";
        String email = "tester3377@mail.com";
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By registrationButton = By.xpath("//button[@class = 'custom-register-button']");
        By userNameInputReg = By.xpath("//input[@id = 'reg_username']");
        By emailInputReg = By.xpath("//input[@id = 'reg_email']");
        By passwordInputReg = By.xpath("//input[@id = 'reg_password']");
        By submitRegistrationButton = By.xpath("//button[@name = 'register']");
        By regIsCompletedText = By.xpath("//div[@class = 'content-page']//div");
        By helloUserText = By.xpath("//div[@class = 'welcome-user']");
        driver.findElement(signInButton).click();
        driver.findElement(registrationButton).click();
        driver.findElement(userNameInputReg).sendKeys(name);
        driver.findElement(emailInputReg).sendKeys(email);
        driver.findElement(passwordInputReg).sendKeys("selenium2077");
        driver.findElement(submitRegistrationButton).click();
        String regText = driver.findElement(regIsCompletedText).getText();
        String helloRegNameTex = driver.findElement(helloUserText).getText().replaceAll("[|!]","").trim();
        Assert.assertEquals("Ошибка при корректной регистрации","Регистрация завершена",regText);
        Assert.assertEquals("Ошибка в имени в верхнем левом углу","Привет " + name,helloRegNameTex);
    }
    @Test
    @Description("Негативная проверка полей регистрации пользователя 1")
    public void negativeRegistrationTest1 () {
        driver.get("http://intershop5.skillbox.ru/register/");
        By submitRegistrationButton = By.xpath("//button[@name = 'register']");
        By errorAlert = By.xpath("//ul[@class = 'woocommerce-error']//li");
        String emailErrorAlertText = "Error: Пожалуйста, введите корректный email.";
        driver.findElement(submitRegistrationButton).click();
        Assert.assertTrue("Алерт при регистрации с пустыми полями не появился",
                driver.findElement(errorAlert).isDisplayed());
        String actualAlertInIncorrectEmail = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                emailErrorAlertText, actualAlertInIncorrectEmail);
    }
    @Test
    @Description("Негативная проверка полей регистрации пользователя 2")
    public void negativeRegistrationTest2 () {
        driver.get("http://intershop5.skillbox.ru/register/");
        By userNameInputReg = By.xpath("//input[@id = 'reg_username']");
        By emailInputReg = By.xpath("//input[@id = 'reg_email']");
        By submitRegistrationButton = By.xpath("//button[@name = 'register']");
        By errorAlert = By.xpath("//ul[@class = 'woocommerce-error']//li");
        String nameErrorAlertText = "Error: Пожалуйста введите корректное имя пользователя.";
        String emailErrorAlertText = "Error: Пожалуйста, введите корректный email.";
        driver.findElement(emailInputReg).sendKeys("mail@mail");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectEmail2 = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                emailErrorAlertText,actualAlertInIncorrectEmail2);
        driver.findElement(emailInputReg).clear();
        driver.findElement(emailInputReg).sendKeys("mail@yahhooo.com");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectName = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                nameErrorAlertText,actualAlertInIncorrectName);
        driver.findElement(userNameInputReg).sendKeys("+_)(*?:%;№");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectName2 = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                nameErrorAlertText,actualAlertInIncorrectName2);
        driver.findElement(userNameInputReg).clear();
        driver.findElement(userNameInputReg).sendKeys("  ");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectName3 = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                nameErrorAlertText,actualAlertInIncorrectName3);
        driver.findElement(userNameInputReg).clear();
    }
    @Test
    @Description("Негативная проверка полей регистрации пользователя 3")
    public void negativeRegistrationTest3 () {
        driver.get("http://intershop5.skillbox.ru/register/");
        By userNameInputReg = By.xpath("//input[@id = 'reg_username']");
        By emailInputReg = By.xpath("//input[@id = 'reg_email']");
        By passwordInputReg = By.xpath("//input[@id = 'reg_password']");
        By submitRegistrationButton = By.xpath("//button[@name = 'register']");
        By errorAlert = By.xpath("//ul[@class = 'woocommerce-error']//li");
        By regIsCompletedText = By.xpath("//div[@class = 'content-page']//div");
        String passWordErrorAlertText = "Error: Введите пароль для регистрации.";
        String toLongEmailError = "Error: Максимальное допустимое количество символов: 20";
        driver.findElement(emailInputReg).sendKeys("mail@yagle.com");
        driver.findElement(userNameInputReg).sendKeys("ordinaryNameTest36");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectPass = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                passWordErrorAlertText,actualAlertInIncorrectPass);
        driver.findElement(emailInputReg).clear();
        driver.findElement(emailInputReg).sendKeys("testers@yahhhhooo.com");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectEmail3 = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                toLongEmailError,actualAlertInIncorrectEmail3);
        driver.findElement(emailInputReg).clear();
        driver.findElement(emailInputReg).sendKeys("tet@yaaaaandddeex.ru");
        driver.findElement(submitRegistrationButton).click();
        String actualAlertInIncorrectPass2 = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Ошибка в алерте с ошибкой при регистрации",
                passWordErrorAlertText,actualAlertInIncorrectPass2);
        driver.findElement(passwordInputReg).sendKeys("123");
        driver.findElement(submitRegistrationButton).click();
        String regText = driver.findElement(regIsCompletedText).getText();
        Assert.assertEquals("Ошибка при корректной регистрации","Регистрация завершена",regText);
    }
    @Test
    @Description("Тест на авторизацию через главный экран через логин + пароль")
    public void registrationTest3 () {
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        By helloFiled = By.xpath("//div[@class = 'woocommerce-MyAccount-content']//p[contains(text(), 'Привет ')]");
        driver.get(mainPageUrl);
        driver.findElement(signInButton).click();
        driver.findElement(userNameLogInField).sendKeys("selenium2023");
        driver.findElement(passwordLogInField).sendKeys("selenium2023");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Поле с приветствием не появилось",driver.findElement(helloFiled).isDisplayed());
        String actualHelloText = driver.findElement(helloFiled).getText();
        Assert.assertEquals("В поле с приветствием ошибка","Привет selenium2023 (Выйти)",actualHelloText);
    }
    @Test
    @Description("Тест на авторизацию через главный экран через эл.почту + пароль")
    public void registrationTest4 () {
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        By helloFiled = By.xpath("//div[@class = 'woocommerce-MyAccount-content']//p[contains(text(), 'Привет ')]");
        driver.get(mainPageUrl);
        driver.findElement(signInButton).click();
        driver.findElement(userNameLogInField).sendKeys("selenium@test.com");
        driver.findElement(passwordLogInField).sendKeys("selenium2023");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Поле с приветствием не появилось",driver.findElement(helloFiled).isDisplayed());
        String actualHelloText = driver.findElement(helloFiled).getText();
        Assert.assertEquals("В поле с приветствием ошибка","Привет selenium2023 (Выйти)",actualHelloText);
        driver.findElement(By.xpath("//a[@class='logout']")).click();
    }
    @Test
    @Description("Тест на авторизацию через добавление товара в корзину")
    public void registrationTest5 () {
        driver.get("http://intershop5.skillbox.ru/product/ipad-2020-32gb-wi-fi/");
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By goToCartButton = By.xpath("//div[@class='woocommerce-notices-wrapper']//a");
        By checkOutButton = By.xpath("//a[@class='checkout-button button alt wc-forward']");
        By signInButton = By.xpath("//a[@class='showlogin']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        By logInButton = By.xpath("//button[@type='submit'][@name = 'login']");
        By currentTitle = By.xpath("//span[@class='current']");
        By orderDetailsText = By.xpath("//div[@class='woocommerce-billing-fields']//h3");
        By additionalDetailsText = By.xpath("//div[@class='woocommerce-additional-fields']//h3");
        By actualProductNameInOrder = By.xpath("//td[@class='product-name']");
        driver.findElement(addToCartButton).click();
        driver.findElement(goToCartButton).click();
        driver.findElement(checkOutButton).click();
        driver.findElement(signInButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(userNameLogInField));
        driver.findElement(userNameLogInField).sendKeys("selenium2023");
        driver.findElement(passwordLogInField).sendKeys("selenium2023");
        driver.findElement(logInButton).click();
        String actualTitle = driver.findElement(currentTitle).getText().toLowerCase();
        Assert.assertEquals("При авторизации через корзину переход к странице оформления не произведён"
                ,"оформление заказа",actualTitle);
        Assert.assertTrue("Столбец 'Детали заказа' не отображается",
                driver.findElement(orderDetailsText).isDisplayed());
        Assert.assertTrue("Столбец 'Дополнительная информация' не отображается",
                driver.findElement(additionalDetailsText).isDisplayed());
        String orderName = driver.findElement(actualProductNameInOrder).getText();
        Assert.assertEquals("В заказе не тот товар","iPad 2020 32gb wi-fi  × 1",orderName);
    }
    @Test
    @Description("Негативный тест на авторизацию без указания пароля и логина")
    public void authorizationTest () {
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By errorTextLocator = By.xpath("//ul[@class='woocommerce-error']//li");
        driver.get(mainPageUrl);
        driver.findElement(signInButton).click();
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания логина и пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText1 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку", "Error: Имя пользователя обязательно.", actualErrorText1);
    }
    @Test
    @Description("Негативный тест на авторизацию. Тестирование поля 'Логин'")
    public void authorizationTest2 () {
        driver.get(mainPageUrl);
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        By errorTextLocator = By.xpath("//ul[@class='woocommerce-error']//li");
        driver.findElement(signInButton).click();
        driver.findElement(userNameLogInField).sendKeys("Имя");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText2 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку", "Пароль обязателен.", actualErrorText2);
        driver.findElement(passwordLogInField).sendKeys("1");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText3 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку", "Неизвестное имя пользователя. Попробуйте еще раз или укажите адрес почты.", actualErrorText3);
    }
    @Test
    @Description("Негативный тест на авторизацию. Тестирование поля 'Пароль'")
    public void authorizationTest3 () {
        driver.get(mainPageUrl);
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        By errorTextLocator = By.xpath("//ul[@class='woocommerce-error']//li");
        driver.findElement(signInButton).click();
        driver.findElement(userNameLogInField).clear();
        driver.findElement(userNameLogInField).sendKeys("m22222ail@mail.com");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText4 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку","Пароль обязателен.",actualErrorText4);
        driver.findElement(userNameLogInField).clear();
        driver.findElement(userNameLogInField).sendKeys("selenium@test.com");
        driver.findElement(passwordLogInField).sendKeys("1");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText5 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку","Введенный пароль для почты selenium@test.com неверный. Забыли пароль?",actualErrorText5);
        driver.findElement(userNameLogInField).clear();
        driver.findElement(passwordLogInField).sendKeys("selenium2023");
        driver.findElement(logInButton).click();
        Assert.assertTrue("Произведён вход в аккаунт без указания пароля",
                driver.findElement(By.xpath("//*[contains(text(), 'Войти')]")).isDisplayed());
        String actualErrorText6 = driver.findElement(errorTextLocator).getText();
        Assert.assertEquals("Алерт о некорректном входе содержит ошибку","Error: Имя пользователя обязательно.",actualErrorText6);
    }
    @Test
    @Description("Проверка регистрации пользователя с использованием уже существующего логина и электронной почты")
    public void registrationTest7 () {
        driver.get(mainPageUrl);
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By registrationButton = By.xpath("//button[@class = 'custom-register-button']");
        By userNameInputReg = By.xpath("//input[@id = 'reg_username']");
        By emailInputReg = By.xpath("//input[@id = 'reg_email']");
        By passwordInputReg = By.xpath("//input[@id = 'reg_password']");
        By submitRegistrationButton = By.xpath("//button[@name = 'register']");
        By errorAlert = By.xpath("//ul[@class = 'woocommerce-error']/li");
        driver.findElement(signInButton).click();
        driver.findElement(registrationButton).click();
        driver.findElement(userNameInputReg).sendKeys("selenium2077");
        driver.findElement(emailInputReg).sendKeys("selenium@test.com");
        driver.findElement(passwordInputReg).sendKeys("selenium2077");
        driver.findElement(submitRegistrationButton).click();
        Assert.assertTrue("Сообщение об ошибке при появилось",driver.findElement(errorAlert).isDisplayed());
        String errorText = driver.findElement(errorAlert).getText();
        Assert.assertEquals("Сообщение об ошибке некорректное",
                "Error: Учетная запись с такой почтой уже зарегистировавана. Пожалуйста авторизуйтесь.",errorText);
    }
}
