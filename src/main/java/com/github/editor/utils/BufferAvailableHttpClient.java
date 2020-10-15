package com.github.editor.utils;

import sun.net.www.http.HttpClient;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 对HTTP进行扩展,原有的HTTP报文只支持流式读写,这里扩展NIO的读写方式,提升IO速率
 *
 */
public class BufferAvailableHttpClient extends HttpClient {

	private ByteBuffer buffer;

	private FileChannel channel;


}
