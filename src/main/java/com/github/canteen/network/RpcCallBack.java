package com.github.canteen.network;

import com.github.canteen.rpc.message.Message;

public interface RpcCallBack {


	public void onSuccess(Message message);

	public void onFailure(Exception cause);

}
