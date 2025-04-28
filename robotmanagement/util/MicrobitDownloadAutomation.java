package com.example.robotmanagement.util;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class MicrobitDownloadAutomation {
    public static void deployCodeToMicrobit(String code, String name) {
        // Set path to WebDriver
        System.setProperty("webdriver.chrome.driver", "robotmanagement\\src\\main\\resources\\chromedriver-win64\\chromedriver.exe");

        Path path = Paths.get(System.getProperty("user.home")); // Get C:\Users\YourUser
        String secondFolder = path.getName(1).toString(); // Get "meste"

        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");  // Optional: Uncomment to run in headless mode.
        //options.addArguments("--disable-gpu"); // Sometimes helps with headless mode.
        //options.addArguments("disk-cache-dir=robotmanagement\\src\\main\\resources\\chromedriver-win64\\Cache_Data");
        options.addArguments("user-data-dir=C:\\Users\\"+ secondFolder +"\\AppData\\Local\\Google\\Chrome\\User Data");
        options.addArguments("profile-directory=Default");  // Change "Default" to your actual profile name
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the MakeCode Microbit editor
            driver.get("https://makecode.microbit.org");

            // Wait for the blocking element to disappear
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            //intru in js la cod
            WebElement downloadButton;

            // Focus on the body element with class "main"
            WebElement bodyElement = driver.findElement(By.cssSelector("body.main"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", bodyElement);
            // Copy the file to clipboard
            String filePath = "robotmanagement\\src\\main\\resources\\chromedriver-win64\\open.hex";
            copyFileToClipboard(filePath);
            bodyElement.sendKeys(Keys.CONTROL, "v"); // Simulates pasting the clipboard content

            Thread.sleep(100);
//
//            downloadButton = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.cssSelector(".ui.card.link.buttoncard.newprojectcard")));
//            downloadButton.click();
//
//            // Wait for the element to be visible
//            WebElement inputField = wait.until(
//                    ExpectedConditions.visibilityOfElementLocated(By.id("projectNameInput"))
//            );
//            inputField.sendKeys(name);
//            Thread.sleep(500);
//            // Wait for the button to be clickable
//            WebElement approveButton = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.cssSelector(".ui.button.icon.icon-and-text.approve.icon.right.labeled.approve.positive"))
//            );
//            approveButton.click();

            try {
                downloadButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector(".icon.close.remove.circle"))
                );
                downloadButton.click();
            } catch (org.openqa.selenium.TimeoutException e) {
                System.out.println("Element not found or clickable, skipping click.");
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                System.out.println("Element is intercepted, skipping click.");
            }
//
//            //javascript switch
//            downloadButton = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.cssSelector(".ui.item.link.base-menuitem.javascript-menuitem")));
//            downloadButton.click();

            // Wait for the element to be visible
            WebElement monacoEditor = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".view-lines.monaco-mouse-cursor-text"))
            );

            // Find the first "view-line" div
            monacoEditor.findElement(By.cssSelector(".view-line"));

            Thread.sleep(100);

//            for (int i=0; i<10; i++) {
//                // JavaScript to select all text, trigger the 'cut' event, and then insert the new text
//                String script =
//                        "let textArea = document.querySelector('.inputarea.monaco-mouse-cursor-text');" + // Select the textarea
//                                "textArea.focus();" + // Focus the text area
//                                "document.execCommand('selectAll');" + // Select all text
//                                "document.execCommand('cut');"; // Trigger the 'cut' event
//                ((JavascriptExecutor) driver).executeScript(script);
//            }

            String script = "let textArea = document.querySelector('.inputarea.monaco-mouse-cursor-text');" +
                    "textArea.focus();" + // Focus the text area
                    "textArea.value = `" + code + "`;" + // Insert the multiline text
                    "textArea.dispatchEvent(new Event('input'));"; // Trigger the input event
            ((JavascriptExecutor) driver).executeScript(script);
// Optional: Wait for a moment to ensure the input is processed
            Thread.sleep(200);  // Wait for a second
//
//            WebElement block = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='monacoEditorToolbox']//div[@class='blocklyTreeRoot']//div[@role='tree']//div[@role='treeitem']//div[@role='button' and contains(@class, 'blocklyTreeRow') and @data-ns='addpackage' and .//span[@class='blocklyTreeLabel' and text()='Extensions']]"))
//            );
//            block.click();
//
//            Thread.sleep(2000);
//            String inputText = "https://github.com/DFRobot/pxt-motor";
//            inputField = driver.findElement(By.cssSelector(".common-input.has-icon[role='textbox']"));
//            // Use JavaScript to focus, clear, set the value, and simulate a click
//            script = "document.body.click();"+  // Simulate click on the body to remove selection
//                    "arguments[0].focus();" +  // Focus the input field
//                            "arguments[0].value = arguments[1];" + // Set the new value
//                            "arguments[0].dispatchEvent(new Event('input'));"+
//                    "var event = new KeyboardEvent('keydown', {key: 'Enter'}); arguments[0].dispatchEvent(event);";  // Simulate Enter key press
//            ((JavascriptExecutor) driver).executeScript(script, inputField, inputText);
//            Thread.sleep(200);
//            Actions actions = new Actions(driver);
//            actions.sendKeys(Keys.ENTER).perform();  // Simulate pressing the Enter key
//            Thread.sleep(100000);  // Wait for a second

            // Find the input field by its ID or other attributes
            WebElement inputField = driver.findElement(By.id("fileNameInput2"));
            inputField.clear();
            inputField.sendKeys(name);

            //cele trei puncte
            downloadButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".icon.ellipsis.horizontal"))
            );
            downloadButton.click();
            //download
            downloadButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".icon.xicon.file-download.icon-and-text"))
            );
            downloadButton.click();
            System.out.println("Download button clicked successfully!");

            // Keep browser open for 5 seconds before closing
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }
    public static void openCode(String name) {
        // Set path to WebDriver
        System.setProperty("webdriver.chrome.driver", "robotmanagement\\src\\main\\resources\\chromedriver-win64\\chromedriver.exe");

        Path path = Paths.get(System.getProperty("user.home")); // Get C:\Users\YourUser
        String secondFolder = path.getName(1).toString(); // Get "meste"

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\" + secondFolder + "\\AppData\\Local\\Google\\Chrome\\User Data");
        options.addArguments("profile-directory=Default");  // Change "Default" to your actual profile name
        WebDriver driver = new ChromeDriver(options);

        try {
            // Open the MakeCode Microbit editor
            driver.get("https://makecode.microbit.org");

            //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

            List<WebElement> carouselItems = driver.findElements(By.cssSelector(".carouselcontainer .carouselbody .carouselitem"));

            for (WebElement item : carouselItems) {
                // Get the header text inside each carousel item
                WebElement header = item.findElement(By.cssSelector(".content .header"));

                // Check if the header text matches the desired name
                if (header.getText().equals(name)) {
                    // Click on the carousel item
                    item.click();
                    break; // Exit the loop once the item is found and clicked
                }
            }

            Thread.sleep(10000);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to copy a file to the clipboard
    private static void copyFileToClipboard(String filePath) {
        File file = new File(filePath);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new FileTransferable(file), null);
    }

    // Custom Transferable for File Copying
    static class FileTransferable implements Transferable {
        private final File file;

        public FileTransferable(File file) {
            this.file = file;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.javaFileListFlavor);
        }

        @NotNull
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return java.util.Collections.singletonList(file);
        }
    }
}
