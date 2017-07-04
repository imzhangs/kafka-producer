package com.kd.business.service.imp;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.kd.commons.consts.StringFormatConsts;

import cn.szkedun.business.service.MonitorService;
import cn.szkedun.kfk.message.consts.SaveMessageConsts;
import cn.szkedun.kfk.message.domain.MonitorMessage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MonitorServiceImp implements MonitorService {

	static Logger logger = LoggerFactory.getLogger(MonitorServiceImp.class);

	@Value("${monitor.worker.expire}")
	int expireSeconds = 86400;

	@Autowired
	JedisPool jedisPool;

	@SuppressWarnings("finally")
	@Override
	public int write(MonitorMessage message) {
		
		if(message==null || StringUtils.isBlank(message.getMonitorType())){
			return -1;
		}
		
		Jedis jedis = null;
		int result = 0;
		try {
			String ymd = DateFormatUtils.format(new Date(), StringFormatConsts.DATE_HOUR_NUMBER_FORMAT);
			String key = SaveMessageConsts.MONITOR_DATA_KEY +message.getMonitorType()+ ymd;
			//monitor-
			jedis = jedisPool.getResource();
			jedis.expire(key, expireSeconds);
			long val = jedis.sadd(key, JSONObject.toJSONString(message));
			result = val > 0 ? 1 : 0;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			jedis.close();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
			return result;
		}

	}

	@Override
	public Set<String> readAll() {
		Set<String> result=readByMonitorType(SaveMessageConsts.MONITOR_CONSUMER_KEY);
		result.addAll(readByMonitorType(SaveMessageConsts.MONITOR_WORKER_KEY));
		return result;
	}

	@Override
	public Set<String> readByMonitorType(String monitorType) {
		Jedis jedis = null;
		try {
			String ymd = DateFormatUtils.format(new Date(), StringFormatConsts.DATE_HOUR_NUMBER_FORMAT);
			String key = SaveMessageConsts.MONITOR_DATA_KEY+monitorType + ymd;
			jedis = jedisPool.getResource();
			Set<String> resultSet = jedis.smembers(key);
			return resultSet;
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			jedis.close();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

}
