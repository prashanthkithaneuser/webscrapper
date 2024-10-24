package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import java.io.IOException;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */

     
    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest(alwaysRun = true)
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }



    @Test(priority=1)
    public void testCase01() throws StreamWriteException, DatabindException, IOException, InterruptedException{

        try{
            driver.get("https://www.scrapethissite.com/pages/forms/");
            System.out.println("Successfully opened the url to perform webscrapping");
            Thread.sleep(2000);

        }catch(Exception e){
            System.out.println("Failed to connect to url for webscrapping data");
        }
        Wrappers.scrapHockeyData(driver);

    }


    @Test(priority=2)
    public void testCase02() throws StreamWriteException, DatabindException, IOException, InterruptedException{
        try{
            driver.get("https://www.scrapethissite.com/pages/ajax-javascript/");
            System.out.println("Successfully opened the url to perform webscrapping for test case2");
            Thread.sleep(2000);

        }catch(Exception e){
            System.out.println("Failed to connect to url for webscrapping data for test case 2");
        }  
        Wrappers.scrapMoviesData(driver);
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}