package com.github.canteen.network.codec;

import com.github.canteen.internal.ConfigReader;
import com.github.canteen.internal.Configuration;
import com.github.canteen.rpc.RPCAddress;
import com.github.canteen.utils.ParameterUtil;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class InboxFilter<T> extends ChannelInitializer<SocketChannel> {

	private Codec codec;

	private RPCAddress address;

	private int maxSize=1048576;

	private boolean isServer=false;

	private boolean isSilent=true;

	private Class<T> type;

	private static ConfigReader reader=ConfigReader.getReader();

	public InboxFilter(RPCAddress address,Codec codec,int maxSize,boolean isServer,boolean isSilent){
		this.address=address;
		this.codec=codec;
		this.maxSize=maxSize;
		this.isServer=isServer;
		this.isSilent=isSilent;
	}

	public InboxFilter(RPCAddress address,Codec codec,int maxSize,boolean isServer){
		this.codec=codec;
		this.address=address;
		this.maxSize=maxSize;
		this.isServer=isServer;
	}

	public InboxFilter(RPCAddress address,Codec codec,int maxSize){
		this.codec=codec;
		this.address=address;
		this.maxSize=maxSize;
	}

	public InboxFilter(Codec codec){
		this.codec=codec;
	}

	public Codec getCodec() {
		return codec;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline=ch.pipeline();
		switch (codec){
			case STRING:
				pipeline.addLast("FrameDecoder",new LineBasedFrameDecoder(maxSize));
				pipeline.addLast("StringDecoder",new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast("StringEncoder",new StringEncoder());
			break;

			case BASE64:
				pipeline.addLast("FrameDecoder",new DelimiterBasedFrameDecoder(maxSize));
				pipeline.addLast("Base64Decoder",new Base64Decoder());
				pipeline.addLast("Base64Encoder",new Base64Encoder());
			break;

			case BYTEARRAY:
				pipeline.addLast("FrameDecoder",new LengthFieldBasedFrameDecoder(maxSize,0,4,0,4));
				pipeline.addLast("ByteArrayDecoder",new ByteArrayDecoder());
				pipeline.addLast("FrameEncoder",new LengthFieldPrepender(4));
				pipeline.addLast("ByteArrayEncoder",new ByteArrayEncoder());
			break;

			case HTTP:
				pipeline.addLast("HttpEncoder",new HttpRequestEncoder());
				pipeline.addLast("HttpDecoder",new HttpResponseDecoder());
				pipeline.addLast(
						"Aggregator",
						new HttpObjectAggregator(
								ParameterUtil.getConfiguration(
										Configuration.HTTP_AGGREGATOR_MAX_ZISE,Configuration.DEFAULT_HTTP_AGGREGATOR_MAX_ZISE
								)
						)
				);
			break;

			case PROTOBUF:
				assert type.newInstance() instanceof MessageLite;
				pipeline.addLast("ProtoBufFrameDecoder",new ProtobufVarint32FrameDecoder());
				pipeline.addLast("ProtoBufEncoder",new ProtobufEncoder());
				pipeline.addLast("ProtoBufPrepender",new ProtobufVarint32LengthFieldPrepender());
				pipeline.addLast("ProtoBufDecoder",new ProtobufDecoder((MessageLite) type.newInstance()));
			break;
		}
		pipeline.addLast("endpoint "+address.toString(),new InboxHandler<>(isServer,isSilent));
	}
}
