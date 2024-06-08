package com.carrie.springbootinit.bimq;

import com.carrie.springbootinit.common.ErrorCode;
import com.carrie.springbootinit.exception.BusinessException;
import com.carrie.springbootinit.exception.ThrowUtils;
import com.carrie.springbootinit.model.entity.Chart;
import com.carrie.springbootinit.openai.ChatGptService;
import com.carrie.springbootinit.service.ChartService;
import com.carrie.springbootinit.utils.ExcelUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import retrofit2.http.Header;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;

    @Resource
    private ChatGptService chatGptService;



    //指定程序监听的消息队列和确认机制
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(@Payload String message,Channel channel,@Headers Map<String,Object> map) throws IOException {
        log.info("receiveMessage message={}", message);
        if(StringUtils.isBlank(message)){
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long chartId=Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if(chart==null){
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
        }
        //修改执行状态
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,false);
            handleChartUpdateError(chart.getId(),"图表运行状态更改失败");
            return;
        }
        //调用ai接口
        String result = chatGptService.doChat(buildUserInput(chart));
        String[] split = result.split("【【【【【");
        if (split.length < 3) {
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,false);
            handleChartUpdateError(chart.getId(),"AI生成错误");
        }
        String genChart = split[1].trim();
        String genResult = split[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus("succeed");
        boolean br = chartService.updateById(updateChartResult);
        if (!br) {
            channel.basicNack((Long)map.get(AmqpHeaders.DELIVERY_TAG),false,false);
            handleChartUpdateError(chart.getId(),"图表成功状态更改失败");
            return;
        }
        channel.basicAck((Long)map.get(AmqpHeaders.DELIVERY_TAG),false);
    }

    /*
    构造用户输入
     */
    private String buildUserInput(Chart chart){
        String goal=chart.getGoal();
        String chartType=chart.getChartType();
        String csvData=chart.getChartData();
        //用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append('\n');
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += ",请使用" + chartType;
        }
        userInput.append(userGoal).append('\n');
        userInput.append("原始数据：").append('\n');
        userInput.append(csvData).append('\n');;
        return userInput.toString();
    }
    private void handleChartUpdateError(long chartId,String execMessage){
        Chart updateChartResult=new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean br = chartService.updateById(updateChartResult);
        if (!br) {
            log.error("更新图表失败状态失败"+chartId+","+execMessage);
        }
    }
}
