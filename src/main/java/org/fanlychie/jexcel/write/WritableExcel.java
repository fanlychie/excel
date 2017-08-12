package org.fanlychie.jexcel.write;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.fanlychie.jexcel.annotation.AnnotationHandler;
import org.fanlychie.jexcel.annotation.CellField;
import org.fanlychie.jexcel.exception.ExcelCastException;
import org.fanlychie.jexcel.spec.Format;
import org.fanlychie.jexcel.spec.Sheet;
import org.fanlychie.jreflect.BeanDescriptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 可写的 Excel, 用于输出 Excel 文件
 * Created by fanlychie on 2017/3/4.
 */
public class WritableExcel {

    /**
     * 工作表
     */
    private WritableSheet writableSheet;

    /**
     * SXSSF 工作表
     */
    private SXSSFSheet sxssfSheet;

    /**
     * SXSSF 工作薄
     */
    private SXSSFWorkbook sxssfWorkbook;

    /**
     * 单元格注解字段列表
     */
    private List<CellField> cellFields;

    /**
     * 布尔值字符串映射表
     */
    private Map<Boolean, String> booleanStringMapping;

    /**
     * 工作表计数
     */
    private int sheetCount = 1;

    /**
     * 构建实例
     *
     * @param writableSheet 工作表
     */
    public WritableExcel(WritableSheet writableSheet) {
        this.writableSheet = writableSheet;
        this.sxssfWorkbook = new SXSSFWorkbook();
        if (writableSheet.getDataType() == null) {
            throw new IllegalArgumentException("dataType can not be null");
        }
        this.cellFields = AnnotationHandler.parseClass(writableSheet.getDataType());
    }

    /**
     * 填充数据, 当数据列表为空时, 输出一个除了标题无实体内容的文件
     *
     * @param data 数据列表
     * @return 返回当前对象
     */
    public WritableExcel addSheet(List<?> data) {
        try {
            String sheetName = Sheet.getName(sheetCount++, writableSheet.getName());
            sxssfSheet = sxssfWorkbook.createSheet(sheetName);
            buildExcelTitleRow();
            if (data != null && !data.isEmpty()) {
                RowStyle bodyRowStyle = writableSheet.getBodyRowStyle();
                int bodyIndex = bodyRowStyle.getIndex();
                for (Object item : data) {
                    buildExcelBodyRow(bodyRowStyle, bodyIndex++, item);
                }
            }
            return this;
        } catch (Throwable e) {
            throw new ExcelCastException(e);
        }
    }

    /**
     * 输出到文件
     *
     * @param pathname 文件路径名称
     */
    public void toFile(String pathname) {
        try {
            sxssfWorkbook.write(new FileOutputStream(new File(pathname)));
        } catch (Throwable e) {
            throw new ExcelCastException(e);
        }
    }

