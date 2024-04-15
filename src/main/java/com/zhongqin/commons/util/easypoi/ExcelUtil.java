package com.zhongqin.commons.util.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerDefaultImpl;
import com.zhongqin.commons.exception.CustomException;
import com.zhongqin.commons.util.easypoi.styler.CommonExcelExportStyler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static cn.afterturn.easypoi.util.PoiCellUtil.getCellValue;

/**
 * @author Kevin
 * @date 2021/12/23 17:10
 * 基本导出导入工具类
 */
public class ExcelUtil {

    /**
     * excel 导出
     *
     * @param list           数据
     * @param title          标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param fileName       文件名称
     * @param isCreateHeader 是否创建表头
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list      数据
     * @param title     标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param fileName  文件名称
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName,
                                   HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF));
    }

    /**
     * excel 导出
     *
     * @param list         数据
     * @param pojoClass    pojo类型
     * @param fileName     文件名称
     * @param response
     * @param exportParams 导出参数
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams,
                                   HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    /**
     * excel 导出
     *
     * @param list     数据
     * @param fileName 文件名称
     * @param response
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        defaultExport(list, fileName, response);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list         数据
     * @param pojoClass    pojo类型
     * @param fileName     文件名称
     * @param response
     * @param exportParams 导出参数
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response,
                                      ExportParams exportParams) {
        Workbook workbook = buildExportExcelWorkbook(exportParams, pojoClass, list);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list     数据
     * @param fileName 文件名称
     * @param response
     */
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出到本地文件
     *
     * @param list          数据
     * @param pojoClass     pojo类型
     * @param fileLocalPath 文件路径
     * @param fileName      文件名称
     * @param exportParams  导出参数
     */
    public static void exportExcelToLocal(List<?> list, Class<?> pojoClass, String fileLocalPath, String fileName,
                                          ExportParams exportParams) {
        Workbook workbook = buildExportExcelWorkbook(exportParams, pojoClass, list);
        writeToLocal(fileLocalPath, fileName, workbook);
    }

    /**
     * excel 导出到InputStream
     *
     * @param list         数据
     * @param pojoClass    pojo类型
     * @param exportParams 导出参数
     * @return
     */
    public static InputStream exportExcelToInputStream(List<?> list, Class<?> pojoClass, ExportParams exportParams) {
        Workbook workbook = buildExportExcelWorkbook(exportParams, pojoClass, list);
        try (ByteArrayOutputStream dstStream = new ByteArrayOutputStream()) {
            workbook.write(dstStream);
            byte[] bytes = dstStream.toByteArray();
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes)) {
                return byteStream;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Workbook buildExportExcelWorkbook(ExportParams exportParams, Class<?> pojoClass, Collection<?> list) {
        try {
            if (exportParams.getStyle() == null || exportParams.getStyle() == ExcelExportStylerDefaultImpl.class) {
                exportParams.setStyle(CommonExcelExportStyler.class);
            }
        } catch (Exception ignored) {}
        return ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
    }

    /**
     * workbook 写入到本地文件夹
     *
     * @param fileLocalPath 文件路径
     * @param fileName      文件名称
     * @param workbook      workbook
     */
    private static void writeToLocal(String fileLocalPath, String fileName, Workbook workbook) {
        try (OutputStream outputStream = new FileOutputStream(fileLocalPath + fileName)) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载
     *
     * @param fileName 文件名称
     * @param response
     * @param workbook excel数据
     */
    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * excel 导入并保存文件到本地
     *
     * @param filePath   excel文件路径
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows,
                                          Class<T> pojoClass, String saveUrl) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl(saveUrl);
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new CustomException("模板不能为空");
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param filePath   excel文件路径
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new CustomException("模板不能为空");
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) {
        return importExcel(file, 0, 1, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param file       excel文件
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        verificationExcelHeadLine(0, 0, pojoClass, file);
        return importExcel(file, titleRows, headerRows, false, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param file       上传的文件
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param needVerfiy 是否检验excel内容
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, boolean needVerfiy,
                                          Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        try {
            return importExcel(file.getInputStream(), titleRows, headerRows, needVerfiy, pojoClass);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> pojoClass) {
        return importExcel(inputStream, 0, 1, false, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param titleRows   标题行
     * @param headerRows  表头行
     * @param needVerify  是否检验excel内容
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows,
                                          boolean needVerify, Class<T> pojoClass) {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl("/excel/");
        params.setNeedSave(true);
        params.setNeedVerify(needVerify);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new CustomException("excel文件不能为空");
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    /**
     * 验证用户上传的模板是否是最新的模板
     *
     * @param headerRows
     * @param sheetAt
     * @param pojoClass
     * @param file
     * @param <T>
     */
    public static <T> void verificationExcelHeadLine(Integer headerRows, Integer sheetAt, Class<T> pojoClass, MultipartFile file) {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx") &&
                !fileName.endsWith("XLS") && !fileName.endsWith("XLSX")) {
            throw new CustomException("Excel file type error");
        }
        Map<String, String> map = new HashMap<>(16);
        Field[] fields = pojoClass.getDeclaredFields();
        for (Field field : fields) {
            boolean annotationPresent = field.isAnnotationPresent(Excel.class);
            if (annotationPresent) {
                // 获取注解值
                String name = field.getAnnotation(Excel.class).name();
                map.put(name, field.getName());
            }
        }
        String keyError = "Error";
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException ex) {
            try {
                workbook = new HSSFWorkbook(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Sheet sheet = workbook.getSheetAt(sheetAt);
        Row row = sheet.getRow(headerRows);
        int lastCellNum = row.getLastCellNum();
        if (lastCellNum < NumberUtils.INTEGER_ZERO) {
            throw new CustomException("Header line error, please download the latest template!");
        }
        String[] valueList = new String[lastCellNum];
        for (int index = 0; index < lastCellNum; index++) {
            String value = getCellValue(row.getCell(index));
            valueList[index] = value;
        }
        if (!Arrays.asList(valueList).contains(keyError)) {
            map.keySet().removeIf(keyError::equals);
        }
        String[] columnName = map.keySet().toArray(new String[0]);
        Arrays.sort(valueList);
        Arrays.sort(columnName);
        if (!Arrays.equals(valueList, columnName)) {
            throw new CustomException("Header line error, please download the latest template!");
        }
    }
}
