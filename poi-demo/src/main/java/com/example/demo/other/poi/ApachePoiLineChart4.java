package com.example.demo.other.poi;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * POI EXCEL 图表-折线图
 */
public class ApachePoiLineChart4 {

    public static void main(String[] args) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook("/Users/hui.yang/Desktop/1111.xlsx");
        String sheetName = "Sheet1";
        FileOutputStream fileOut = null;
        try {
            XSSFSheet sheet = wb.getSheetAt(0);
           /*
            // 第一行，国家名称
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("俄罗斯");
            cell = row.createCell(1);
            cell.setCellValue("加拿大");
            cell = row.createCell(2);
            cell.setCellValue("美国");
            cell = row.createCell(3);
            cell.setCellValue("中国");
            cell = row.createCell(4);
            cell.setCellValue("巴西");
            cell = row.createCell(5);
            cell.setCellValue("澳大利亚");
            cell = row.createCell(6);
            cell.setCellValue("印度");
            // 第二行，乡村地区
            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(17098242);
            cell = row.createCell(1);
            cell.setCellValue(9984670);
            cell = row.createCell(2);
            cell.setCellValue(9826675);
            cell = row.createCell(3);
            cell.setCellValue(9596961);
            cell = row.createCell(4);
            cell.setCellValue(8514877);
            cell = row.createCell(5);
            cell.setCellValue(7741220);
            cell = row.createCell(6);
            cell.setCellValue(3287263);
            // 第三行，农村人口
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue(14590041);
            cell = row.createCell(1);
            cell.setCellValue(35151728);
            cell = row.createCell(2);
            cell.setCellValue(32993302);
            cell = row.createCell(3);
            cell.setCellValue(14362887);
            cell = row.createCell(4);
            cell.setCellValue(21172141);
            cell = row.createCell(5);
            cell.setCellValue(25335727);
            cell = row.createCell(6);
            cell.setCellValue(13724923);
            // 第四行，面积平局
            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue(9435701.143);
            cell = row.createCell(1);
            cell.setCellValue(9435701.143);
            cell = row.createCell(2);
            cell.setCellValue(9435701.143);
            cell = row.createCell(3);
            cell.setCellValue(9435701.143);
            cell = row.createCell(4);
            cell.setCellValue(9435701.143);
            cell = row.createCell(5);
            cell.setCellValue(9435701.143);
            cell = row.createCell(6);
            cell.setCellValue(9435701.143);
            // 第四行，人口平局
            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue(22475821.29);
            cell = row.createCell(1);
            cell.setCellValue(22475821.29);
            cell = row.createCell(2);
            cell.setCellValue(22475821.29);
            cell = row.createCell(3);
            cell.setCellValue(22475821.29);
            cell = row.createCell(4);
            cell.setCellValue(22475821.29);
            cell = row.createCell(5);
            cell.setCellValue(22475821.29);
            cell = row.createCell(6);
            cell.setCellValue(22475821.29);*/

            // 创建一个画布
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            // 前四个默认0，[0,5]：从0列5行开始;[7,26]:到7列26行结束
            // 默认宽度(14-8)*12
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 17, 13, 26, 25);
            // 创建一个chart对象
            XSSFChart chart = drawing.createChart(anchor);
            // 标题
            chart.setTitleText("隐患整改率--yh测试");
            // 标题覆盖
            chart.setTitleOverlay(false);

            // 图例位置
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.TOP);

            // 分类轴标(X轴),标题位置
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            //bottomAxis.setTitle("管辖单位");
            // 值(Y轴)轴,标题位置
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            //leftAxis.setTitle("隐患整改率");

            //设置X轴 从0开始
            XDDFDataSource<String> countries = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, 25, 0, 0));

            //设置Y轴
            XDDFNumericalDataSource<Double> area = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, 25, 11, 11));
            // XDDFNumericalDataSource<Integer> area = XDDFDataSourcesFactory.fromArray(new Integer[] {17098242,9984670,9826675,9596961,8514877,7741220,3287263});

            // 数据1，单元格范围位置[2, 0]到[2, 6]
            // XDDFNumericalDataSource<Double> population = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 0, 6));

            // LINE：折线图，
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

            // 图表加载数据，折线1
            XDDFLineChartData.Series series1 = (XDDFLineChartData.Series) data.addSeries(countries, area);
            // 折线图例标题
            series1.setTitle("隐患整改率", null);
            // 设置生成是曲线还是直线 true直线
            series1.setSmooth(true);
            // 设置标记大小
            series1.setMarkerSize((short) 3);
            // 设置标记样式，星星  设置曲线上标记的样式
            series1.setMarkerStyle(MarkerStyle.CIRCLE);
           // series1.setFillProperties(new XDDFSolidFillProperties());

            // 图表加载数据，折线2
   /*         XDDFLineChartData.Series series2 = (XDDFLineChartData.Series) data.addSeries(countries, population);
            // 折线图例标题
            series2.setTitle("人口", null);
            // 曲线
            series2.setSmooth(true);
            // 设置标记大小
            series2.setMarkerSize((short) 6);
            // 设置标记样式，正方形
            series2.setMarkerStyle(MarkerStyle.SQUARE);*/

            // 图表加载数据，平均线3
            // 数据1，单元格范围位置[2, 0]到[2, 6]
           /* XDDFNumericalDataSource<Double> population3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(3, 3, 0, 6));
            XDDFLineChartData.Series series3 = (XDDFLineChartData.Series) data.addSeries(countries, population3);
            // 折线图例标题
            series3.setTitle("面积平均", null);
            // 直线
            series3.setSmooth(false);
            // 设置标记大小
            // series3.setMarkerSize((short) 3);
            // 设置标记样式，正方形
            series3.setMarkerStyle(MarkerStyle.NONE);
            // 折线图LineChart
            // XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.CHARTREUSE));
            XDDFLineProperties line = new XDDFLineProperties();
            // line.setFillProperties(fill);
            // line.setLineCap(LineCap.ROUND);
            line.setPresetDash(new XDDFPresetLineDash(PresetLineDash.DOT));// 虚线
            // XDDFShapeProperties shapeProperties = new XDDFShapeProperties();
            // shapeProperties.setLineProperties(line);
            // series3.setShapeProperties(shapeProperties);
            series3.setLineProperties(line);

            // 图表加载数据，平均线3
            // 数据1，单元格范围位置[2, 0]到[2, 6]
            XDDFNumericalDataSource<Double> population4 = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(4, 4, 0, 6));
            XDDFLineChartData.Series series4 = (XDDFLineChartData.Series) data.addSeries(countries, population4);
            // 折线图例标题
            series4.setTitle("人口平均", null);
            // 直线
            series4.setSmooth(false);
            // 设置标记大小
            // series4.setMarkerSize((short) 3);
            // 设置标记样式，正方形
            series4.setMarkerStyle(MarkerStyle.NONE);
            XDDFLineProperties line4 = new XDDFLineProperties();
            line4.setPresetDash(new XDDFPresetLineDash(PresetLineDash.DOT));// 虚线
            series4.setLineProperties(line);*/

            // 绘制
            chart.plot(data);

            // 打印图表的xml
             System.out.println(chart.getCTChart());

            // 将输出写入excel文件
            String filename = "/Users/hui.yang/Desktop/2222.xlsx";
            fileOut = new FileOutputStream(filename);
            wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            if (fileOut != null) {
                fileOut.close();
            }
        }

    }

}