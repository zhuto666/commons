package com.zhongqin.commons.util.easypoi.styler;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import cn.afterturn.easypoi.excel.export.styler.AbstractExcelExportStyler;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.*;

/**
 * <p> 自定义通用导出Excel，数值类型字段样式 </p>
 *
 * @author PaulYang
 * @since 2023-02-13
 */
public class CommonExcelExportStyler extends AbstractExcelExportStyler implements IExcelExportStyler {

    protected CellStyle numberCellStyle;
    protected CellStyle textCellStyle;

    public CommonExcelExportStyler(Workbook workbook) {
        super.createStyles(workbook);
        this.numberCellStyle = workbook.createCellStyle();
        this.textCellStyle = workbook.createCellStyle();
        this.textCellStyle.setAlignment(HorizontalAlignment.LEFT);
        this.textCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setWrapText(true);
        return titleStyle;
    }

    @Override
    public CellStyle stringSeptailStyle(Workbook workbook, boolean isWarp) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(STRING_FORMAT);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }

    @Override
    public CellStyle getHeaderStyle(short color) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        titleStyle.setFont(font);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return titleStyle;
    }

    @Override
    public CellStyle stringNoneStyle(Workbook workbook, boolean isWarp) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(STRING_FORMAT);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }

    @Override
    public CellStyle getStyles(boolean noneStyler, ExcelExportEntity entity) {
        try {
            if (entity != null && BaseEntityTypeConstants.DOUBLE_TYPE == entity.getType()) {
                return numberCellStyle;
            }
            if (entity != null && BaseEntityTypeConstants.STRING_TYPE == entity.getType()) {
                return textCellStyle;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getStyles(noneStyler, entity);
    }

    @Override
    public CellStyle getStyles(Cell cell, int dataRow, ExcelExportEntity entity, Object obj, Object data) {
        return getStyles(true, entity);
    }
}
