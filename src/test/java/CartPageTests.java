import jdk.jfr.Description;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CartPageTests {
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
    String catalogPage = "http://intershop5.skillbox.ru/product-category/catalog/";
    @Test
    @Description("Тест на изменение количества товаров в корзине")
    public void cartCountAndPriceTest() throws InterruptedException {
        driver.get(catalogPage);
        By addFirstProductInCartButton = By.xpath("(//div[@class='price-cart']//a[contains(text(), 'В корзину')])[1]");
        By cartButton = By.cssSelector(".menu-item > a[href$='cart/']");
        By totalCartPriceLocator = By.xpath("//td[@class = 'product-subtotal']");
        By inputCountOfProducts = By.xpath("//input[@type = 'number']");
        By cartUpdateAlert = By.xpath("//div[@role = 'alert'][contains(text(), 'Cart updated.')]");
        driver.findElement(addFirstProductInCartButton).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("(//div[@class='price-cart']//a[contains(text(), 'Подробнее')])[1]")));
        driver.findElement(cartButton).click();
        String totalCartPrice = driver.findElement(totalCartPriceLocator).getText();
        int price = Integer.parseInt(totalCartPrice.replaceAll("[^\\d]", ""));
        price = price /100;
        driver.findElement(inputCountOfProducts).sendKeys(Keys.DELETE);
        driver.findElement(inputCountOfProducts).sendKeys("2");
        driver.findElement(inputCountOfProducts).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartUpdateAlert));
        Assert.assertTrue("Алерт не появился после обновления корзины не появился",
                driver.findElement(cartUpdateAlert).isDisplayed());
        String newTotalCartPrice1 = driver.findElement(totalCartPriceLocator).getText();
        int newPrice = Integer.parseInt(newTotalCartPrice1.replaceAll("[^\\d]", ""));
        newPrice = newPrice /100;
        Assert.assertTrue("Ошибка в цене при изменении кол-ва товаров",(newPrice == (price * 2)));
        driver.findElement(inputCountOfProducts).sendKeys(Keys.DELETE);
        driver.findElement(inputCountOfProducts).sendKeys("1");
        driver.findElement(inputCountOfProducts).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartUpdateAlert));
        Assert.assertTrue("Алерт не появился после обновления корзины не появился",
                driver.findElement(cartUpdateAlert).isDisplayed());
        Thread.sleep(1000);//Здесь иначе я не знаю как, потому что небольшая подзагрузка после ввода нового количества товаров
        String newTotalCartPrice2 = driver.findElement(totalCartPriceLocator).getText();
        int newPrice2 = Integer.parseInt(newTotalCartPrice2.replaceAll("[^\\d]", ""));
        newPrice2 = newPrice2 /100;
        System.out.println(newPrice2);
        System.out.println(price);
        Assert.assertTrue("Ошибка в цене при изменении кол-ва товаров",(price == newPrice2));
    }
    @Test
    @Description("Тест на удаление товара из корзины + тест на восстановление удаленного товара")
    public void deleteAndRestoreProductInCartTest() {
        driver.get(catalogPage);
        By categoryOfTVs = By.xpath("//ul[@class='product-categories']//a[contains(text(), 'Телевизоры')]");
        By addFirstProduct = By.xpath("(//div[@class='wc-products']//li//a[contains(text(), 'В корзину')])[1]");
        By goToCartFromFirstProduct = By.xpath("(//div[@class='wc-products']//li//a[contains(text(), 'Подробнее')])[1]");
        By totalCartPriceLocator = By.xpath("//td[@class = 'product-subtotal']");
        By productPrice = By.xpath("//td[@class = 'product-price']");
        By productName = By.xpath("//td[@class = 'product-name']");
        By deleteButton = By.xpath("//td[@class = 'product-remove']//a");
        By cartUpdateAlert = By.xpath("//div[@role = 'alert']");
        By restoreButtonInAlert = By.xpath("//div[@role = 'alert']//a['restore-item']");
        By cartIsEmptyPicture = By.xpath("//p[@class='cart-empty woocommerce-info']");
        By checkOutButton = By.xpath("//div[@class='wc-proceed-to-checkout']//a");
        driver.findElement(categoryOfTVs).click();
        driver.findElement(addFirstProduct).click();
        driver.findElement(goToCartFromFirstProduct).click();
        String totalPriceOld = driver.findElement(totalCartPriceLocator).getText();
        String priceOld = driver.findElement(productPrice).getText();
        String name = driver.findElement(productName).getText();
        driver.findElement(deleteButton).click();
        Assert.assertTrue("Сообщения 'Корзина пуста' не появилось",driver.findElement(cartIsEmptyPicture).isDisplayed());
        String alertText = driver.findElement(cartUpdateAlert).getText().trim();
        Assert.assertEquals("В тексте алерта ошибка","“"+name+"” удален. Вернуть?",alertText);
        driver.findElement(restoreButtonInAlert).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(totalCartPriceLocator));
        String newTotalPrice = driver.findElement(totalCartPriceLocator).getText();
        String newPrice = driver.findElement(productPrice).getText();
        String newName = driver.findElement(productName).getText();
        Assert.assertEquals("После восстановления удалённого товара цена изменилась",totalPriceOld,newTotalPrice);
        Assert.assertEquals("После восстановления удалённого товара название изменилось",name,newName);
        Assert.assertEquals("После восстановления удалённого товара цена изменилась",priceOld,newPrice);
        driver.findElement(checkOutButton).click();
        String actualPageUrl = driver.getCurrentUrl();
        Assert.assertEquals("Переход на страницу оформления заказа не осуществлён","http://intershop5.skillbox.ru/checkout/",actualPageUrl);
    }
    @Test
    @Description("Тест на купон sert500 на скидку в 500р")
    public void couponTest() {
        driver.get(mainPageUrl);
        By searchFiled = By.cssSelector("input.search-field");
        By submitSearch = By.cssSelector("button.searchsubmit");
        By priceInCardOfProduct = By.xpath("//p[@class = 'price']");
        By addToCartButtonOnCardOfProduct = By.xpath("//div[@class='summary entry-summary']//button[@type = 'submit']");
        By goToCartButtonInCardOfProduct = By.xpath("//div[@class='woocommerce-notices-wrapper']//a");
        By productPrice = By.xpath("//td[@class = 'product-price']");
        By orderPrice = By.xpath("//tr[@class='order-total']//bdi");
        By inputCoupon = By.xpath("//div[@class='coupon']//input");
        By submitCoupon = By.xpath("//div[@class='coupon']//button");
        By cartUpdateAlert = By.xpath("//div[@role = 'alert']");
        driver.findElement(searchFiled).sendKeys("BEKO WRE64P1BWW");
        driver.findElement(submitSearch).click();
        String actualPriceFromCatalogPage = driver.findElement(priceInCardOfProduct).getText().trim();
        driver.findElement(addToCartButtonOnCardOfProduct).click();
        driver.findElement(goToCartButtonInCardOfProduct).click();
        String priceFormCart = driver.findElement(productPrice).getText();
        Assert.assertEquals("Цена в корзине отличается от цены в каталоге",actualPriceFromCatalogPage,priceFormCart);
        String priceBeforeCoupon = driver.findElement(orderPrice).getText();
        int priceBefore = Integer.parseInt(priceBeforeCoupon.replaceAll("[^\\d]", ""));
        priceBefore = priceBefore /100;
        driver.findElement(inputCoupon).sendKeys("sert500");
        driver.findElement(submitCoupon).click();
        Assert.assertTrue("Алерт после ввода купона не появился",driver.findElement(cartUpdateAlert).isDisplayed());
        String alertText = driver.findElement(cartUpdateAlert).getText();
        Assert.assertEquals("Нет алерта о добавлении купона","Купон успешно добавлен.",alertText);
        Assert.assertTrue("Строка с указанием скидки не появилась",
                driver.findElement(By.xpath("//tr[@class='cart-discount coupon-sert500']")).isDisplayed());
        String priceAfterCoupon = driver.findElement(By.xpath("//tr[@class='order-total']")).getText();
        int priceAfter = Integer.parseInt(priceAfterCoupon.replaceAll("[^\\d]", ""));
        priceAfter = priceAfter /100;
        Assert.assertEquals("Скидка рассчитана некорректно",(priceBefore - 500),priceAfter);
    }
    @Test
    @Description("Тест на удаление купона скидки для скидочного товара + Негативный тест на добавление несуществующего купона")
    public void couponTest2 () {
        driver.get(catalogPage);
        By searchFiled = By.cssSelector("input.search-field");
        By submitSearch = By.cssSelector("button.searchsubmit");
        By saleLabelLocator = By.xpath("//span[@class = 'onsale']");
        By addToCartButtonOnCardOfProduct = By.xpath("//div[@class='summary entry-summary']//button[@type = 'submit']");
        By goToCartButtonInCardOfProduct = By.xpath("//div[@class='woocommerce-notices-wrapper']//a");
        By orderPrice = By.xpath("//tr[@class='order-total']//bdi");
        By inputCoupon = By.xpath("//div[@class='coupon']//input");
        By submitCoupon = By.xpath("//div[@class='coupon']//button");
        By deleteCoupon = By.xpath("//tr[@class='cart-discount coupon-sert500']//a[contains(text(), 'Удалить')]");
        By cartUpdateAlert = By.xpath("//div[@role = 'alert']");
        driver.findElement(searchFiled).sendKeys("OnePlus 8 Pro");
        driver.findElement(submitSearch).click();
        Assert.assertTrue("Товар не скидочный, необходимо заменить товар для теста",
                driver.findElement(saleLabelLocator).isDisplayed());
        driver.findElement(addToCartButtonOnCardOfProduct).click();
        driver.findElement(goToCartButtonInCardOfProduct).click();
        String priceBeforeCoupon = driver.findElement(orderPrice).getText();
        int priceBefore = Integer.parseInt(priceBeforeCoupon.replaceAll("[^\\d]", ""));
        priceBefore = priceBefore /100;
        driver.findElement(inputCoupon).sendKeys("sert500");
        driver.findElement(submitCoupon).click();
        Assert.assertTrue("Алерт после ввода купона не появился",driver.findElement(cartUpdateAlert).isDisplayed());
        String alertText = driver.findElement(cartUpdateAlert).getText();
        Assert.assertEquals("Нет алерта о добавлении купона","Купон успешно добавлен.",alertText);
        Assert.assertTrue("Строка с указанием скидки не появилась",
                driver.findElement(By.xpath("//tr[@class='cart-discount coupon-sert500']")).isDisplayed());
        String priceAfterCoupon = driver.findElement(By.xpath("//tr[@class='order-total']")).getText();
        int priceAfter = Integer.parseInt(priceAfterCoupon.replaceAll("[^\\d]", ""));
        priceAfter = priceAfter /100;
        Assert.assertEquals("Скидка рассчитана некорректно",(priceBefore - 500),priceAfter);
        driver.findElement(deleteCoupon).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@role = 'alert'][contains(text(),'Купон удален.')]")));
        String alertText2 = driver.findElement(cartUpdateAlert).getText();
        Assert.assertEquals("Нет алерта об удалении купона","Купон удален.",alertText2);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//tr[@class='cart-discount coupon-sert500']")));
        String priceAfterDeleteCoupon = driver.findElement(By.xpath("//tr[@class='order-total']//bdi")).getText();
        Assert.assertEquals("Ошибка в цене, после удаления скидочного купона",
                priceBeforeCoupon,priceAfterDeleteCoupon);
        driver.findElement(inputCoupon).sendKeys("sert10000");
        driver.findElement(submitCoupon).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//ul[@role = 'alert']")));
        String negativeAlertText = driver.findElement(By.xpath("//ul[@role = 'alert']")).getText();
        Assert.assertEquals("Ошибка в алерте при добавлении несуществующего купона","Неверный купон.",negativeAlertText);
        String lastPrice = driver.findElement(orderPrice).getText();
        Assert.assertEquals("Цена после ввода несуществующего купона изменилась",priceBeforeCoupon,lastPrice);
    }

}
