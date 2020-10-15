package network;

import sun.net.www.http.HttpClient;

import java.io.IOException;

public class Client {

	public static HttpClient httpClient;

	public static void main(String[] args) {
		try {
			httpClient.openServer("127.0.0.1",5555);
			httpClient.writeRequests(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
