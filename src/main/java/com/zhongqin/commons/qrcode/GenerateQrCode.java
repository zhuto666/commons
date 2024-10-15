package com.zhongqin.commons.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.zhongqin.commons.exception.CustomException;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * 生成二维码
 *
 * @author Kevin
 * @date 2023/4/6 13:43 星期四
 */
public class GenerateQrCode {

    /**
     * 生成二维码
     *
     * @param text       扫码后的结果
     * @param qrcodeSize 二维码尺寸
     * @param format     文件格式
     * @param filePath   文件保存地址
     */
    @SneakyThrows
    public static void generateQrCodeImage(String text, int qrcodeSize, String format, String filePath) {
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(getBitMatrix(text, qrcodeSize, format, filePath, getEncodeHintType()), format, path);
    }

    /**
     * 生成支付二维码返回流
     *
     * @param url  二维码内容
     * @param logo 公司logo
     * @return 输出流
     */
    public static InputStream generatePayQrCode(String url, String logo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(GenerateQrCode.getQrCodeImage(url, 1000, Boolean.TRUE, logo, 0x000000), "jpg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 生成二维码
     *
     * @param text       扫码后的结果
     * @param qrcodeSize 二维码尺寸
     * @param format     文件格式
     * @param filePath   文件保存地址
     * @param images     插入图片
     */

    public static String generateQrCodeImage(String text, int qrcodeSize, String format, String filePath, String images) {
        Path path = FileSystems.getDefault().getPath(filePath);
        BitMatrix bitMatrix = getBitMatrix(text, qrcodeSize, format, filePath, getEncodeHintType());
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int tempHeight = height;
        if (StringUtils.hasText("")) {
            tempHeight = tempHeight + 12;
        }
        BufferedImage image = new BufferedImage(width, tempHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFFFF);
            }
        }
        // 判断是否添加logo
        if (StringUtils.hasText(images)) {
            insertLogoImage(image, qrcodeSize, images);
        }
        File outputFile = new File(filePath);
        try {
            ImageIO.write(image, format, outputFile);
        } catch (IOException e) {
            throw new CustomException("文件写入失败" + e.getMessage());
        }
        return filePath;
    }

    private static HashMap<EncodeHintType, Object> getEncodeHintType() {
        HashMap<EncodeHintType, Object> hints = new HashMap<>(16);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // L 水平 7%的字码可被修正
        // M 水平 15%的字码可被修正
        // Q 水平 25%的字码可被修正
        // H 水平 30%的字码可被修正
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        return hints;
    }

