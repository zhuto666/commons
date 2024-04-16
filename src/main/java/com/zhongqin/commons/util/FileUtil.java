package com.zhongqin.commons.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin
 * @date 2020/11/24 14:11
 */
@Slf4j
public class FileUtil {

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return
     */
    public static String readFile(String filePath, String fileName) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        BufferedInputStream inStream = null;
        File file = new File(filePath + File.separator + fileName);
        try {
            // 创建BufferedInputStream 对象
            inStream = new BufferedInputStream(new FileInputStream(file));
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytesRead = inStream.read(buffer)) != -1) {
                // 将读取的字节转为字符串对象
                String chunk = new String(buffer, 0, bytesRead);
                stringBuffer.append(chunk);
            }
            return stringBuffer.toString();
        } catch (Exception ex) {
            log.error("readFile file failure!", ex);
        } finally {
            IOUtils.closeQuietly(inStream);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return
     */
    public static boolean deleteFile(String filePath, String fileName) {
        File file = new File(filePath + File.separator + fileName);
        return file.delete();
    }

    /**
     * 移动文件
     *
     * @param originFilePath 源文件地址
     * @param originFileName 源文件名称
     * @param moveDirectory  移动目录
     * @return
     */
    public static boolean moveFile(String originFilePath, String originFileName, String moveDirectory) {
        try {
            // 源文件
            File originFile = new File(originFilePath + File.separator + originFileName);
            // 目标目录
            File endDirection = new File(moveDirectory);
            // 如果目标目录不存在,则进行创建
            if (!endDirection.exists()) {
                endDirection.mkdirs();
            }
            // 目标目录+源文件名称
            File endFile = new File(endDirection + File.separator + originFile.getName());
            // 移动文件
            return originFile.renameTo(endFile);
        } catch (Exception ex) {
            log.error("readFile file failure!", ex);
        }
        return false;
    }

    public static void moveFiles(String sourceFolderPath, String targetFolderPath) {
        File sourceFolder = new File(sourceFolderPath);
        // 获取文件夹内的所有文件
        File[] files = sourceFolder.listFiles();
        // 遍历文件夹内的所有文件
        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是文件夹，则递归调用moveFiles方法继续遍历文件夹内的文件
                moveFiles(file.getAbsolutePath(), targetFolderPath);
            } else {
                // 如果是文件，则调用moveFile方法移动文件到目标位置
                moveFile(file, targetFolderPath);
            }
        }
    }

    public static void moveFile(File file, String targetFolderPath) {
        try {
            // 目标文件的路径
            String targetFilePath = targetFolderPath + File.separator + file.getName();
            File targetFile = new File(targetFilePath);
            // 使用Java的文件操作方法将文件移动到目标位置
            Files.move(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved file: " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某个目录下所有直接下级文件,不包括目录下的子目录的下的文件
     *
     * @param filePath
     * @return
     */
    public static List<String> getFiles(String filePath) {
        List<String> fileList = new ArrayList<>();
        File file = new File(filePath);
        File[] tempList = file.listFiles();
        assert tempList != null;
        for (File fil : tempList) {
            if (fil.isFile()) {
                // 文件名，不包含路径
                fileList.add(fil.getName());
            }
        }
        return fileList;
    }

    /**
     * 获取当前目录下全部文件
     */
    public static List<File> getAllFiles(String path) {
        List<File> fileList = Lists.newArrayList();
        // 检查省定路径是否为目录
        File directory = new File(path);
        if (!directory.isDirectory()) {
            return fileList;
        }
        // 获歌目录下的所有文件和子目录
        File[] files = directory.listFiles();
        // 遍历所有文件和子目录
        assert files != null;
        for (File file : files) {
            // 如果是文件则派加资文件列表中
            if (file.isFile()) {
                fileList.add(file);
            }
            //如果是昌最则诺归调用这方法
            if (file.isDirectory()) {
                fileList.addAll(getAllFiles(file.getAbsolutePath()));
            }
        }
        return fileList;
    }

    /**
     * 复制文件夹
     *
     * @param srcFolderPath
     * @param destFolderPath
     */
    public static void copyFolder(String srcFolderPath, String destFolderPath) {
        // 封装数据源目录
        File srcFolder = new File(srcFolderPath);
        // 封装目的地目录
        File destFolder = new File(destFolderPath);
        // 如果目的地文件夹不存在就创建
        if (!destFolder.exists()) {
            destFolder.mkdir();
        }
        // 获取数据源目录下的所有文件的File数组
        File[] fileArray = srcFolder.listFiles();

        // 遍历File数组得到每一个File对象
        for (File file : fileArray) {
            // 获取文件名
            String name = file.getName();
            // 获取文件路径
            File newFile = new File(destFolder, name);
            try {
                copyFile(file, newFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void copyFile(File file, File newFile) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
        byte[] bys = new byte[1024];
        int len = 0;
        while ((len = bis.read(bys)) != -1) {
            bos.write(bys, 0, len);
        }
        bos.close();
        bis.close();
    }

}
