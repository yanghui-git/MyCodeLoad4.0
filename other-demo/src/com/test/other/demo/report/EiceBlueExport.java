package com.test.other.demo.report;


import com.spire.xls.*;

/**
 * e-iceblue 往excel插入图片
 */
public class EiceBlueExport {

    public static void main(String[] args) {

        //创建Workbook实例
        Workbook workbook = new Workbook();
        //加载Excel文档
        workbook.loadFromFile("/Users/hui.yang/Desktop/excel演示/e-iceblue/演示1.xlsx");
        //获取第一张工作表
        Worksheet sheet = workbook.getWorksheets().get(0);
        //设置图表插入的位置
        ExcelPicture pic = sheet.getPictures().add(10, 1, "/Users/hui.yang/Desktop/excel演示/e-iceblue/演示图表1.png");
        //设置图片的宽度和高度
        pic.setWidth(600);
        pic.setHeight(120);
        //保存文档
        workbook.saveToFile("/Users/hui.yang/Desktop/excel演示/e-iceblue/演示插入结果.xlsx", ExcelVersion.Version2013);
        //文档转pdf
        transFileToPdf("/Users/hui.yang/Desktop/excel演示/e-iceblue/庞源在线-安全-安全周报-20230303130801.xlsx");
    }

    /**
     * 转pdf
     */
    public static void transFileToPdf(String fillPath) {
        Workbook wb = new Workbook();
        wb.loadFromFile(fillPath);
        wb.getWorksheets().get(0);
        ConverterSetting converterSetting = new ConverterSetting();
        converterSetting.setSheetFitToPage(true);
        wb.setConverterSetting(converterSetting);
        //调用方法保存为PDF格式
        wb.saveToFile("/Users/hui.yang/Desktop/excel演示/e-iceblue/庞源在线-安全-安全周报-20230303130801.pdf", FileFormat.PDF);
    }

}