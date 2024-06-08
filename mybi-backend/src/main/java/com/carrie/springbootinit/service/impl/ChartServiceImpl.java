package com.carrie.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carrie.springbootinit.model.entity.Chart;
import com.carrie.springbootinit.service.ChartService;
import com.carrie.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author carriemint
* @description 针对表【chart(图表信息)】的数据库操作Service实现
* @createDate 2024-05-13 18:08:53
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




