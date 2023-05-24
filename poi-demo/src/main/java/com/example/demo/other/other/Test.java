package com.example.demo.other.other;

import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;

/**
 * 打印POI 各个节点属性
 * https://blog.csdn.net/u014644574/article/details/105695787
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String path = "/Users/hui.yang/Desktop/数据表.xlsx";
        FileInputStream fis = new FileInputStream(path);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFDrawing drawing = sheet.getDrawingPatriarch();
        List<XSSFChart> charts = drawing.getCharts();
        for (int i = 0; i < charts.size(); i++) {
            XSSFChart chart = charts.get(i);
            CTChart ctChart = chart.getCTChart();
            System.out.println(ctChart);
        }
        fis.close();
        wb.close();
    }

}