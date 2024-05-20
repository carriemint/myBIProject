package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
//import org.springframework.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
//import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelUtils {
    public static String excelTocsv(MultipartFile multipartFile) throws FileNotFoundException {
//        File file = null;
//        try{
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        }catch(FileNotFoundException e){
//            e.printStackTrace();
//        }
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误");
        }
        //可以同时判断null或者空
        if(CollUtil.isEmpty(list)){
            return "";
        }
        //转csv
        StringBuilder stringbuilder=new StringBuilder();
        //读取表头（linkedhashmap是有序的）
        LinkedHashMap<Integer,String> headerMap=(LinkedHashMap<Integer, String>) list.get(0);
        List<String> hedearList=headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringbuilder.append(StringUtils.join(hedearList,",")).append("\n");
        //读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer,String> dataMap=(LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList=dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringbuilder.append(StringUtils.join(dataList,",")).append("\n");
        }
        return stringbuilder.toString();
    }

    public static void main(String[] args) {
        try {
            excelTocsv(null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}