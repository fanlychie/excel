package org.fanlychie.jexcel.write;

/**
 * 工作表
 * Created by fanlychie on 2017/3/4.
 */
public class WritableSheet {

    /**
     * 工作表名称
     */
    private String name;

    /**
     * 单元格宽度
     */
    private int cellWidth;

    /**
     * 填充工作表的数据类型
     */
    private Class<?> dataType;

    /**
     * 标题行样式
     */
    private RowStyle titleRowStyle;

    /**
     * 主体行样式
     */
    private RowStyle bodyRowStyle;

    /**
     * 脚部行样式
     */
    private RowStyle footerRowStyle;

    /**
     * 构建实例
     *
     * @param dataType 填充工作表的数据类型
     */
    public WritableSheet(Class<?> dataType) {
        this.dataType = dataType;
    }

    /**
     * 设置工作表的名称
     *
     * @param name 工作表的名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置单元格宽度
     *
     * @param cellWidth 单元格宽度
     */
    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * 设置标题行样式
     *
     * @param titleRowStyle 标题行样式
     */
    public void setTitleRowStyle(RowStyle titleRowStyle) {
        this.titleRowStyle = titleRowStyle;
    }

    /**
     * 设置主体行样式
     *
     * @param bodyRowStyle 主体行样式
     */
    public void setBodyRowStyle(RowStyle bodyRowStyle) {
        this.bodyRowStyle = bodyRowStyle;
    }

    /**
     * 获取工作表名称
     *
     * @return 返回工作表名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取单元格宽度
     *
     * @return 返回单元格宽度
     */
    public int getCellWidth() {
        return cellWidth * 256 + 184;
    }

    /**
     * 获取填充工作表的数据类型
     *
     * @return 返回填充工作表的数据类型
     */
    public Class<?> getDataType() {
        return dataType;
    }

    /**
     * 获取标题行样式
     *
     * @return 返回标题行样式
     */
    public RowStyle getTitleRowStyle() {
        return titleRowStyle;
    }

    /**
     * 获取主体行样式
     *
     * @return 返回主体行样式
     */
    public RowStyle getBodyRowStyle() {
        return bodyRowStyle;
    }

    /**
     * 获取脚部行样式
     *
     * @return 返回脚部行样式
     */
    public RowStyle getFooterRowStyle() {
        return footerRowStyle;
    }

    /**
     * 设置脚部行样式
     *
     * @param footerRowStyle 脚部行样式
     */
    public void setFooterRowStyle(RowStyle footerRowStyle) {
        this.footerRowStyle = footerRowStyle;
    }

}