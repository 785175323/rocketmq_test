package com.example.producer.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.example.producer.bean.bo.RocketmqConfig;

/**
 * @Auther: gaoyang
 * @Date: 2019/3/15 15:18
 * @Description:
 */
@Configuration
public class DefaultConfig {

    @Resource
    private RocketmqConfig rocketmqConfig;
    @Resource
    private CheckListen checkListen;


    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(rocketmqConfig.getGroupName());
        producer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());
        producer.setVipChannelEnabled(false);
        producer.setRetryTimesWhenSendAsyncFailed(10);
        producer.start();
        return producer;
    }

    @Bean
    public TransactionMQProducer transactionMQProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer("trans");
        producer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());
        producer.setVipChannelEnabled(false);
        producer.setTransactionListener(checkListen);
        producer.start();
        return producer;
    }


    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastConverter;
        return new HttpMessageConverters(converter);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
