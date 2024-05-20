package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiManagerTest {
    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String answer=aiManager.doChat(1792517838291542018L,"请帮我分析网站用户的增长趋势，并帮我生成前端代码\n" +
                "原始数据如下：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,40");
        String[] split = answer.split("【【【【【");
        String genChart=split[0];
        String genResult=split[1];
        System.out.println(genChart);
        System.out.println(genResult);
    }
}