    /**
     * 输出到文件
     *
     * @param file 文件对象
     */
    public void toFile(File file) {
        OutputStream os = null;
        try {
            sxssfWorkbook.write(os);
        } catch (Throwable e) {
            throw new ExcelCastException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * 输出到输出流
     *
     * @param os 输出流
     */
    public void toStream(OutputStream os) {
        try {
            sxssfWorkbook.write(os);
        } catch (Throwable e) {
            throw new ExcelCastException(e);
        }
    }

    /**
     * 获取可写的工作表对象
     *
     * @return 返回可写的工作表对象
     */
    public WritableSheet getWritableSheet() {
        return writableSheet;
    }

    /**
     * 设置布尔值字符串映射表
     *
     * @param booleanStringMapping 布尔值字符串映射表
     * @return 返回当前对象
     */
    public WritableExcel booleanStringMapping(Map<Boolean, String> booleanStringMapping) {
        this.booleanStringMapping = booleanStringMapping;
        return this;
    }

    /**
     * 构建 Excel 标题行内容
     *
     * @throws Throwable
     */
    private void buildExcelTitleRow() throws Throwable {
        RowStyle rowStyle = writableSheet.getTitleRowStyle();
        SXSSFRow row = sxssfSheet.createRow(rowStyle.getIndex());
        row.setHeightInPoints(rowStyle.getHeight());
        for (CellField cellField : cellFields) {
            int index = cellField.getIndex();
            sxssfSheet.setColumnWidth(index, writableSheet.getCellWidth());
            SXSSFCell cell = row.createCell(index);
            cell.setCellStyle(rowStyle.getCellStyle(sxssfWorkbook, null));
            cell.setCellValue(cellField.getName());
        }
    }

    /**
     * 构建 Excel 主体行内容
     *
     * @param rowStyle  行样式
     * @param index     行索引
     * @param obj       填充单元格的对象数据
     * @throws Throwable
     */
    private void buildExcelBodyRow(RowStyle rowStyle, int index, Object obj) throws Throwable {
        SXSSFRow row = sxssfSheet.createRow(index);
        row.setHeightInPoints(rowStyle.getHeight());
        BeanDescriptor beanDescriptor = new BeanDescriptor(obj);
        for (CellField cellField : cellFields) {
            SXSSFCell cell = row.createCell(cellField.getIndex());
            CellStyle cellStyle = rowStyle.getCellStyle(sxssfWorkbook, cellField);
            cellStyle.setAlignment(cellField.getAlign().getValue());
            Object value = beanDescriptor.getValueByName(cellField.getField());
            Class<?> type = cellField.getType();
            String format = cellField.getFormat();
            setCellValue(cell, cellStyle, value, type, format);
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 设置单元格的值
     *
     * @param cell      单元格对象
     * @param cellStyle 单元格样式
     * @param value     值
     * @param type      值的类型
     * @param format    数据格式
     */
    private void setCellValue(SXSSFCell cell, CellStyle cellStyle, Object value, Class<?> type, String format) {
        if (value == null) {
            setCellStringValue(cell, cellStyle, "");
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            setCellBooleanValue(cell, cellStyle, value, format);
        } else if ((Number.class.isAssignableFrom(type) || type.isPrimitive()) && type != Byte.TYPE && type != Character.TYPE) {
            setCellDataFormat(cellStyle, format);
            cell.setCellValue(Double.parseDouble(value.toString()));
        } else if (type == Date.class) {
            cell.setCellValue((Date) value);
            setCellDataFormat(cellStyle, format);
        } else {
            setCellStringValue(cell, cellStyle, value.toString());
        }
    }

    /**
     * 设置单元格字符串值
     *
     * @param cell      单元格对象
     * @param cellStyle 单元格样式
     * @param value     值
     */
    private void setCellStringValue(SXSSFCell cell, CellStyle cellStyle, String value) {
        cell.setCellValue(value);
        setCellDataFormat(cellStyle, Format.STRING);
    }

    /**
     * 设置单元格布尔类型的值
     *
     * @param cell      单元格对象
     * @param cellStyle 单元格样式
     * @param value     值
     * @param format    数据格式
     */
    private void setCellBooleanValue(SXSSFCell cell, CellStyle cellStyle, Object value, String format) {
        boolean boolValue = (boolean) value;
        if (booleanStringMapping != null) {
            String booleanStr = booleanStringMapping.get(boolValue);
            if (booleanStr != null) {
                setCellStringValue(cell, cellStyle, booleanStr);
            } else {
                cell.setCellValue(boolValue);
                setCellDataFormat(cellStyle, format);
            }
        } else {
            cell.setCellValue(boolValue);
            setCellDataFormat(cellStyle, format);
        }
    }

    /**
     * 设置单元格数据格式
     *
     * @param cellStyle 单元格样式
     * @param format    数据格式
     */
    private void setCellDataFormat(CellStyle cellStyle, String format) {
        DataFormat formatter = sxssfWorkbook.createDataFormat();
        short dataFormat = formatter.getFormat(format);
        cellStyle.setDataFormat(dataFormat);
    }

}