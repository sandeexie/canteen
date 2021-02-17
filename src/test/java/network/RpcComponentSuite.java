package network;

import com.github.canteen.network.TransportClient;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;


/**
 *  RPC组件测试
 */
public class RpcComponentSuite extends NetWorkSuite{

	@Test
	public void testInTwoTransportClient() throws Exception{

		Channel channel1 = new NioSocketChannel();
		Channel channel2 = new NioSocketChannel();

		TransportClient client1=new TransportClient(channel1,"A");
		TransportClient client2=new TransportClient(channel2,"B");

		client1.start();
		client2.start();

	}

}
