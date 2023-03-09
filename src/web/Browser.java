package web;

import db.DBHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Browser {

    private final ChromeDriver driver;
    private final DBHandler db;
    public boolean watching = false;

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
        By messageLocator = By.cssSelector(".chat-line__message:not(.twitchStats-tracked)");

        WebElement cookieBanner = wait.until(ExpectedConditions.presenceOfElementLocated(cookieBannerLocator));
        removeElementFromDom(cookieBanner);

        String userName;
        StringBuilder messageBuilder;
        String message;
        List<WebElement> messageElements = new ArrayList<>();
        List<WebElement> fragments;
        List<String> fragmentClasses;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        while (this.watching) {

            try {
                messageElements = this.driver.findElements(messageLocator);
            } catch (NoSuchElementException e) {
                System.out.println("Elements not found. Waiting 5 seconds...");
                Thread.sleep(5000);
            }

            for (WebElement element: messageElements) {

                try {
                    if (!elementIsTracked(element)) {
                        userName = element.getAttribute("data-a-user");

                        fragments = element
                                .findElements(By.cssSelector(
                                        "span[data-a-target='chat-line-message-body'] span.text-fragment," + // text
                                                "span[data-a-target='chat-line-message-body'] span.mention-fragment," + // mentions
                                                "img.chat-image")); // emotes
                        messageBuilder = new StringBuilder();
                        for (WebElement fragment: fragments) {
                            fragmentClasses = Arrays.asList(fragment.getAttribute("class").split(" "));
                            if (fragmentClasses.contains("text-fragment") || fragmentClasses.contains("mention-fragment")) {
                                messageBuilder.append(" ").append(fragment.getText());
                            } else if (fragmentClasses.contains("chat-image")) {
                                messageBuilder.append(" ").append(fragment.getAttribute("alt"));
                                System.out.println(fragment);
                            }

                        }
                        message = messageBuilder.toString().replaceAll(" {2}", " ");
                        // the loop above puts a space in front of every fragment, so we remove the first space (for the first fragment)
                        message = message.trim();

                        if (!message.isEmpty()) {
                            this.db.saveMessage(userName, message, dateFormat.format(new Date().getTime()), channelName);
                        }

                        addDoneTagToElement(element);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        this.driver.executeScript("arguments[0].classList.add('twitchStats-tracked')", element);
    }

    private boolean elementIsTracked(WebElement element) {
        return element.getAttribute("class").contains("twitchStats-tracked");
    }

}
