import jdk.jfr.Description;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderTests {
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
        driver.get(mainPageUrl);
        By signInButton = By.xpath("//div[@class = 'login-woocommerce']//a");
        By logInButton = By.xpath("//button[@name = 'login']");
        By userNameLogInField = By.xpath("//input[@name = 'username']");
        By passwordLogInField = By.xpath("//input[@name = 'password']");
        driver.findElement(signInButton).click();
        driver.findElement(userNameLogInField).sendKeys("BillingTester");
        driver.findElement(passwordLogInField).sendKeys("1234567890");
        driver.findElement(logInButton).click();
    }
    @After
    public void tearDown() {
        driver.get("http://intershop5.skillbox.ru/cart/");
        try {
            driver.findElement(By.xpath("//td[@class='product-remove']")).click();
        } catch (NoSuchElementException e) {
            System.out.println("Корзина уже пуста.");
        }
        driver.quit();
    }
    String mainPageUrl = "http://intershop5.skillbox.ru/";
    String orderPage = "http://intershop5.skillbox.ru/checkout/";
    @Test
    @Description("Проверка оформления заказа с заполнением всех полей + заказ с оплатой 'Прямой банковский перевод'")
    public void test1_orderTest1 () {
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By goToCartButton = By.xpath("//div[@role = 'alert']//a");
        By totalCartPrice = By.xpath("//td[@class = 'product-subtotal']");
        By billingName = By.id("billing_first_name");
        By billingLastName = By.id("billing_last_name");
        By billingAddress = By.id("billing_address_1");
        By billingCity = By.id("billing_city");
        By billingState = By.id("billing_state");
        By billingPostcode = By.id("billing_postcode");
        By billingPhone = By.id("billing_phone");
        By billingEmail = By.id("billing_email");
        By billingComment = By.id("order_comments");
        By bankPaymentMethod = By.xpath("//li[@class = 'wc_payment_method payment_method_bacs']//input");
        By takeAnOrder = By.id("place_order");
        driver.get("http://intershop5.skillbox.ru/?s=iPad+Air+2020+64gb+wi-fi&post_type=product");
        driver.findElement(addToCartButton).click();
        driver.findElement(goToCartButton).click();
        String orderPrice = driver.findElement(totalCartPrice).getText().replace(",00₽","");
        int expectedPrice = Integer.parseInt(orderPrice);
        driver.get(orderPage);
        driver.findElement(billingName).clear();
        driver.findElement(billingName).sendKeys("Тест");
        driver.findElement(billingLastName).clear();
        driver.findElement(billingLastName).sendKeys("Тестеров");
        driver.findElement(billingAddress).clear();
        driver.findElement(billingAddress).sendKeys(" Тестовая, д3");
        driver.findElement(billingCity).clear();
        driver.findElement(billingCity).sendKeys("Тестоград");
        driver.findElement(billingState).clear();
        driver.findElement(billingState).sendKeys("Тестовская");
        driver.findElement(billingPostcode).clear();
        driver.findElement(billingPostcode).sendKeys("444333");
        driver.findElement(billingPhone).clear();
        driver.findElement(billingPhone).sendKeys("89993332211");
        driver.findElement(billingEmail).clear();
        driver.findElement(billingEmail).sendKeys("selenium@test.com");
        driver.findElement(billingComment).sendKeys("Тестовый заказ");
        js.executeScript("window.scrollBy(0,1000)");
        driver.findElement(By.xpath("//li[@class = 'wc_payment_method payment_method_cod']//input")).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='payment']//p[contains(text(), 'Оплата')]")));
        String firstPaymentMessage = driver.findElement
                (By.xpath("//div[@id='payment']//p[contains(text(), 'Оплата')]")).getText();
        Assert.assertEquals("Ошибка в тексте со способом оплаты","Оплата наличными при доставке заказа.",firstPaymentMessage);
        driver.findElement(bankPaymentMethod).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@id='payment']//p[contains(text(), 'Оплату')]")));
        String secondPaymentMessage = driver.findElement
                (By.xpath("//div[@id='payment']//p[contains(text(), 'Оплату')]")).getText();
        Assert.assertEquals
                ("Оплату нужно направлять напрямую на наш банковский счет. " +
                        "Используйте идентификатор заказа в качестве кода платежа. " +
                        "Заказ будет отправлен после поступления средств на наш счет.",secondPaymentMessage);
        driver.findElement(takeAnOrder).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@class = 'post-title']")));
        int checkPrice = Integer.parseInt
                (driver.findElement
                        (By.xpath("//span[@class = 'woocommerce-Price-amount amount']//bdi")).getText().replace(",00₽",""));
        Assert.assertEquals("Цена в чеке не совпадает с ценой заказа!",expectedPrice,checkPrice);
        String checkPaymentMethod = driver.findElement(By.xpath("//li[@class = 'woocommerce-order-overview__payment-method method']//strong")).getText();
        Assert.assertEquals("В чеке указан не верный способ товара","Прямой банковский перевод",checkPaymentMethod);
    }
    @Test
    @Description("Проверка полей оформления заказа.")
    public void test2_orderTest2() {
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By billingName = By.id("billing_first_name");
        By billingLastName = By.id("billing_last_name");
        By billingAddress = By.id("billing_address_1");
        By billingCity = By.id("billing_city");
        By billingState = By.id("billing_state");
        By billingPostcode = By.id("billing_postcode");
        By billingPhone = By.id("billing_phone");
        By billingEmail = By.id("billing_email");
        By takeAnOrder = By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]");
        By errorAlertText = By.xpath("//ul[@class = 'woocommerce-error']//li");
        By block = By.xpath("//div[@class='blockUI blockOverlay']");
        driver.get("http://intershop5.skillbox.ru/product/air-3/");
        driver.findElement(addToCartButton).click();
        driver.get(orderPage);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(billingName));
        driver.findElement(billingName).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage1 = driver.findElement(errorAlertText).getText();
        Assert.assertEquals("Алерт об ошибке в имени содержит ошибку","Имя для выставления счета обязательное поле.",errorMessage1);
        driver.findElement(billingName).sendKeys("ТестовоеИмя");
        driver.findElement(billingLastName).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage2 = driver.findElement(By.xpath("//li[@data-id='billing_last_name']")).getText();
        Assert.assertEquals("Алерт об ошибке в фамилии содержит ошибку","Фамилия для выставления счета обязательное поле.",errorMessage2);
        driver.findElement(billingLastName).sendKeys("ФамилиияТестовая");
        driver.findElement(billingAddress).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage3 = driver.findElement(By.xpath("//li[@data-id='billing_address_1']")).getText();
        Assert.assertEquals("Алерт об ошибке в адресе содержит ошибку","Адрес для выставления счета обязательное поле.",errorMessage3);
        driver.findElement(billingAddress).sendKeys("Тестовый Адрес д.01");
        driver.findElement(billingCity).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage4 = driver.findElement(By.xpath("//li[@data-id='billing_city']")).getText();
        Assert.assertEquals("Алерт об ошибке в Адресе (Город) содержит ошибку","Город / Населенный пункт для выставления счета обязательное поле.",errorMessage4);
        driver.findElement(billingCity).sendKeys("Москва");
        driver.findElement(billingState).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage5 = driver.findElement(By.xpath("//li[@data-id='billing_state']")).getText();
        Assert.assertEquals("Алерт об ошибке в Адресе (Область) содержит ошибку","Область для выставления счета обязательное поле.",errorMessage5);
        driver.findElement(billingState).sendKeys("Московская");
        driver.findElement(billingPostcode).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage6 = driver.findElement(By.xpath("//li[@data-id='billing_postcode']")).getText();
        Assert.assertEquals("Алерт об ошибке в индексе содержит ошибку","Почтовый индекс для выставления счета обязательное поле.",errorMessage6);
        driver.findElement(billingPostcode).sendKeys("123123");
        driver.findElement(billingPhone).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage7 = driver.findElement(By.xpath("(//li[@data-id='billing_phone'])[2]")).getText();
        Assert.assertEquals("Алерт об ошибке в телефоне содержит ошибку","Телефон для выставления счета обязательное поле.",errorMessage7);
        driver.findElement(billingPhone).sendKeys("+79993332211");
        driver.findElement(billingEmail).clear();
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//button[@id='place_order'][contains(text(),'Оформить заказ')]")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(block));
        driver.findElement(takeAnOrder).click();
        Assert.assertTrue("Алерта с ошибкой не появилось",driver.findElement(errorAlertText).isDisplayed());
        String errorMessage8 = driver.findElement(By.xpath("//li[@data-id='billing_email']")).getText();
        Assert.assertEquals("Алерт об ошибке в емейл содержит ошибку","Адрес почты для выставления счета обязательное поле.",errorMessage8);
        driver.findElement(billingEmail).sendKeys("test@maillll.com");
    }
    @Test
    @Description("Проверка оформления заказа со скидочным купоном + заказ с оплатой 'Оплата при доставке'")
    public void test3_couponOrderTest() {
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By goToCartButton = By.xpath("//div[@role = 'alert']//a");
        By goToOrder = By.xpath("//div[@class='store-menu']//a[contains(text(),'Оформление заказ')]");
        By totalPriceInOrder = By.xpath("//tr[@class = 'order-total']//bdi");
        By textButtonToShowCoupon = By.xpath("//a[@class = 'showcoupon']");
        By inputCoupon = By.id("coupon_code");
        By submitCoupon = By.name("apply_coupon");
        By couponAlert = By.xpath("//div[@role = 'alert'][contains(text(), 'Купон успешно добавлен.')]");
        By discountBlock = By.xpath("//tr[@class = 'cart-discount coupon-sert500']//th");
        By removeCouponButton = By.xpath("//a[@class = 'woocommerce-remove-coupon']");
        By payToCourier = By.id("payment_method_cod");
        By takeAnOrder = By.id("place_order");
        driver.get("http://intershop5.skillbox.ru/product/led-телевизор-lg-65nano956na-ultra-hd-8k/");
        driver.findElement(addToCartButton).click();
        driver.get("http://intershop5.skillbox.ru/checkout/");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(totalPriceInOrder));
        js.executeScript("window.scrollBy(0,1000)");
        int actualPrice = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        driver.findElement(textButtonToShowCoupon).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCoupon));
        driver.findElement(inputCoupon).sendKeys("sert500");
        driver.findElement(submitCoupon).click();
        Assert.assertTrue("Алерт о применении купона не появился",driver.findElement(couponAlert).isDisplayed());
        Assert.assertTrue("Блок со скидкой не появился",driver.findElement(discountBlock).isDisplayed());
        Assert.assertTrue("Кнопка удаления купона не отображается",driver.findElement(removeCouponButton).isDisplayed());
        int priceAfterCoupon = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Купон не верно рассчитал скидку",priceAfterCoupon,(actualPrice - 500));
        driver.findElement(payToCourier).click();
        driver.findElement(takeAnOrder).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@class = 'post-title']")));
        js.executeScript("window.scrollBy(0,500)");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@class='post-title'][contains(text(),'Заказ получен')]")));
        int checkPrice = Integer.parseInt
                (driver.findElement
                        (By.xpath("//span[@class = 'woocommerce-Price-amount amount']//bdi")).getText().replace(",00₽",""));
        Assert.assertEquals("Цена в чеке не совпадает с ценой заказа!",priceAfterCoupon,checkPrice);
        String checkPaymentMethod = driver.findElement(By.xpath("//li[@class = 'woocommerce-order-overview__payment-method method']//strong")).getText();
        Assert.assertEquals("В чеке указан не верный способ товара","Оплата при доставке",checkPaymentMethod);
    }
    @Test
    @Description("Проверка удаления скидочного купона + негативный тест на некорректный купон.")
    public void test4_negativeCouponOrderTest() {
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By totalPriceInOrder = By.xpath("//tr[@class = 'order-total']//bdi");
        By textButtonToShowCoupon = By.xpath("//a[@class = 'showcoupon']");
        By inputCoupon = By.id("coupon_code");
        By submitCoupon = By.name("apply_coupon");
        By couponAlert = By.xpath("//ul[@role = 'alert']//li");
        By successCouponAlert = By.xpath("//div[@role = 'alert']");
        By removeCouponButton = By.xpath("//a[@class = 'woocommerce-remove-coupon']");
        driver.get("http://intershop5.skillbox.ru/product/samsung-galaxy-a21s-64gb-sm-a217f-черный/");
        driver.findElement(addToCartButton).click();
        driver.findElement(By.xpath("(//a[contains(text(), 'Оформление заказа')])[1]")).click();
        int actualPrice = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        driver.findElement(textButtonToShowCoupon).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCoupon));
        driver.findElement(inputCoupon).sendKeys("sert5000");
        driver.findElement(submitCoupon).click();
        Assert.assertTrue("Алерт об ошибке применения купона не появился",driver.findElement(couponAlert).isDisplayed());
        String actualAlertText = driver.findElement(couponAlert).getText();
        Assert.assertEquals("В тексте алерта ошибка","Неверный купон.",actualAlertText);
        int newPrice = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Ошибка! Скидка без купона прошла",newPrice,actualPrice);
        driver.findElement(textButtonToShowCoupon).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCoupon));
        driver.findElement(inputCoupon).clear();
        driver.findElement(inputCoupon).sendKeys("sert500");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(submitCoupon));
        driver.findElement(submitCoupon).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(successCouponAlert));
        Assert.assertTrue("Алерт о применении купона не появился",driver.findElement(successCouponAlert).isDisplayed());
        String actualAlertText2 = driver.findElement(successCouponAlert).getText();
        Assert.assertEquals("В тексте алерта ошибка","Купон успешно добавлен.",actualAlertText2);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//tr[@class='cart-discount coupon-sert500']")));
        int newPrice2 = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Ошибка! Скидка не прошла. ",newPrice2,(actualPrice - 500));
        driver.findElement(textButtonToShowCoupon).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCoupon));
        driver.findElement(inputCoupon).clear();
        driver.findElement(inputCoupon).sendKeys("sert500");
        driver.findElement(submitCoupon).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(couponAlert));
        Assert.assertTrue("Алерт об ошибке применения купона не появился",driver.findElement(couponAlert).isDisplayed());
        String actualAlertText3 = driver.findElement(couponAlert).getText();
        Assert.assertEquals("В тексте алерта ошибка","Coupon code already applied!",actualAlertText3);
        int newPrice3 = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Ошибка! Скидка не прошла. ",newPrice2,newPrice3);
        driver.findElement(removeCouponButton).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(successCouponAlert));
        Assert.assertTrue("Алерт об удалении  купона не появился",driver.findElement(successCouponAlert).isDisplayed());
        String actualAlertText4 = driver.findElement(successCouponAlert).getText();
        Assert.assertEquals("В тексте алерта ошибка","Купон удален.",actualAlertText4);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//tr[@class = 'cart-discount coupon-sert500']//th")));
        int newPrice4 = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Cкидка не отменена после удаления купона",actualPrice,newPrice4);
        driver.findElement(textButtonToShowCoupon).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputCoupon));
        driver.findElement(inputCoupon).clear();
        driver.findElement(submitCoupon).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(couponAlert));
        String actualAlertText5 = driver.findElement(couponAlert).getText();
        Assert.assertEquals("В тексте алерта ошибка","Пожалуйста, введите код купона.",actualAlertText5);
        int newPrice5 = Integer.parseInt(driver.findElement(totalPriceInOrder).getText().replace(",00₽",""));
        Assert.assertEquals("Cкидка не отменена после удаления купона",actualPrice,newPrice5);
        }
}

