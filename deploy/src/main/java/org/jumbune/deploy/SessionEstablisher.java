package org.jumbune.deploy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * This class is used to establish connection during deployment
 *
 */
public final class SessionEstablisher {
	private SessionEstablisher(){}
	static final String ECHO_HADOOP_HOME = "echo $HADOOP_HOME \n \n";
	private static final String SCP_COMMAND = "scp -f ";
	private static final Logger LOGGER = LogManager.getLogger(SessionEstablisher.class);
	private static byte[] bufs;
	
	/**
	 * method for establishing connection while authentication
	 * @param username
	 * @param namenodeIP
	 * @param nnpwd
	 * @param privateKeyPath
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Session establishConnection(String username, String namenodeIP, String nnpwd, String privateKeyPath) throws JSchException, IOException, InterruptedException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(username, namenodeIP, Constants.TWENTY_TWO);
		session.setPassword(nnpwd);
		UserInfo info = new JumbuneUserInfo();
		jsch.addIdentity(privateKeyPath, nnpwd);
		session.setUserInfo(info);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;
	}

	/**
	 * method for copying hadoop jars from namenode
	 * @param session
	 * @param username
	 * @param namenodeIP
	 * @param hadoopHome
	 * @param destinationAbsolutePath
	 * @param listOfFiles
	 * @throws JSchException
	 * @throws IOException
	 */
	public static void fetchHadoopJarsFromNamenode(Session session, String username, String namenodeIP, String hadoopHome, String destinationAbsolutePath, String... listOfFiles)
			throws JSchException, IOException {

		new File(destinationAbsolutePath).mkdirs();
		for (String fileName : listOfFiles) {
			String command = SCP_COMMAND + hadoopHome + "/" + fileName;
			copyRemoteFile(session, command, destinationAbsolutePath);
		}

	}

	private static void copyRemoteFile(Session session, String command, String fileLocation) throws JSchException, IOException {
		FileOutputStream fos = null;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		channel.connect();
		bufs = new byte[Constants.ONE_ZERO_TWO_FOUR];

		// send '\0'
		bufs[0] = 0;
		out.write(bufs, 0, 1);
		out.flush();

		while (true) {
			int c = checkAck(in);
			if (c != 'C') {
				break;
			}

			// read '0644 '
			in.read(bufs, 0, Constants.FIVE);

			long filesize = 0L;
			while (true) {
				if (in.read(bufs, 0, 1) < 0) {
					// error
					break;
				}
				if (bufs[0] == ' '){
					break;
				}
				filesize = filesize * Constants.TENL + (long) (bufs[0] - '0');
			}

			String file = null;
			for (int i = 0;; i++) {
				in.read(bufs, i, 1);
				if (bufs[i] == (byte) Constants.ZERO_CROSS_ZERO_A) {
					file = new String(bufs, 0, i);
					break;
				}
			}
			
			// send '\0'
			bufs[0] = 0;
			out.write(bufs, 0, 1);
			out.flush();

			// read a content of file
			fos = new FileOutputStream(fileLocation == null ? fileLocation : fileLocation + file);
			filesize = readFile(fos, in, filesize);
			fos.close();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			// send '\0'
			bufs[0] = 0;
			out.write(bufs, 0, 1);
			out.flush();
		}
	}

	private static long readFile(FileOutputStream fos, InputStream in,
			long filesize) throws IOException {
		int foo;
		long tempfilesize=filesize;
		while (true) {
			if (bufs.length < tempfilesize){
				foo = bufs.length;
			}else {
				foo = (int) tempfilesize;
			}
			foo = in.read(bufs, 0, foo);
			if (foo < 0) {
				// error
				break;
			}
			fos.write(bufs, 0, foo);
			tempfilesize -= foo;
			if (tempfilesize == 0L){
				break;
			}
		}
		return tempfilesize;
	}

	/**
	 * Method used to check the input
	 * b may be 0 for success,
 	 * 1 for error,
	 * 2 for fatal error,
	 * -1
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0 || b == -1){
			return b;
		}

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { 
				LOGGER.error(sb.toString());
			}
			if (b == 2) { 
				LOGGER.error(sb.toString());
			}
		}
		return b;
	}


	/**
	 * This method execute the given command over SSH
	 * 
	 * @param session
	 * @param command
	 *            Command to be executed
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	private static void executeCommand(Session session, String command) throws JSchException, IOException, InterruptedException {
		InputStream in = null;
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();
			String msg = validateCommandExecution(channel, in);
		} finally {
			if (in != null) {
				in.close();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
	}

	/**
	 * This method validates the executed command
	 * 
	 * @param channel
	 *            SSH channel
	 * @param in
	 *            input stream
	 * @return Error message if any

	 *             If any error occurred
	 */
	private static String validateCommandExecution(Channel channel, InputStream in) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();
		byte[] tmp = new byte[Constants.ONE_ZERO_TWO_FOUR];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0,Constants.ONE_ZERO_TWO_FOUR);
				if (i < 0){
					break;
				}
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				break;
			}
			Thread.sleep(Constants.THOUSAND);
		}
		return sb.toString();
	}

	private static class JumbuneUserInfo implements UserInfo {
		
		/**
		 * gets the password
		 */
		public String getPassword() {
			return null;
		}

		/**
		 * set boolean YES/NO
		 */
		public boolean promptYesNo(String str) {
			return true;
		}

		/**
		 * get the passphrase
		 */
		public String getPassphrase() {
			return null;
		}

		/**
		 * set passphrase message
		 */
		public boolean promptPassphrase(String message) {
			return true;
		}

		/**
		 * set the password
		 */
		public boolean promptPassword(String message) {
			return true;
		}

		/**
		 * set the message
		 */
		public void showMessage(String message) {
		}
	}

	/**
	 * This method execute the given command over SSH using shell prompt
	 * 
	 * @param session
	 * @param command
	 *            Command to be executed
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static  String executeCommandUsingShell(Session session, String echoCommand) throws JSchException, IOException {
		ChannelShell channelShell = null;
		BufferedReader brIn = null;
		DataOutputStream dataOut = null;
		String hadoopHome = null;
		try {
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.connect();
			brIn = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
			dataOut = new DataOutputStream(channelShell.getOutputStream());
			dataOut.writeBytes(echoCommand);
			dataOut.flush();
			String line = null;
			while ((line = brIn.readLine()) != null) {
				if (line.contains("$ echo $HADOOP_HOME")) {
					hadoopHome = brIn.readLine();
					break;
				}

			}

		}finally {
			if (brIn != null) {
					brIn.close();
			}
			if (dataOut != null) {
					dataOut.close();
			}
			if (channelShell != null) {
				channelShell.disconnect();
			}
		}
		return hadoopHome;
	}

}