    private static HashMap<EncodeHintType, Object> getEncodeHintType(String characterSet, ErrorCorrectionLevel errorCorrectionLevel, int margin) {
        HashMap<EncodeHintType, Object> hints = new HashMap<>(16);
        hints.put(EncodeHintType.CHARACTER_SET, characterSet);
        // L 水平 7%的字码可被修正
        // M 水平 15%的字码可被修正
        // Q 水平 25%的字码可被修正
        // H 水平 30%的字码可被修正
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, margin);
        return hints;
    }

    @SneakyThrows
    private static BitMatrix getBitMatrix(String text, int qrcodeSize) {
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        // 定义内容字符集的编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 定义纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        return new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, qrcodeSize, qrcodeSize, hints);
    }

    @SneakyThrows
    private static BitMatrix getBitMatrix(String text, int qrcodeSize, String format, String filePath, HashMap<EncodeHintType, Object> hints) {
        return new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, qrcodeSize, qrcodeSize, hints);
    }

    @SneakyThrows
    public static void qenerateQrCode(String contents, int qrcodeSize, String format, boolean needLogo, String bottomText, String images, int backgroundColor, HttpServletResponse response) {
        BufferedImage bufferedImage = GenerateQrCode.getQrCodeImage(contents, qrcodeSize, needLogo, bottomText, images, backgroundColor);
        response.setContentType("image/png");
        // 把图片写给浏览器
        ImageIO.write(bufferedImage, format, response.getOutputStream());
    }

    @SneakyThrows
    public static void qenerateQrCode(String contents, int qrcodeSize, String format, boolean needLogo, String images, int backgroundColor, HttpServletResponse response) {
        BufferedImage bufferedImage = GenerateQrCode.getQrCodeImage(contents, qrcodeSize, needLogo, images, backgroundColor);
        response.setContentType("image/png");
        // 把图片写给浏览器
        ImageIO.write(bufferedImage, format, response.getOutputStream());
    }

    @SneakyThrows
    public static void qenerateQrCode(String contents, int qrcodeSize, String format, String bottomText, int backgroundColor, HttpServletResponse response) {
        BufferedImage bufferedImage = GenerateQrCode.getQrCodeImage(contents, qrcodeSize, bottomText, backgroundColor);
        response.setContentType("image/png");
        // 把图片写给浏览器
        ImageIO.write(bufferedImage, format, response.getOutputStream());
    }

    @SneakyThrows
    private static BufferedImage getQrCodeImage(String text, int qrcodeSize, boolean needLogo, String bottomText, String images, int backgroundColor) {
        if (text == null) {
            throw new RuntimeException("未包含任何信息");
        }
        BitMatrix bitMatrix = getBitMatrix(text, qrcodeSize);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int tempHeight = height;
        if (StringUtils.hasText(bottomText)) {
            tempHeight = tempHeight + 12;
        }
        BufferedImage image = new BufferedImage(width, tempHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? backgroundColor : 0xFFFFFFFF);
            }
        }
        // 判断是否添加logo
        if (needLogo) {
            insertLogoImage(image, qrcodeSize, images);
        }
        // 判断是否添加底部文字
        if (StringUtils.hasText(bottomText)) {
            addFontImage(image, bottomText, qrcodeSize);
        }
        return image;
    }

    private static BufferedImage getQrCodeImage(String text, int qrcodeSize, boolean needLogo, String images, int backgroundColor) {
        if (text == null) {
            throw new RuntimeException("未包含任何信息");
        }
        BitMatrix bitMatrix = getBitMatrix(text, qrcodeSize);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? backgroundColor : 0xFFFFFFFF);
            }
        }
        // 添加logo
        if (needLogo) {
            insertLogoImage(image, qrcodeSize, images);
        }
        return image;
    }

    private static BufferedImage getQrCodeImage(String text, int qrcodeSize, String bottomText, int backgroundColor) {
        if (text == null) {
            throw new RuntimeException("未包含任何信息");
        }
        BitMatrix bitMatrix = getBitMatrix(text, qrcodeSize);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int tempHeight = height;
        if (StringUtils.hasText(bottomText)) {
            tempHeight = tempHeight + 12;
        }
        BufferedImage image = new BufferedImage(width, tempHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? backgroundColor : 0xFFFFFFFF);
            }
        }
        // 添加底部文字
        addFontImage(image, bottomText, qrcodeSize);
        return image;
    }

    /**
     * 解析二维码
     *
     * @param filePath 文件所在地
     */
    @SneakyThrows
    public static String parseQrCodeImage(String filePath) {
        QRCodeReader qrCodeReader = new QRCodeReader();
        File file = new File(filePath);
        BufferedImage bufferedImage = ImageIO.read(file);
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
        Binarizer binarizer = new HybridBinarizer(luminanceSource);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        return qrCodeReader.decode(binaryBitmap).getText();
    }

    /**
     * 插入logo图片
     *
     * @param source     二维码
     * @param qrcodeSize 二维码尺寸
     * @param images     logo图片
     */
    @SneakyThrows
    private static void insertLogoImage(BufferedImage source, int qrcodeSize, String images) {
        // 默认logo放于resource/static/image目录下
        ClassPathResource classPathResource = new ClassPathResource("static/image/" + images);
        InputStream inputStream = classPathResource.getInputStream();
        if (inputStream.available() == 0) {
            return;
        }
        Image src = ImageIO.read(inputStream);
        int width = qrcodeSize / 4;
        int height = qrcodeSize / 4;
        Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        // 绘制缩小后的图
        g.drawImage(image, 0, 0, null);
        g.dispose();
        src = image;
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (qrcodeSize - width) / 2;
        int y = (qrcodeSize - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 插入底部文字
     *
     * @param source      二维码
     * @param declareText 二维码尺寸
     * @param qrcodeSize  图片尺寸
     */
    private static void addFontImage(BufferedImage source, String declareText, int qrcodeSize) {
        // 生成image
        int defineHeight = 20;
        BufferedImage textImage = new BufferedImage(qrcodeSize, defineHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) textImage.getGraphics();
        //开启文字抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, qrcodeSize, defineHeight);
        g2.setPaint(Color.BLACK);
        Graphics2D graph = source.createGraphics();
        // 开启文字抗锯齿
        graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 添加image
        int width = textImage.getWidth(null);
        int height = textImage.getHeight(null);
        graph.drawImage(textImage, 0, qrcodeSize - 8, width, height, Color.WHITE, null);
        graph.dispose();
    }

}
