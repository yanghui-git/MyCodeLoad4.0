package com.example.demo.other.jfree;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * https://www.jianshu.com/p/6c4f3832c396
 *
 * jfree生成柱状图 图表
 */
public class TestPoiAndJfreeChart2 {

    public static void main(String[] args) throws Exception {
        // excel2003工作表
        HSSFWorkbook wb = new HSSFWorkbook();
        // 创建工作表
        HSSFSheet sheet = wb.createSheet("Sheet 1");
        // 创建字节输出流
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        //如 果不使用Font,中文将显示不出来
        Font font = new Font("新宋体", Font.BOLD, 15);
        // 创建数据
        Map<String, Map<String, Double>> datas =new HashMap<String, Map<String,Double>>();

        Map<String, Double> map1=new HashMap<String, Double>();
        Map<String, Double> map2=new HashMap<String, Double>();
        Map<String, Double> map3=new HashMap<String, Double>();

        map1.put("故障数", (double) 1000);
        map2.put("故障数", (double) 1300);
        map3.put("故障数", (double) 1000);

        //压入数据
        datas.put("设备网络掉线", map1);
        datas.put("CPU利用率高", map2);
        datas.put("磁盘占用高", map3);

        JFreeChart chart = createPort("故障类型比例",datas,"故障类型","数量单位（个）",font);
        // 读取chart信息至字节输出流
        ChartUtilities.writeChartAsPNG(byteArrayOut, chart, 600, 300);
        // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // anchor主要用于设置图片的属性
        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 2, (short) 1, (short) 12, (short) 15);
        anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
        // 插入图片
        patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        // excel2003后缀
        FileOutputStream fileOut = new FileOutputStream("/Users/hui.yang/Desktop/excel演示/jfree/生成数据表演示.xlsx");
        wb.write(fileOut);
        fileOut.close();
    }

    public static JFreeChart createPort(String title,Map<String,Map<String,Double>> datas,String type,String danwei,Font font){
        try {
            //种类数据集
            DefaultCategoryDataset ds = new DefaultCategoryDataset();


            //获取迭代器：
            Set<Map.Entry<String, Map<String, Double>>> set1 =  datas.entrySet();
            Iterator iterator1= set1.iterator();
            Iterator iterator2;
            HashMap<String, Double> map;
            Set<Map.Entry<String,Double>> set2;
            Map.Entry entry1;
            Map.Entry entry2;

            while(iterator1.hasNext()){
                entry1=(Map.Entry) iterator1.next();

                map=(HashMap<String, Double>) entry1.getValue();
                set2=map.entrySet();
                iterator2=set2.iterator();
                while (iterator2.hasNext()) {
                    entry2= (Map.Entry) iterator2.next();
                    ds.setValue(Double.parseDouble(entry2.getValue().toString()), entry2.getKey().toString(), entry1.getKey().toString());
                }
            }

            //创建柱状图,柱状图分水平显示和垂直显示两种
            JFreeChart chart = ChartFactory.createBarChart(title, type, danwei, ds, PlotOrientation.VERTICAL, true, true, true);

            //设置整个图片的标题字体
            chart.getTitle().setFont(font);

            //设置提示条字体
            font = new Font("宋体", Font.BOLD, 15);
            chart.getLegend().setItemFont(font);

            //得到绘图区
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            //得到绘图区的域轴(横轴),设置标签的字体
            plot.getDomainAxis().setLabelFont(font);

            //设置横轴标签项字体
            plot.getDomainAxis().setTickLabelFont(font);

            //设置范围轴(纵轴)字体
            plot.getRangeAxis().setLabelFont(font);

            plot.setForegroundAlpha(1.0f);
            return chart;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
 