package com.zhongqin.commons.util;

import com.zhongqin.commons.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

/**
 * @author Kevin
 * @version 1.0
 * @date 2025/1/16 13:53 星期四
 */
@Slf4j
public class GoogleBrowserUtil {

    /**
     * 打开浏览器
     *
     * @param localityDriverPath  驱动路径
     * @param localityBrowserPath 浏览器路径
     * @param reqUrl              请求url
     * @return WebDriver
     */
    public static WebDriver openBrowser(String localityDriverPath, String localityBrowserPath, String reqUrl) {
        try {
            System.out.println("localityDriverPath" + localityDriverPath);
            System.out.println("localityBrowserPath" + localityBrowserPath);
            // 需配置
            System.setProperty("webdriver.chrome.driver", localityDriverPath);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--no-sandbox");
            // 谷歌文档提到需要加上这个属性来规避bug
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-zygote");
            chromeOptions.addArguments("'user-agent=%s'%user_agent");
            log.info("初始化chromeOptions");
            // 需配置
            chromeOptions.setBinary(localityBrowserPath);
            // 3、初始化一个浏览器实例
            WebDriver webDriver = new ChromeDriver(chromeOptions);
            log.info("初始化webDriver");
            // 全屏化
            webDriver.manage().window().maximize();
            // webDriver.manage().window().setSize(new Dimension(1366,768));
            log.info("最大化");
            log.info("设置reqUrl");
            webDriver.get(reqUrl);
            log.info("跳转url");
            return webDriver;
        } catch (Exception e) {
            throw new CustomException("chromedriver配置异常：" + e.getMessage());
        }
    }


    /**
     * 获取最新的窗口句柄
     *
     * @param webDriver webDriver
     */
    public static void getNewWindowHandle(WebDriver webDriver) {
        // 获取当前窗口句柄
        String currentWindow = webDriver.getWindowHandle();
        // 获取所有窗口句柄
        Set<String> handles = webDriver.getWindowHandles();
        Iterator<String> it = handles.iterator();
        String lastWindowHandle = "";
        while (it.hasNext()) {
            String windowHandle = it.next();
            // 判断是否是原来的界面
            if (Objects.equals(currentWindow, windowHandle)) {
                continue;
            } else {
                // 切换到新窗口
                lastWindowHandle = windowHandle;
            }
        }
        webDriver.switchTo().window(lastWindowHandle);
    }

    /**
     * 关闭其他窗口
     *
     * @param webDriver webDriver
     */
    public static void closeOtherWindows(WebDriver webDriver) {
        // 获取当前窗口句柄
        String currentWindow = webDriver.getWindowHandle();
        // 获取所有窗口句柄
        Set<String> handles = webDriver.getWindowHandles();
        Iterator<String> it = handles.iterator();
        List<String> closeList = new ArrayList<>();
        while (it.hasNext()) {
            String windowHandle = it.next();
            // 判断是否是原来的界面
            if (currentWindow.equals(windowHandle)) {
                continue;
            }
            closeList.add(windowHandle);
        }
        for (String string : closeList) {
            // 切换到新窗口
            webDriver.switchTo().window(string);
            // 关闭页面
            webDriver.close();
            // 切换到新窗口
            webDriver.switchTo().window(currentWindow);
        }
    }

}
