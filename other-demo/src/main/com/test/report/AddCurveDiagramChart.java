package test.report;

import com.spire.xls.*;
import com.spire.xls.charts.ChartSerie;
import com.spire.xls.charts.ChartSeries;

/**
 * 生成曲线图
 */
public class AddCurveDiagramChart {


    public static void main(String[] args) {

        //创建一个Workbook类的对象
        Workbook workbook = new Workbook();

        //加载Excel文档
        workbook.loadFromFile("/Users/hui.yang/Desktop/excel演示/e-iceblue/生成图片/曲线图.xlsx");

        //获取第一个工作表
        Worksheet sheet = workbook.getWorksheets().get(0);
        //设置工作表名称
        sheet.setName("汇总");

        //添加图表到工作表
        Chart chart = sheet.getCharts().add(ExcelChartType.LineStacked);
        //统计数据 Y轴
        chart.setDataRange(sheet.getRange().get("L1:L26"));
        chart.setSeriesDataFromRange(false);
        //设置位置信息
        chart.setTopRow(28);
        chart.setBottomRow(45);
        chart.setLeftColumn(1);
        chart.setRightColumn(18);
        chart.setChartTitle("隐患整改率-yh演示效果");
        chart.getChartTitleArea().isBold(true);
        //设置字体大小
        chart.getChartTitleArea().setSize(15);
        ChartSerie cs1 = chart.getSeries().get(0);
        //添加数据标签
        ChartSeries series = chart.getSeries();
        for (int i = 0;i < series.size();i++)
        {
            ChartSerie cs = series.get(i);
            // cs.getFormat().getOptions().isVaryColor(true);
            //打印图形上面的数据
            cs.getDataPoints().getDefaultDataPoint().getDataLabels().hasValue(true);
        }
        //X轴
        cs1.setCategoryLabels(sheet.getRange().get("A2:A26"));

        //添加数据表到图表
        chart.hasDataTable(true);

        //保存结果文件
        workbook.saveToFile("/Users/hui.yang/Desktop/excel演示/e-iceblue/生成图片/自动生成曲线图.xlsx", ExcelVersion.Version2016);
    }
}
