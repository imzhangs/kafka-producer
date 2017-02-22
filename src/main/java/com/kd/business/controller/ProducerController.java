package com.kd.business.controller;

import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.kd.commons.domain.KafkaMessage;
import com.kd.commons.enums.ExplainTypeEnum;
import com.kd.commons.result.BaseResult;
import com.kd.commons.result.ResponseSet;

@RestController
public class ProducerController {

	@Value("${kafka.topic}")
	String kafkaTopic;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	@Qualifier("sendKfkExcutor")
	ExecutorService executor ;

	@RequestMapping(value = "/pushTest")
	public String pushTest(String url, boolean isSave) {
		KafkaMessage message = new KafkaMessage();
		if (StringUtils.isBlank(url)) {
			return "param is error";
		}
		message.setUrl(url);
		message.setKeyRegexs("评论|参与");
		message.setType(ExplainTypeEnum._default);
		message.setSaveToIndex(isSave);

		ListenableFuture<SendResult<String, String>> resultFuture = kafkaTemplate.send(kafkaTopic,
				JSONObject.toJSONString(message));
		if (resultFuture.isDone()) {
			return "sent";
		}
		return "OK";
	}

	@RequestMapping(value = "/pushData", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public String pushData(@RequestBody String data) {
		
		ListenableFuture<SendResult<String, String>> resultFuture = kafkaTemplate.send(kafkaTopic, data);
		if (resultFuture.isDone()) {
			return "sent";
		}
		return "OK";
	}
	
	@RequestMapping(value = "/push")
	public ResponseSet push(KafkaMessage message){
		BaseResult result=new BaseResult();
		if(StringUtils.isBlank(message.getTopic())){
			return result.paramError("topic is null");
		}
		ListenableFuture<SendResult<String, String>> resultFuture = kafkaTemplate.send(message.getTopic(), JSONObject.toJSONString(message));
		if (resultFuture.isDone()) {
			return result.ok("sent done");
		}
		return result.ok();
	}

}
