package bigdata.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class DistributedClient {
	private ZooKeeper zk = null;
	private static final String connectString = "mini1:2181";//���Ÿ���
	private static final int sessionTimeout = 2000;
	private static final String parentNode = "/servers";
	private volatile List<String> serverList;
	
	/**
	 * ����zk�Ŀͻ�������
	 * @throws IOException
	 */
	public void getConnect() throws IOException{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				//�յ��¼�֪ͨ��Ļص�������Ӧ��ʱ�����Լ��¼��Ĵ����߼�
				try {
					getServerList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * ��ȡ��������Ϣ�б�
	 * @throws Exception
	 */
	public void getServerList() throws Exception{
		List<String> children = zk.getChildren(parentNode, true);
		List<String> servers = new ArrayList<>();
		for (String child : children){
			byte[] data = zk.getData(parentNode + "/" + child, false, null);
			servers.add(new String(data));
		}
		// ��servers��ֵ����Ա����serverList�����ṩ����ҵ���߳�ʹ��
		serverList = servers;
		//��ӡ�������б�
		System.out.println(serverList);
	}
	
	/**
	 * ҵ����
	 * @throws InterruptedException
	 */
	public void handleBussiness() throws InterruptedException {
		System.out.println("client start working.....");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		// ��ȡzk����
		DistributedClient client = new DistributedClient();
		client.getConnect();
		
		// ��ȡservers���ӽڵ���Ϣ���������������л�ȡ��������Ϣ�б�
		client.getServerList();
		
		// ҵ���߳�����
		client.handleBussiness();
	}
}
