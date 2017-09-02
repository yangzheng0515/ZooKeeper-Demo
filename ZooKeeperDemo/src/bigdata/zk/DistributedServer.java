package bigdata.zk;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class DistributedServer {
	private ZooKeeper zk = null;
	private static final String connectString = "mini1:2181,mini2:2181,mini3:2181";//逗号隔开
	private static final int sessionTimeout = 2000;
	private static final String parentNode = "/servers";
	
	/**
	 * 创建zk的客户端连接
	 * @throws IOException
	 */
	public void getConnect() throws IOException{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				//收到事件通知后的回调函数，应该时我们自己事件的处理逻辑
				System.out.println(event.getType() + "---" + event.getPath());
				try {
					zk.getChildren("/", true);
				} catch (Exception e) { } 
			}
		});
	}
	
	/**
	 * 向zk集群注册服务器信息
	 * @param hostname
	 * @throws Exception
	 */
	public void registerServer(String hostname) throws Exception{
		String create = zk.create(parentNode + "/server", hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname + " is online..." + create);
	}
	
	/**
	 * 业务功能
	 * @param hostname
	 * @throws InterruptedException
	 */
	public void handleBussiness(String hostname) throws InterruptedException{
		System.out.println(hostname + " start working.....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	public static void main(String[] args) throws Exception {
		//获取zk连接
		DistributedServer server = new DistributedServer();
		server.getConnect();
		
		//利用zk连接注册服务器信息
		server.registerServer(args[0]);
		
		//启动业务功能
		server.handleBussiness(args[0]);
	}
}
