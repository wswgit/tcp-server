package com.csii.tcpserver.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelAPIParse {
    public static Map map = new HashMap();

    /**
     * 支持配置的文件就是API文件，或者是直接包含API文件，考虑内存问题，我们只取第一个excel的文件
     *
     * @param apiPath
     * @return
     * @throws IOException
     */
    public static void loadFile(String apiPath) throws IOException {
        File file = new File(apiPath);
        String fileEnd = null;
        if (file.isDirectory()) {//如果配的是个文件夹我们读取文件夹下的第一个文件
            File[] files = file.listFiles();
            if (files.length < 1)
                throw new IOException("application.properties中的api.path指向的目录中没有需要的文件");
            for (File f : files) {
                fileEnd = f.getName().substring(f.getName().lastIndexOf(".")).toUpperCase();
                if (!".XLS".equals(fileEnd) && !".XLSX".equals(fileEnd)) continue;
                file = f;
                break;
            }
        }
        if (!".XLS".equals(fileEnd) && !".XLSX".equals(fileEnd))
            //不是文件夹不是文件直接报错
            throw new IOException("application.properties中的api.path需要指向一个目录或者一个文件");
        try (InputStream inputStream = new FileInputStream(file)) {
            //这里我们开始解析文件
            readExcel(inputStream, fileEnd);
        }
    }


    /**
     * 根据不同的格式获取不同的对象
     *
     * @param in      输入流
     * @param fileEnd 文件格式
     * @return Excel的对象
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream in, String fileEnd) throws Exception {
        Workbook workbook = null;
        if (".XLS".equals(fileEnd))
            workbook = new HSSFWorkbook(in);
        else if (".XLSX".equals(fileEnd))
            workbook = new XSSFWorkbook(in);
        return workbook;
    }

    public static void readExcel(InputStream in, String fileEnd) {
        try {
            Workbook wb = getWorkbook(in, fileEnd);//获得正确的流
            Iterator iterator = wb.sheetIterator();
            while (iterator.hasNext()) {//遍历sheet
                Sheet sheet = (Sheet) iterator.next();
                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();
                Row sheetTypeRow = sheet.getRow(firstRowIndex);
                if (sheetTypeRow != null)
                    if ("API".equalsIgnoreCase(sheetTypeRow.getCell(sheetTypeRow.getFirstCellNum()).toString()))
                        map.putAll(analysisAPIBody(sheet, firstRowIndex, lastRowIndex));
                    else if ("APIHeader".equalsIgnoreCase(sheetTypeRow.getCell(sheetTypeRow.getFirstCellNum()).toString()))
                        map.put("Header", analysisAPIHead(sheet, firstRowIndex, lastRowIndex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sheet
     * @return
     * @steps 1.获取到编辑有API的sheet
     * 2.空行为接口之间的分隔
     * 3.遍历行列
     * 4.遍历到包含"(TC)"为一个接口定义开始,"(TC)"为子接口
     * 5.
     */
    private static Map analysisAPIBody(Sheet sheet, int firstRowIndex, int lastRowIndex) throws IOException {
        Map map = new HashMap();
        Map apiMap = new HashMap();
        Map reqMap = new HashMap();
        Map resMap = new HashMap();
        apiMap.put("Req", reqMap);
        apiMap.put("Res", resMap);
        int titleRow = 0;
        boolean inAPI = false;
        String inReqOrRes = "";
        String apiCode = "";

        for (int rowIndex = firstRowIndex + 1; rowIndex <= lastRowIndex; rowIndex++) {   //遍历行
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                inAPI = false;
                inReqOrRes = "";
                continue;
            }
            int firstCellIndex = row.getFirstCellNum();
            int lastCellIndex = row.getLastCellNum();

            Map rowMap = null;
            if (!"".equals(inReqOrRes))
                rowMap = new HashMap();
            for (int index = firstCellIndex; index < lastCellIndex; index++) {   //遍历列
                Cell cell = row.getCell(index);
                if (cell == null)
                    continue;
                String value = cell.toString();
                if (value.contains("(TC)") && !inAPI) {
                    inAPI = true;
                    inReqOrRes = "";
                    if (row.getCell(index + 1) == null)
                        throw new IOException("交易码需要值");
                    apiCode += row.getCell(index + 1).toString();
                } else if (value.contains("(STC)") && inAPI) {
                    if (row.getCell(index + 1) == null)
                        throw new IOException("子交易码需要值");
                    apiCode += row.getCell(index + 1).toString();
                } else if (value.contains("(REQ)") && inAPI) {
                    map.put(apiCode, apiMap);
                    rowMap = new HashMap();
                    titleRow = rowIndex - 1;
                    inReqOrRes = "REQ";
                } else if (value.contains("(RES)") && inAPI) {
                    inReqOrRes = "RES";
                } else if ("REQ".equalsIgnoreCase(inReqOrRes)) {
                    String title = sheet.getRow(titleRow).getCell(index).toString();
                    rowMap.put(title.substring(title.indexOf("(") + 1, title.length() - 1), value);
                } else if ("RES".equalsIgnoreCase(inReqOrRes)) {
                    String title = sheet.getRow(titleRow).getCell(index).toString();
                    rowMap.put(title.substring(title.indexOf("(") + 1, title.length() - 1), value);
                }
            }
            if ("REQ".equalsIgnoreCase(inReqOrRes)) {
                if (row.getCell(1) == null)
                    throw new IOException("字段名称列不能为空");
                reqMap.put(row.getCell(1).toString(), rowMap);
            } else if ("RES".equalsIgnoreCase(inReqOrRes)) {
                if (row.getCell(1) == null)
                    throw new IOException("字段名称列不能为空");
                resMap.put(row.getCell(1).toString(), rowMap);
            }
        }
        System.out.println(map);
        return map;
    }


    private static Map analysisAPIHead(Sheet sheet, int firstRowIndex, int lastRowIndex) {
        Map map = new HashMap();

        for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            Row row = sheet.getRow(rIndex);
        }


        return map;
    }

}
