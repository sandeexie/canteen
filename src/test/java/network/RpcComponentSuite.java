package network;

import com.github.canteen.network.EventLoopGroupPool;
import com.github.canteen.network.NettyEndPoint;
import com.github.canteen.network.codec.Codec;
import com.github.canteen.rpc.RPCAddress;
import com.github.canteen.network.message.Message;
import com.github.canteen.utils.ParameterUtil;
import io.netty.channel.EventLoopGroup;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  RPC组件测试
 */
public class RpcComponentSuite extends NetWorkSuite{

	static int MAX_HEAP_BUFFER_SIZE=65536;

	public static final Logger logger= LoggerFactory.getLogger(RpcComponentSuite.class);

	@Test
	public void testTwoAddressIsEqual(){
		RPCAddress address1=ParameterUtil.toRpcAddress("rpc://192.168.0.1:1000");
		RPCAddress address2=ParameterUtil.toRpcAddress("rpc://192.168.0.1:1000");
		System.out.println(address1.equals(address2));
	}

	@Test
	public void testInTwoTransportClient() throws Exception{

	}

	@Test
	public void connectInTwoNettyEndPoint() throws Exception{
		NettyEndPoint endPoint1=new NettyEndPoint("127.0.0.1",3000);
		NettyEndPoint endPoint2=new NettyEndPoint("127.0.0.1",4000);

		EventLoopGroupPool.allocate(10);

		EventLoopGroup group1=EventLoopGroupPool.get();
		EventLoopGroup group2=EventLoopGroupPool.get();

		endPoint1.setEventLoopGroup(group1);
		endPoint2.setEventLoopGroup(group2);

		endPoint1.start();
		endPoint2.start();

		endPoint1.connect(endPoint2, Codec.STRING,8192,false);

		if(endPoint1.getConnection(endPoint2.getRpcAddress())!=null){
			System.out.println("linked");
		}else{
			System.out.println("disconnected");
		}

		endPoint1.disconnect(endPoint2);

		if(endPoint1.getConnection(endPoint2.getRpcAddress())!=null){
			System.out.println("linked");
		}else{
			System.out.println("disconnected");
		}

	}

	@Test
	public void sendMessageWithInTwoEndpoint() throws InterruptedException {
		NettyEndPoint endPoint1=new NettyEndPoint("127.0.0.1",3000);
		NettyEndPoint endPoint2=new NettyEndPoint("127.0.0.1",4000);

		EventLoopGroupPool.allocate(10);

		EventLoopGroup group1=EventLoopGroupPool.get();
		EventLoopGroup group2=EventLoopGroupPool.get();

		endPoint1.setEventLoopGroup(group1);
		endPoint2.setEventLoopGroup(group2);

		endPoint2.start();
		endPoint1.start();

		endPoint1.connect(endPoint2, Codec.STRING,8192,false);

		Message message=new Message(
				endPoint1.getRpcAddress(),
				endPoint2.getRpcAddress(),
				"check".getBytes()
		);

		endPoint1.sendForTest(message,endPoint2);
//		endPoint2.receive(endPoint1);
	}

}
