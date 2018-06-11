package com.art1001.supply.test;

import com.art1001.supply.SupplyApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author heshaohua
 * @Title: TestBase
 * @Description: 测试类的父类
 * @date 2018/6/9 8:34
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SupplyApplication.class)
@WebAppConfiguration
public class TestBase {

}
