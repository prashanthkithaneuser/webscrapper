package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */


     public static void scrapHockeyData(WebDriver driver) throws StreamWriteException, DatabindException, IOException, InterruptedException{
         // List to hold the scraped data
         List<Map<String, Object>> scrapedJsonData = new ArrayList<>();

         try {
             // Loop through the first 4 pages
             for (int i = 1; i <= 4; i++) {
                 driver.get("https://www.scrapethissite.com/pages/forms/?page_num=" + i);
                 Thread.sleep(2000);
                 // Find all team rows
                 List<WebElement> rows = driver.findElements(By.cssSelector(".team"));
                // Capture the current epoch time
                long epochTime = Instant.now().getEpochSecond();
                 for (WebElement row : rows) {
                     // Create a map to store each team's data dynamically
                     Map<String, Object> teamData = new HashMap<>();
                     
                     // Extract the fields from the row and put them into the map
                     teamData.put("Team_Name", row.findElement(By.cssSelector(".name")).getText());
                     teamData.put("Year", Integer.parseInt(row.findElement(By.cssSelector(".year")).getText()));
                     teamData.put("win_percentage", Double.parseDouble(row.findElement(By.cssSelector(".pct")).getText()));
                     teamData.put("epochTime",epochTime);
                     // Add the map to the list
                     scrapedJsonData.add(teamData);
                 }
             }
         } finally {
             //do nothing here
         }
 
         // Serialize the scraped data to JSON and save it to a file
         ObjectMapper objectMapper = new ObjectMapper();
         objectMapper.writeValue(new File("./hockey-team-data.json"), scrapedJsonData);
 
         System.out.println("Data has been saved to hockey-team-data.json");
     
     }

     public static void scrapMoviesData(ChromeDriver driver) throws StreamWriteException, DatabindException, IOException, InterruptedException {
        // List to hold all scraped data
        List<Map<String, Object>> movieData = new ArrayList<>();
    
        // Capture all the year buttons
        List<WebElement> yearButtons = driver.findElements(By.cssSelector(".year-link"));
    
        // Retry configuration
        int maxRetries = 3;
        int waitBetweenRetries = 2000; // 2 seconds
    
        // Loop through each year
        for (WebElement yearButton : yearButtons) {
            String year = yearButton.getText().trim();
            boolean successfulScrape = false;
    
            for (int attempt = 0; attempt < maxRetries; attempt++) {
                try {
                    // Click on the year button
                    yearButton.click();
    
                    // Wait for the data to load
                    Thread.sleep(4000);
    
                    // Capture the epoch time for the scrape
                    long epochTime = Instant.now().getEpochSecond();
    
                    // Get the list of movies for that year
                    List<WebElement> movieRows = driver.findElements(By.xpath("//tr[@class='film']"));
                    WebDriverWait wait =new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@class='film']")));
                    // Limit to top 5 movies
                    for (int i = 0; i < Math.min(movieRows.size(), 5); i++) {
                        WebElement movieRow = movieRows.get(i);
    
                        // Extract movie details
                        String title = movieRow.findElement(By.xpath(".//td[@class='film-title']")).getText().trim();
                        System.out.println("Movie Title: "+title);
                        String nominations = movieRow.findElement(By.xpath(".//td[@class='film-nominations']")).getText().trim();
                        System.out.println("Movie Nomination: "+nominations);
                        String awards = movieRow.findElement(By.xpath(".//td[@class='film-awards']")).getText().trim();
                        
                        // Check if the movie was the Best Picture winner
                        boolean isWinner = movieRow.findElement(By.xpath(".//td[@class='film-best-picture']")).findElements(By.tagName("i")).size() > 0;
    
                        // Create a map to hold each movie's details
                        Map<String, Object> movieDetails = new HashMap<>();
                        movieDetails.put("epoch_time_of_scrape", epochTime);
                        movieDetails.put("year", year);
                        movieDetails.put("title", title);
                        movieDetails.put("nominations", nominations);
                        movieDetails.put("awards", awards);
                        movieDetails.put("is_winner", isWinner);
    
                        // Add the movie details to the list
                        movieData.add(movieDetails);
                    }
                    System.out.println("Movie Data: "+movieData);
                    // Break out of retry loop since scraping succeeded
                    successfulScrape = true;
                    break;
                } catch (Exception e) {
                    System.out.println("Error while scraping data for year " + year + ": " + e.getMessage());
                    if (attempt < maxRetries - 1) {
                        System.out.println("Retrying... Attempt " + (attempt + 1));
                        Thread.sleep(waitBetweenRetries);
                    } else {
                        System.out.println("Failed to scrape data for year " + year + " after " + maxRetries + " attempts.");
                    }
                }
            }
    
            // If scraping failed after retries, log it
            if (!successfulScrape) {
                System.out.println("Could not scrape data for year: " + year);
            }
        }
    
        // Serialize the scraped data to a JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        File outputDir = new File("./output");
    
        // Create the directory if it doesn't exist
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    
        File outputFile = new File(outputDir, "oscar-winner-data.json");
        objectMapper.writeValue(outputFile, movieData);
    
        // Assert the file is created and not empty
        Assert.assertTrue(outputFile.exists(), "The JSON file was not created.");
        Assert.assertTrue(outputFile.length() > 0, "The JSON file is empty.");
    
        System.out.println("Data has been saved to output/oscar-winner-data.json");
    }
    
}
