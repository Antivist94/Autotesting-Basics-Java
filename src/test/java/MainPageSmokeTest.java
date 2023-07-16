import jdk.jfr.Description;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class MainPageSmokeTest {
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
    String expectedEmail = "skillbox@skillbox.ru";
    String expectedPhoneNumber = "+7-999-123-12-12";
    String catalogPageExpectedUrl = "http://intershop5.skillbox.ru/product-category/catalog/";
    String myAccountExpectedUrl = "http://intershop5.skillbox.ru/my-account/";
    String cartPageExpectedUrl = "http://intershop5.skillbox.ru/cart/";
    @Test
    @Description("Проверка элементов в хэдере сайта")
    public void headerCheckUp () {
        driver.get(mainPageUrl);
        var phoneNumberOnHeader = driver.findElement
                (By.xpath("//div[@class='textwidget custom-html-widget']/a[1]")).getText();
        var emailOnHeader = driver.findElement
                (By.xpath("//div[@class='textwidget custom-html-widget']/a[2]")).getText();
        Assert.assertEquals("Ошибка в номере телефона в хедере", phoneNumberOnHeader, expectedPhoneNumber);
        Assert.assertEquals("Ошибка в электронной почте в хедере", emailOnHeader, expectedEmail);
        Assert.assertTrue("Поле с поиском в хедере не отображается",
                driver.findElement(By.cssSelector("input[name = 's']")).isDisplayed());
        Assert.assertTrue("Кнопка поиска в хедере не отображается",
                driver.findElement(By.cssSelector("button[class = 'searchsubmit']")).isDisplayed());
        Assert.assertTrue("Логотип в хедере не отображается",
                driver.findElement(By.xpath("//a[@class='site-logo']/img")).isDisplayed());
        var singUpButton = driver.findElement(By.xpath("//a[@class = 'account']"));
        Assert.assertTrue("Кнопка входа не отображается",singUpButton.isDisplayed());
        String singUpButtonText = singUpButton.getText().trim();
        Assert.assertEquals("На кнопке входа некорректный текст", "Войти", singUpButtonText);
    }
    @Test
    @Description("Проверка элементов основного меню сайта")
    public void mainMenuElementsCheckUp() {
        driver.get(mainPageUrl);
        var mainPageButtonInMainMenu = driver.findElement
                (By.xpath("//li[contains(@class,'menu-item-home')]//a"));
        var catalogButtonInMainMenu = driver.findElement
                (By.cssSelector(".menu-item-object-product_cat > a[href$='catalog/']"));
        var myAccountButtonInMainMenu = driver.findElement(By.cssSelector(".menu-item > a[href$='my-account/']"));
        var cartButtonInMainMenu = driver.findElement(By.cssSelector(".menu-item > a[href$='cart/']"));
        var myOrderButtonInMainMenu = driver.findElement(By.cssSelector(".menu-item > a[href$='checkout/']"));
        Assert.assertTrue("Кнопка 'Главная' не отображается",mainPageButtonInMainMenu.isDisplayed());
        Assert.assertTrue("Кнопка 'Каталог' не отображается",catalogButtonInMainMenu.isDisplayed());
        Assert.assertTrue("Кнопка 'Мой аккаунт' не отображается",myAccountButtonInMainMenu.isDisplayed());
        Assert.assertTrue("Кнопка 'Корзина' не отображается",cartButtonInMainMenu.isDisplayed());
        Assert.assertTrue("Кнопка 'Мой заказ' не отображается",myOrderButtonInMainMenu.isDisplayed());
    }
    @Test
    @Description("Проверка контентной части главной страницы")
    public void mainPageContentCheckUp() {
        driver.get(mainPageUrl);
        var promoImagesSmall = driver.findElements(By.xpath("//div[@class = 'promo-wrap1']//img"));
        var smallPromoImagesButton = driver.findElements
                (By.xpath("//div[@class = 'promo-wrap1']//span[@class = 'btn promo-link-btn']"));
        Assert.assertEquals("Промо карточек не три",3,promoImagesSmall.size());
        Assert.assertEquals("Кнопок на промо карточках не три",3,smallPromoImagesButton.size());
        Assert.assertTrue("Название блока 'Распродажа' не указано",
                driver.findElement(By.xpath("//*[h2 ='Распродажа']")).isDisplayed());
        Assert.assertTrue("Название блока 'Новые поступления' не указано",
                driver.findElement(By.xpath("//*[h2 ='Новые поступления']")).isDisplayed());
        List<WebElement> promoImages = driver.findElements(By.xpath("(//div[@class = 'slick-list draggable'])[2]//li[@aria-hidden = 'false']"));
        for (WebElement promoImage : promoImages) {
            List<WebElement> onSaleLabels = promoImage.findElements(By.xpath("//span[@class='onsale']"));
            if (onSaleLabels.size() == 0) {
                fail("Не лейбла 'Скидка' на промо фото в 'Новых поступлениях'" + promoImage.getText());
            }
        }
        Assert.assertTrue("Большое промо фото не отображается",
                driver.findElement(By.xpath("//img[@alt = 'Уже в продаже!']")).isDisplayed());
        js.executeScript("window.scrollBy(0,1300)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("(//span[@class= 'btn promo-link-btn']) [4]")));
        Assert.assertTrue("Нет кнопки на большом промо фото",
                driver.findElement(By.xpath("(//span[@class= 'btn promo-link-btn']) [4]")).isDisplayed());
    }
    @Test
    @Description("Проверка отображения цен у карточек товаров")
    public void showPriceTest() {
        driver.get(mainPageUrl);
        List<WebElement> productCard = driver.findElements(By.xpath("//section[@class='product-slider']//li[@aria-hidden='false']"));
        js.executeScript("window.scrollBy(0,3500)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//section[@class='product-slider']//li[@aria-hidden='false']")));
        for (WebElement product : productCard) {
            WebElement price = product.findElement(By.cssSelector("span.price"));
            String priceText = price.getText();
            if (!priceText.contains("₽") && priceText.matches(".*[0-9].*")) {
                fail("У товара " + product.findElement(By.cssSelector("h3")).getText() + " не отображается значок ₽ и/или цена в цифрах");
            }
        }
    }
    @Test
    @Description("Проверка футера главной страницы")
    public void mainPageFooterCheckUp() throws InterruptedException {
        driver.get(mainPageUrl);
        var footerPagesMenu = driver.findElements(By.xpath("//aside[@id = 'pages-2']//a"));
        Assert.assertEquals("Пунктов меню в футере не 6",6,footerPagesMenu.size());
        List<String> expectedOptions = Arrays.asList("Все товары", "Главная", "Корзина", "Мой аккаунт", "Оформление заказа", "Регистрация");
        List<WebElement>menuOptions = driver.findElements(By.xpath("//aside[@id = 'pages-2']//a"));
        List<Object> actualOptions = new ArrayList<>();
        for (WebElement menuOption : menuOptions) {
            actualOptions.add(menuOption.getText());
        }
        Assert.assertEquals("В пунктах меню в футере ошибка",expectedOptions,actualOptions);
        By footerPhone = By.xpath("//*[@class='text-5-value'][strong[contains(text(), 'Телефон:')]]");
        By footerEmail = By.xpath("//p[@class='text-5-value'][strong ='Email:']");
        js.executeScript("window.scrollBy(0,2500)");
        Thread.sleep(1000);
        var phoneNumberElement = driver.findElement(footerPhone).getText().trim().replace("Телефон: ","");
        var actualEmailInFooter =
                driver.findElement(footerEmail).getText().trim().replace("Email: ","");
        Assert.assertEquals("Ошибка в номере телефона в футере", phoneNumberElement, expectedPhoneNumber);
        Assert.assertEquals("Ошибка в электронной почте в футере", actualEmailInFooter, expectedEmail);
    }
    @Test
    @Description("Проверка кнопки перехода в каталог через главное меню")
    public void mainMenuCatalogButtonTest() {
        By catalogButtonInMainMenu = By.cssSelector(".menu-item-object-product_cat > a[href$='catalog/']");
        driver.get(mainPageUrl);
        driver.findElement(catalogButtonInMainMenu).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Переход по кнопке 'Каталог' на страницу каталога не произведён",
                catalogPageExpectedUrl,actualUrl);
    }
    @Test
    @Description("Проверка кнопки перехода на страницу Аккаунта через главное меню")
    public void mainMenuMyAccountButtonTest() {
        driver.get(mainPageUrl);
        By myAccountButtonInMainMenu = By.cssSelector(".menu-item > a[href$='my-account/']");
        driver.findElement(myAccountButtonInMainMenu).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Переход по кнопке 'Мой Аккаунт' на страницу Аккаунта не произведён",
                myAccountExpectedUrl,actualUrl);
    }
    @Test
    @Description("Проверка кнопки перехода в корзину через главное меню")
    public void mainMenuCartButtonTest() {
        driver.get(mainPageUrl);
        By cartButtonInMainMenu = By.cssSelector(".menu-item > a[href$='cart/']");
        driver.findElement(cartButtonInMainMenu).click();
        String actualCatalogUrl = driver.getCurrentUrl();
        Assert.assertEquals("Переход по кнопке 'Корзина' на страницу корзины не произведён",
                cartPageExpectedUrl,actualCatalogUrl);
    }
    @Test
    @Description("Проверка кнопок в главном меню на переход к соответствующей странице")
    public void mainMenuMyOrderButtonTest() {
        driver.get(mainPageUrl);
        By myOrderButtonInMainMenu = By.cssSelector(".menu-item > a[href$='checkout/']");
        driver.findElement(myOrderButtonInMainMenu).click();
        String actualCatalogUrl = driver.getCurrentUrl();
        Assert.assertEquals("Переход по кнопке 'Каталог' на страницу каталога не произведён",
                "http://intershop5.skillbox.ru/cart/",actualCatalogUrl);
    }
    @Test
    @Description("Проверка кнопки 'Просмотреть' на карточке 'Книги'")
    public void bookCardButton () {
        driver.get(mainPageUrl);
        By showButtonOnBooksCard = By.xpath("//div[@class='caption wow fadeIn' and h4[contains(text(),'Книги')]]/span[contains(text(),'Просмотреть')]");
        String expectedUrl = "http://intershop5.skillbox.ru/product-category/catalog/books/";
        driver.findElement(showButtonOnBooksCard).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Не верный юрл при переходе по клику на 'Просмотреть' с карточки 'Книги'",
                expectedUrl,actualUrl);
    }
    @Test
    @Description("Проверка кнопки 'Просмотреть' на карточке 'Планшеты'")
    public void tabletsCardButton () {
        driver.get(mainPageUrl);
        By showButtonOnTabletsCard = By.xpath("//div[@class='caption wow fadeIn' and h4[contains(text(),'Планшеты')]]/span[contains(text(),'Просмотреть')]");
        String expectedUrl = "http://intershop5.skillbox.ru/product-category/catalog/electronics/pad/";
        driver.findElement(showButtonOnTabletsCard).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Не верный юрл при переходе по клику на 'Просмотреть' с карточки 'Планшеты'",
                expectedUrl,actualUrl);
    }
    @Test
    @Description("Проверка кнопки 'Просмотреть' на карточке 'Фотоаппараты'")
    public void photoCameraCardButton() {
        driver.get(mainPageUrl);
        By showButtonOnCameraCard = By.xpath("//div[@class='caption wow fadeIn' and h4[contains(text(),'Фотоаппараты')]]/span[contains(text(),'Просмотреть')]");
        String expectedUrl = "http://intershop5.skillbox.ru/product-category/catalog/electronics/photo_video/";
        driver.findElement(showButtonOnCameraCard).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Не верный юрл при переходе по клику на 'Просмотреть' с карточки 'Фотоаппараты'",
                expectedUrl,actualUrl);
    }
    @Test
    @Description("Проверка просмотра товара по кнопке с большого банера + проверка кнопки 'Главная' + проверка указания просмотренного товара")
    public void bigPromoPhotoButtonTest() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,1000)");
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='promo-widget-wrap-full style_one']//span[@class='btn promo-link-btn']")));
        By showProductButtonBigPromo = By.xpath("//div[@class='promo-widget-wrap-full style_one']//span[@class='btn promo-link-btn']");
        By historyOfViewList = By.xpath("//ul[@class = 'product_list_widget']");
        By mainPageButtonInMainMenu = By.xpath("//li[contains(@class,'menu-item-home')]//a");
        driver.findElement(showProductButtonBigPromo).click();
        String actualTitle = driver.getTitle().replace(" — Skillbox","");
        driver.findElement(mainPageButtonInMainMenu).click();
        js.executeScript("window.scrollBy(0,2300)");
        String historyOfViewText = driver.findElement(historyOfViewList).getText();
        System.out.println(historyOfViewText);
        System.out.println(actualTitle);
        Assert.assertTrue("ошибка в истории просмотров",historyOfViewText.contains(actualTitle));
    }
    @Test
    @Description("Проверка поиска на главной странице сайта")
    public void searchFiledTest() {
        driver.get(mainPageUrl);
        By searchFiled = By.cssSelector("input.search-field");
        By submitSearch = By.cssSelector("button.searchsubmit");
        By searchResultTitle = By.cssSelector("h1.entry-title");
        By searchResultList = By.cssSelector("ul.products.columns-4");
        driver.findElement(searchFiled).sendKeys("тестовый поиск");
        driver.findElement(submitSearch).click();
        String searchResult1 = driver.findElement(searchResultTitle).getText().toLowerCase(Locale.ROOT).replace("результаты поиска: ","");
        Assert.assertEquals("Ошибка в результате поиска","“тестовый поиск”",searchResult1);
        driver.findElement(searchFiled).clear();
        driver.findElement(searchFiled).sendKeys("СМАРТФОН");
        driver.findElement(submitSearch).click();
        String searchResult2 = driver.findElement(searchResultTitle).getText().replace("РЕЗУЛЬТАТЫ ПОИСКА: ","");
        Assert.assertEquals("Ошибка в результате поиска","“СМАРТФОН”",searchResult2);
        String actualSearchResultList = driver.findElement(searchResultList).getText().toLowerCase(Locale.ROOT);
        Assert.assertTrue("ошибка в истории просмотров",actualSearchResultList.contains("смартфон"));
    }
    @Test
    @Description("Проверка кнопки 'Все товары' в футере сайта")
    public void footerButtonsTest1() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,2000)");
        By allProductsButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Все товары')]");
        By titleOfPage = By.xpath("//header[@id = 'title_bread_wrap']//span");
        driver.findElement(allProductsButton).click();
        String actualTitle = driver.findElement(titleOfPage).getText().toLowerCase(Locale.ROOT);
        Assert.assertEquals("Ошибка при переходе на страницу 'Все товары' из футера","все товары",actualTitle);
    }
    @Test
    @Description("Проверка кнопки 'Главная' в футере сайта")
    public void footerButtonsTest2() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,2000)");
        By mainPageButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Главная')]");
        driver.findElement(mainPageButton).click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals("Ошибка при переходе на страницу 'Главная' из футера","http://intershop5.skillbox.ru/",actualUrl);
    }
    @Test
    @Description("Проверка кнопки 'Корзина' в футере сайта")
    public void footerButtonsTest3() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,2000)");
        By cartButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Корзина')]");
        By titleOfPage = By.xpath("//div[@id='content']//span[@class = 'current']");
        driver.findElement(cartButton).click();
        String actualTitle = driver.findElement(titleOfPage).getText().toLowerCase(Locale.ROOT);
        Assert.assertEquals("Отображается другая страница, должна быть 'Корзина'","корзина",actualTitle);
    }
    @Test
    @Description("Проверка кнопки 'Мой аккаунт' в футере сайта")
    public void footerButtonsTest4() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,2000)");
        By myAccountButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Мой аккаунт')]");
        By titleOfPage = By.xpath("//div[@id='content']//span[@class = 'current']");
        driver.findElement(myAccountButton).click();
        String actualTitle = driver.findElement(titleOfPage).getText().toLowerCase(Locale.ROOT);
        Assert.assertEquals("Отображается другая страница, должна быть 'Мой аккаунт'","мой аккаунт",actualTitle);
    }
    @Test
    @Description("Проверка кнопки 'Оформление заказа' в футере сайта")
    public void footerButtonsTest5() {
        driver.get(mainPageUrl);
        By takeAnOrderButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Оформление заказа')]");
        By titleOfPage = By.xpath("//div[@id='content']//span[@class = 'current']");
        js.executeScript("window.scrollBy(0,2000)");
        driver.findElement(takeAnOrderButton).click();
        String actualTitle = driver.findElement(titleOfPage).getText().toLowerCase(Locale.ROOT);
        Assert.assertEquals("Отображается другая страница, должна быть 'Корзина'","корзина",actualTitle);
    }
    @Test
    @Description("Проверка кнопки 'Регистрация' в футере сайта")
    public void footerButtonsTest6() {
        driver.get(mainPageUrl);
        js.executeScript("window.scrollBy(0,2000)");
        By myAccountButton = By.xpath("//div[@class ='top-footer-block']//a[contains(text(),'Регистрация')]");
        By titleOfPage = By.xpath("//div[@id='content']//span[@class = 'current']");
        driver.findElement(myAccountButton).click();
        String actualTitle = driver.findElement(titleOfPage).getText().toLowerCase(Locale.ROOT);
        Assert.assertEquals("Отображается другая страница, должна быть 'Регистрация'","регистрация",actualTitle);
    }
}
