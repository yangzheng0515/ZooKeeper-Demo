package bigdata.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DistributedClient {
	private ZooKeeper zk = null;
	private static final String connectString = "mini1:2181";//逗号隔开
	private static final int sessionTimeout = 2000;
	private static final String parentNode = "/servers";
	private volatile List<String> serverList;
	
	/**
	 * 创建zk的客户端连接
	 * @throws IOException
	 */
	public void getConnect() throws IOException{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				//收到事件通知后的回调函数，应该时我们自己事件的处理逻辑
				try {
					getServerList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 获取服务器信息列表
	 * @throws Exception
	 */
	public void getServerList() throws Exception{
		List<String> children = zk.getChildren(parentNode, true);
		List<String> servers = new ArrayList<>();
		for (String child : children){
			byte[] data = zk.getData(parentNode + "/" + child, false, null);
			servers.add(new String(data));
		}
		// 把servers赋值给成员变量serverList，已提供给各业务线程使用
		serverList = servers;
		//打印服务器列表
		System.out.println(serverList);
	}
	
	/**
	 * 业务功能
	 * @throws InterruptedException
	 */
	public void handleBussiness() throws InterruptedException {
		System.out.println("client start working.....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		// 获取zk连接
		DistributedClient client = new DistributedClient();
		client.getConnect();
		
		// 获取servers的子节点信息（并监听），从中获取服务器信息列表
		client.getServerList();
		
		// 业务线程启动
		client.handleBussiness();
	}
}
