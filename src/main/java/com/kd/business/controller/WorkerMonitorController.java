package com.kd.business.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.szkedun.business.service.MonitorService;
import cn.szkedun.kfk.message.domain.MonitorMessage;

@RestController
@RequestMapping("/monitor")
public class WorkerMonitorController {
	
	MonitorService monitorService;
	

	@RequestMapping("/write")
	public int writeMonitorData(MonitorMessage message){
		return monitorService.write(message);
	}

	@RequestMapping("/view")
	public ModelAndView monitorView(String monitorType){
		ModelAndView mv=new ModelAndView("worker_monitor_view");
		Set<String> results=monitorService.readAll();
		List<MonitorMessage> resultList=new ArrayList<>();
		for(String result:results){
			MonitorMessage e=JSONObject.parseObject(result, MonitorMessage.class);
			resultList.add(e);
		}
		mv.addObject("resultList", resultList);
		return mv;
	}


	
}
