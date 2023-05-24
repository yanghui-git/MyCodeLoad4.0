package test.report;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.platform.commons.util.StringUtils;

import java.io.*;

/**
 * POI 往excel插入图片
 */
public class POIExport {

    private Workbook workbook;

    public static void main(String[] args) throws Exception {
        Workbook workbook = getWorkBook("/Users/hui.yang/Desktop/excel演示/poi/演示1.xlsx");
        Sheet sheet = workbook.getSheet("sheet1");
        InputStream inputStream = new FileInputStream("/Users/hui.yang/Desktop/excel演示/poi/演示图表1.png");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        // 这里根据实际需求选择图片类型
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
        CreationHelper helper = workbook.getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        Drawing drawing = sheet.createDrawingPatriarch();
        anchor.setRow1(10); //插入行
        anchor.setCol1(0); // 插入列
        // 插入图片
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        // 调整图片占单元格百分比的大小，1.0就是100%
        pict.resize(8, 5);
        //临时缓冲区
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        //创建临时文件
        try {
            workbook.write(byteArrayOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }
        byte[] bookByteAry = byteArrayOut.toByteArray();
        File file = new File("/Users/hui.yang/Desktop/excel演示/poi/演示插入结果.xlsx");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bookByteAry, 0, bookByteAry.length);
        fos.flush();
        fos.close();
    }

    /**
     * 获取workBoot 兼容xlsx xls
     */
    public static Workbook getWorkBook(String filePath) throws Exception {
        Workbook workbook = null;
        if (StringUtils.isBlank(filePath)) {
            // throw new RuntimeException("路径错误!");
        } else if (filePath.toLowerCase().endsWith("xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(filePath));
        } else if (filePath.toLowerCase().endsWith("xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(filePath));
        } else {
            //   throw new RuntimeException("路径错误!");
        }
        return workbook;
    }
}