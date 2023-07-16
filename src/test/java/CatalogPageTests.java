import jdk.jfr.Description;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CatalogPageTests {
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
    @Description("Проверка перехода к каталогу товаров через строку поиска")
    public void catalogSearchTest () {
        driver.get(mainPageUrl);
        By searchFiled = By.cssSelector("input.search-field");
        By submitSearch = By.cssSelector("button.searchsubmit");
        By searchResultTitle = By.cssSelector("h1.entry-title");
        By searchResultList = By.cssSelector("ul.products.columns-4");
        driver.findElement(searchFiled).sendKeys("xiaomi");
        driver.findElement(submitSearch).click();
        String searchResultTitleText = driver.findElement(searchResultTitle).getText().toLowerCase(Locale.ROOT).replace("результаты поиска: ","");
        Assert.assertEquals("Ошибка в результате поиска","“xiaomi”",searchResultTitleText);
        String searchResulProductsNames = driver.findElement(searchResultList).getText().toLowerCase();
        System.out.println(searchResulProductsNames);
        Assert.assertTrue("В результатах поиска нет упоминаний товара",searchResulProductsNames.contains("xiaomi"));
    }
    @Test
    @Description("Проверка перехода к карточке товара через каталог")
    public void getProductCardFromProductListTest () {
        driver.get(mainPageUrl);
        By catalogButtonInMainMenu = By.cssSelector(".menu-item-object-product_cat > a[href$='catalog/']");
        By listOfProducts = By.cssSelector("ul.products.columns-4");
        By firstProductName = By.xpath("(//div[@class='collection_desc clearfix'])[1]//h3");
        By firstProductLink = By.xpath("(//div[@class='collection_combine'])[1]//a");
        driver.findElement(catalogButtonInMainMenu).click();
        List<WebElement> productItems = driver.findElements(listOfProducts);
        js.executeScript("window.scrollBy(0,1000)");
        for (WebElement item : productItems) {
            WebElement price = item.findElement(By.cssSelector(".price"));
            WebElement button = item.findElement(By.cssSelector(".button"));

            boolean hasPriceText = price.getText().contains("₽");
            boolean hasAddToCartText = button.getText().equals("В корзину");
            boolean hasReadMoreText = button.getText().equals("Read more");
            Assert.assertTrue("В списке товаров не отображается цена и/или не отображается кнопка",
                    hasPriceText && (hasAddToCartText || hasReadMoreText));
        }
        String productName = driver.findElement(firstProductName).getText().toLowerCase();
        driver.findElement(firstProductLink).click();
        String actualName = driver.findElement(By.cssSelector("h1.entry-title.product_title")).getText().toLowerCase();
        Assert.assertTrue("Открытая страница не соответствует карточке",actualName.contains(productName));
    }
    @Test
    @Description("Проверка пагинации списка товаров в Каталоге.")
    public void paginationTest() {
        driver.get(catalogPage);
        By countNumOfProductsPerPage = By.xpath("//p[@class='woocommerce-result-count']");
        By pageTwoButton = By.xpath("//a[@class='page-numbers'][contains(text(), '2')]");
        By pagePreLast = By.xpath("//a[@class='page-numbers'][contains(text(), '10')]");
        By lastPage = By.xpath("(//a[@class='page-numbers'])[last()]");
        String actualFirstPageInfo = driver.findElement(countNumOfProductsPerPage).getText();
        Assert.assertEquals("В нумерации первой страницы ошибка","Отображение 1–12 из 130",actualFirstPageInfo);
        driver.findElement(pageTwoButton).click();
        String actualSecondPageInfo = driver.findElement(countNumOfProductsPerPage).getText();
        Assert.assertEquals("В нумерации второй страницы ошибка","Отображение 13–24 из 130",actualSecondPageInfo);
        driver.findElement(lastPage).click();
        String actualLastPageInfo = driver.findElement(countNumOfProductsPerPage).getText();
        Assert.assertEquals("В нумерации второй страницы ошибка","Отображение 121–130 из 130",actualLastPageInfo);
        driver.findElement(pagePreLast).click();
        String actualPreLastPageInfo = driver.findElement(countNumOfProductsPerPage).getText();
        Assert.assertEquals("В нумерации второй страницы ошибка","Отображение 109–120 из 130",actualPreLastPageInfo);
    }
    @Test
    @Description("Проверка категорий товаров и добавления товара в корзину через список с указанной категорией")
    public void categoryTest() {
        driver.get(catalogPage);
        By categoryOfHomeTechnic = By.xpath("//ul[@class='product-categories']//a[contains(text(), 'Бытовая техника')]");
        By titleOfPage = By.xpath("//h1[@class='entry-title ak-container']");
        By nameOfLastProduct = By.xpath("(//a[@class='collection_title'])[last()]");
        By addLastProductInCartButton = By.xpath("(//a[contains(text(), 'В корзину')])[last()]");
        By showMoreButtonOnLastProduct = By.xpath("(//a[contains(text(), 'Подробнее')])[last()]");
        By currentTitleOfCartPage = By.cssSelector("span.current");
        By nameOfProductInCart = By.xpath("//td[@class='product-name']");
        driver.findElement(categoryOfHomeTechnic).click();
        String actualCategoryPage = driver.findElement(titleOfPage).getText();
        Assert.assertEquals("Открытая страница не соответствует категории","БЫТОВАЯ ТЕХНИКА",actualCategoryPage);
        String productName = driver.findElement(nameOfLastProduct).getText();
        driver.findElement(addLastProductInCartButton).click();
        Assert.assertTrue("Кнопка у последней карточки не изменилась после клика",
                driver.findElement(showMoreButtonOnLastProduct).isDisplayed());
        driver.findElement(showMoreButtonOnLastProduct).click();
        String actualPageTitle = driver.findElement(currentTitleOfCartPage).getText();
        Assert.assertEquals("Переход в корзину не осуществлён","Корзина",actualPageTitle);
        String productNameInCart = driver.findElement(nameOfProductInCart).getText();
        Assert.assertEquals("Названия товара из корзины и списка товара не совпадают",productName,productNameInCart);
    }
    @Test
    @Description("Проверка перехода к карточке товара через строку поиска")
    public void searchAndTakeInCartTest() {
        driver.get(mainPageUrl);
        By searchFiled = By.cssSelector("input.search-field");
        By submitSearch = By.cssSelector("button.searchsubmit");
        By nameOfProduct = By.cssSelector("h1.product_title");
        By addToCartButton = By.xpath("//button[@name='add-to-cart']");
        By alertInCart = By.xpath("//div[@class='woocommerce-notices-wrapper']");
        String expectedAlert = "Вы отложили “iPad 2020 32gb wi-fi” в свою корзину.";
        driver.findElement(searchFiled).sendKeys("iPad 2020 32gb wi-fi");
        driver.findElement(submitSearch).click();
        String actualName = driver.findElement(nameOfProduct).getText();
        Assert.assertEquals("Товар не соответствует тому, который искали","iPad 2020 32gb wi-fi",actualName);
        driver.findElement(addToCartButton).click();
        Assert.assertTrue("Алерт не появился",driver.findElement(alertInCart).isDisplayed());
        String textFormAlert = driver.findElement(alertInCart).getText().replace("Подробнее","").trim();
        Assert.assertEquals("Ошибка в алерте",expectedAlert,textFormAlert);
    }
}
