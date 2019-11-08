package zyxhj.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JschOpenHandler {
	private static final int DEFAULT_PORT = 22;
	private static final int DEF_WAIT_SECONDS = 60;

	private static String HOST = "47.99.209.235";// 服务器地址
	private static String USERNAME = "root";// 服务器登录名
	private static String PASSWORD = "Aa123456Aa";// 服务器密码
	// 源文件
	private static String sourcefile = "C:\\Users\\Admin\\Desktop\\榴莲.xmind";
	// 放在服务器的目标位置
	private static String DST_FILE_PATH = "/var/jar/jititest/file-uploads/1001";

	public static void main(String[] args) {
		Session session = openSession(HOST, USERNAME, PASSWORD, DEF_WAIT_SECONDS);
		ChannelShell openChannelShell = openChannelShell(session);
		openChannelShell.setInputStream(System.in);
		openChannelShell.setOutputStream(System.out);
		ChannelSftp openChannelSftp = openChannelSftp(session);
		try {
			openChannelSftp.ls(DST_FILE_PATH); // 首先查看下目录，如果不存在，系统会被错，捕获这个错，生成新的目录。
			// 上传文件到服务器
			openChannelSftp.put(sourcefile, DST_FILE_PATH, ChannelSftp.OVERWRITE);

			// 从服务器下载文件

			String downloadFileName = "C:/Users/Admin/Desktop/123.xmind";
			String downloadfilepath = DST_FILE_PATH;
			String downloadfile = "榴莲.xmind";
//	        sftpDownLoadFile(session, downloadFileName, downloadfilepath, downloadfile);

			// 删除文件可用如下方法，进入某文件所在的目录后删除该文件
			// openChannelSftp.cd(dstfilepath);
			// openChannelSftp.rm(sourcefile);

			disConn(session, openChannelSftp);
		} catch (Exception e) {
			
			try {
				openChannelSftp.mkdir(DST_FILE_PATH);
				// 上传文件到服务器
				openChannelSftp.put(sourcefile, DST_FILE_PATH, ChannelSftp.OVERWRITE);

				// 从服务器下载文件

				String downloadFileName = "C:/Users/Admin/Desktop/123.xmind";
				String downloadfilepath = DST_FILE_PATH;
				String downloadfile = "榴莲.xmind";
//		        sftpDownLoadFile(session, downloadFileName, downloadfilepath, downloadfile);

				// 删除文件可用如下方法，进入某文件所在的目录后删除该文件
				// openChannelSftp.cd(dstfilepath);
				// openChannelSftp.rm(sourcefile);

				disConn(session, openChannelSftp);
			} catch (SftpException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
		}
	}

	/**
	 * 创建服务器连接
	 * 
	 * @param host        主机
	 * @param user        用户
	 * @param password    密码
	 * @param waitSeconds 等待秒数
	 * @return
	 */
	private static Session openSession(String host, String user, String password, int waitSeconds) {
		Session session = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, DEFAULT_PORT);
			noCheckHostKey(session);
			session.setPassword(password);
			// 这个设置很重要，必须要设置等待时长为大于等于2分钟
			session.connect(waitSeconds * 1000);
			if (!session.isConnected()) {
				throw new IOException("We can't connection to[" + host + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	/**
	 * 不作检查主机键值
	 * 
	 * @param session
	 */
	private static void noCheckHostKey(Session session) {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
	}

	/**
	 * 连接shell
	 * 
	 * @param session session
	 * @return {@link ChannelShell}
	 */
	private static ChannelShell openChannelShell(Session session) {
		ChannelShell channel = null;
		try {
			channel = (ChannelShell) session.openChannel("shell");
			channel.setEnv("LANG", "en_US.UTF-8");
			channel.setAgentForwarding(false);
			channel.setPtySize(500, 500, 1000, 1000);
			channel.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (channel == null) {
			throw new IllegalArgumentException("The channle init was wrong");
		}
		return channel;
	}

	/**
	 * 连接sftp
	 * 
	 * @param session
	 * @return {@link ChannelSftp}
	 */
	private static ChannelSftp openChannelSftp(Session session) {
		ChannelSftp channel = null;
		try {
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channel;
	}

	/**
	 * 关闭连接
	 * 
	 * @param session
	 * @param sftp
	 * @throws Exception
	 */
	public static void disConn(Session session, ChannelSftp sftp) throws Exception {
		if (null != sftp) {
			sftp.disconnect();
			sftp.exit();
			sftp = null;
		}
		if (null != session) {
			session.disconnect();
			session = null;
		}
	}

	/**
	 * @Title: sftpDownLoadFile @Description: 下载指定目录下面的指定文件 @param
	 *         downloadFileName----要把文件下载到本地什麼地方、名字叫什麼，eg，downloadFileName="C:/Users/hechangting/Desktop/file/hhhhh.txt"; @param
	 *         downloadfilepath----要从远程什麼目录下面下载文件，eg，downloadfilepath="/alidata1/6080/share/20161222/301"; @param
	 *         downloadfile----要从远程什麼目录下面下载的文件的名字，eg，downloadfile="redemption_balance_confirm_file_20161222_301.txt"; @return
	 *         void 返回类型 @throws
	 */
	public static void sftpDownLoadFile(Session session, String downloadFileName, String downloadfilepath,
			String downloadfile) throws Exception {
		Channel channel = null;
		try {
			// 创建sftp通信通道
			channel = (Channel) session.openChannel("sftp");
			channel.connect(1000);
			ChannelSftp sftp = (ChannelSftp) channel;

			// 进入服务器指定的文件夹
			sftp.cd(downloadfilepath);

			// 列出服务器指定的文件列表
			Vector v = sftp.ls("*.txt");
			for (int i = 0; i < v.size(); i++) {
				System.out.println(v.get(i));
			}

			// 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			InputStream instream = sftp.get(downloadfile);
			OutputStream outstream = new FileOutputStream(new File(downloadFileName));

			byte b[] = new byte[1024];
			int n;
			while ((n = instream.read(b)) != -1) {
				outstream.write(b, 0, n);
			}
			System.out.println("下載文件" + downloadfile + "成功!");
			outstream.flush();
			outstream.close();
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
			channel.disconnect();
		}
	}
}
