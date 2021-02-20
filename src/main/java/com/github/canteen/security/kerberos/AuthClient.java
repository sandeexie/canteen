package com.github.canteen.security.kerberos;

import com.github.canteen.exception.IllegalKerberosClientException;
import com.github.canteen.internal.ConfigReader;
import com.github.canteen.internal.Configuration;
import com.github.canteen.rpc.Inbox;
import com.github.canteen.rpc.Outbox;
import com.github.canteen.network.message.OutboxMessage;
import com.github.canteen.security.kerberos.algorithm.DecodeAlgorithm;
import com.github.canteen.security.kerberos.algorithm.EncodeAlgorithm;
import com.github.canteen.security.kerberos.algorithm.StringDecoder;
import com.github.canteen.security.kerberos.algorithm.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证客户端
 */
public class AuthClient {

	private static final Logger logger= LoggerFactory.getLogger(AuthClient.class);

	private static final ConfigReader reader=ConfigReader.getReader();

	private static final String BANNED_USER="";

	private static final String BANNED_PASSWORD="";

	private String userName;

	private String password;

	private String userKey;

	private Inbox inbox;

	private Outbox outbox;

	public AuthClient(String userName,String password,EncodeAlgorithm algorithm){
		this.userName=userName;
		this.password=password;
		this.userKey=this.encode(algorithm);
	}

	public AuthClient(String userName,String password){
		this(userName,password,getEncodeAlgotithm(
				reader.get(Configuration.ENCODE_ALGORITHM,
					Configuration.DEFAULT_ENCODE_ALGORITHM)
		));
	}

	/**
	 * 用户密码加密
	 * @param algorithm
	 * @return
	 */
	public String encode(EncodeAlgorithm algorithm){
		return StringEncoder.encode(this.password,algorithm);
	}

	public String decode(DecodeAlgorithm algorithm){
		assert this.userKey!=null;
		return StringDecoder.decode(this.userKey,algorithm);
	}

	public static EncodeAlgorithm getEncodeAlgotithm(String str){
		EncodeAlgorithm algorithm=null;
		switch (str){
			case "AES":
				algorithm=EncodeAlgorithm.AES;
			break;
			case "DES":
				algorithm=EncodeAlgorithm.DES;
			break;
			case "DES3":
				algorithm=EncodeAlgorithm.DES3;
			break;
			case "RSA":
				algorithm=EncodeAlgorithm.RSA;
			break;
			case "DSA":
				algorithm=EncodeAlgorithm.DSA;
			break;
			case "SHA1":
				algorithm=EncodeAlgorithm.SHA1;
			break;
			case "MD5":
				algorithm=EncodeAlgorithm.MD5;
			break;
			default:
				logger.warn("can not find suitable algorithm to encode. " +
						"please check your input algorithm is ok.");
			break;
		}
		return algorithm;
	}

	public static DecodeAlgorithm getDecodeAlgorithm(String str){
		DecodeAlgorithm algorithm=null;
		switch (str){
			case "AES":
				algorithm=DecodeAlgorithm.AES;
			break;
			case "DES":
				algorithm=DecodeAlgorithm.DES;
			break;
			case "DES3":
				algorithm=DecodeAlgorithm.DES3;
			break;
			case "RSA":
				algorithm=DecodeAlgorithm.RSA;
			break;
			case "DSA":
				algorithm=DecodeAlgorithm.DSA;
			break;
			default:
				logger.warn("can not find suitable algorithm to decode. " +
						"please check your input algorithm is ok.");
			break;
		}
		return algorithm;
	}

	/**
	 * 认证客户端向外界发送消息
	 * @param message
	 */
	public void send(OutboxMessage message){
		this.outbox.send(message);
	}

	public void send(OutboxMessage message,long timeout){
		this.outbox.send(message,timeout);
	}

//	public void receive(InboxMessage message){
//		this.inbox.receive(message);
//	}

	public void destroy(){
		this.inbox=null;
		this.outbox=null;
		// TODO 解除Loop中的注册信息

	}

	public void start(){
		this.inbox.start();
		this.outbox.start();
		// TODO 像Dispather中注册
	}

	/**
	 * 向指定认证服务器注册当前客户端
	 * @param server 认证服务器
	 */
	public void register(AuthServer server){

	}

	/**
	 * 客户端运行实体
	 * @param args 程序运行参数
	 */
	public static void main(String[] args) {
		String user=reader.get(Configuration.KERBOEROS_CLIENT_NAME,BANNED_USER);
		String password=reader.get(Configuration.KERBOEROS_CLIENT_PASSWORD,BANNED_PASSWORD);
		if(user.equals(BANNED_USER) || password.equals(BANNED_USER))
			throw new IllegalKerberosClientException("kerberos client has illegal" +
					"format with its name or password.");
		AuthClient client=new AuthClient(user,password);
		client.start();
//		client.register();

	}
}
