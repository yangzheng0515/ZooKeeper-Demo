package bigdata.zk;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class DistributedServer {
	private ZooKeeper zk = null;
	private static final String connectString = "mini1:2181,mini2:2181,mini3:2181";//���Ÿ���
	private static final int sessionTimeout = 2000;
	private static final String parentNode = "/servers";
	
	/**
	 * ����zk�Ŀͻ�������
	 * @throws IOException
	 */
	public void getConnect() throws IOException{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				//�յ��¼�֪ͨ��Ļص�������Ӧ��ʱ�����Լ��¼��Ĵ����߼�
				System.out.println(event.getType() + "---" + event.getPath());
				try {
					zk.getChildren("/", true);
				} catch (Exception e) { } 
			}
		});
	}
	
	/**
	 * ��zk��Ⱥע���������Ϣ
	 * @param hostname
	 * @throws Exception
	 */
	public void registerServer(String hostname) throws Exception{
		String create = zk.create(parentNode + "/server", hostname.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(hostname + " is online..." + create);
	}
	
	/**
	 * ҵ����
	 * @param hostname
	 * @throws InterruptedException
	 */
	public void handleBussiness(String hostname) throws InterruptedException{
		System.out.println(hostname + " start working.....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	
	public static void main(String[] args) throws Exception {
		//��ȡzk����
		DistributedServer server = new DistributedServer();
		server.getConnect();
		
		//����zk����ע���������Ϣ
		server.registerServer(args[0]);
		
		//����ҵ����
		server.handleBussiness(args[0]);
	}
}
