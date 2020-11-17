package com.github.canteen.metrics;

import com.github.canteen.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LongMetrics extends Metrics{

	private static final Logger logger= LoggerFactory.getLogger(IntegerMetrics.class);

	private Map<String, AtomicLong> metrics=new ConcurrentHashMap<String, AtomicLong>();

	public boolean updateMetrics(String name, Number delta) {
		try {
			this.metrics.get(name).getAndAdd((Long) delta);
			return true;
		}catch (Exception e){
			String msg="Metrics was update on failure," +
					"because conversion between Long and " +
					"Number executed unsuccessfully";
			logger.error(msg);
			// TODO 之后处理
			this.send(msg,"localhost",5488);
			return false;
		}
	}

	public Number getMetricsValue(String name) {
		Long value=this.metrics.get(name).get();
		if(value==null)
			logger.warn("Metrics of "+name+" is empty!!!");
		return value;
	}

	public boolean clearMetrics() {
		this.metrics.clear();
		return true;
	}

	public Object send(Object event, String host, Integer port) {
		if(host== "localhost" || host=="127.0.0.1"){
			return true;
		} else{
			// TODO 这里需要通过RPC端口进行连接
			Client client=new Client(host,port);
			return false;
		}
	}
}
