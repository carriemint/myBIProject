package com.carrie.springbootinit.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ChartMapperTest {
    @Resource
    private ChartMapper chartMapper;

    @Test
    void queryChartData() {
        String querySql="select * from chart_1795445006471856130";
        List<Map<String,Object>> resultData=chartMapper.queryChartData(querySql);
        System.out.println(resultData);
    }
}