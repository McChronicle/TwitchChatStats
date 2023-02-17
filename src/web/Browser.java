package web;

import db.DBHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Browser {

    private final ChromeDriver driver;
    private final DBHandler db;

    public Browser(DBHandler db) {
        this.db = db;
        ChromeOptions options = new ChromeOptions();
        this.driver = new ChromeDriver(options);
    }

    public void quit() {
        driver.quit();
    }

    public void readMessages(String channelName) throws SQLException, InterruptedException {

        this.driver.get(String.format("https://www.twitch.tv/popout/%s/chat", channelName));

        Duration d = Duration.ofSeconds(10);
        WebDriverWait wait = new WebDriverWait(this.driver, d);
        By cookieBannerLocator = By.cssSelector(".Layout-sc-1xcs6mc-0 .bYReYr");
        By messageLocator = By.className("chat-line__message");

        WebElement cookieBanner = wait.until(ExpectedConditions.presenceOfElementLocated(cookieBannerLocator));
        removeElementFromDom(cookieBanner);

        String userName;
        StringBuilder messageBuilder;
        String message;
        List<WebElement> messageElements = new ArrayList<>();
        List<WebElement> textFragments;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        while (true) {

            try {
                messageElements = this.driver.findElements(messageLocator);
            } catch (NoSuchElementException e) {
                System.out.println("Element not found. Waiting 5 seconds...");
                Thread.sleep(5000);
            }

            for (WebElement element: messageElements) {

                if (!elementIsTracked(element)) {
                    userName = element.getAttribute("data-a-user");

                    textFragments = element
                            .findElements(By.cssSelector("span[data-a-target='chat-line-message-body'] span.text-fragment")); // ignores all emotes and mentions
                    messageBuilder = new StringBuilder();
                    for (WebElement textFragment: textFragments) {
                        messageBuilder.append(textFragment.getText());
                    }
                    message = messageBuilder.toString();

                    if (message.replaceAll(" ","").length() > 0) {
                        this.db.saveMessage(userName, message, dateFormat.format(new Date().getTime()));
                    }

                    addDoneTagToElement(element);
                }
            }

            Thread.sleep(100);
        }

    }


    private WebElement getLast(List<WebElement> l) {
        return l.get(l.size()-1);
    }

    private void removeElementFromDom(WebElement element) {
        this.driver.executeScript("arguments[0].remove();", element);
    }

    private void addDoneTagToElement(WebElement element) {
        this.driver.executeScript("arguments[0].classList.add('twitchStats.tracked')", element);
    }

    private boolean elementIsTracked(WebElement element) {
        return element.getAttribute("class").contains("twitchStats.tracked");
    }

}
