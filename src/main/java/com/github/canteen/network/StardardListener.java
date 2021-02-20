package com.github.canteen.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class StardardListener<T> implements GenericFutureListener {

	private static final Logger logger= LoggerFactory.getLogger(StardardListener.class);

	private T result;

	private Function function;

	public StardardListener(Function function){
		this.function=function;
	}

	public StardardListener(){
		this(null);
	}

	@Override
	public void operationComplete(Future future) throws Exception {
		if(!future.isSuccess()){
			logger.warn("Get async result on failure. beacuse "+future.cause().getMessage());
			return;
		}
		try {
			result= (T) future.getNow();
			if (function != null)
				function.apply(result);
			logger.info("Future task execute completely.");
		}catch (Exception cause){
			logger.error("Result can not fit the type {}",result.getClass().getName());
		}
	}
}
