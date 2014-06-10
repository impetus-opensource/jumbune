package org.jumbune.remoting.handlers;

import org.easymock.EasyMock;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.handlers.LogFilesEncoder;
import org.junit.*;
import static org.junit.Assert.*;

public class LogFilesEncoderTest {
	private LogFilesEncoder fixture = new LogFilesEncoder();


	public LogFilesEncoder getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testEncode_fixture_1()
		throws Exception {
		LogFilesEncoder fixture2 = getFixture();
		ChannelHandlerContext ctx = EasyMock.createMock(ChannelHandlerContext.class);
		Channel channel = EasyMock.createMock(Channel.class);
		Object originalMessage = "";
		// add mock object expectations here

		EasyMock.replay(ctx);
		EasyMock.replay(channel);

		Object result = fixture2.encode(ctx, channel, originalMessage);

		EasyMock.verify(ctx);
		EasyMock.verify(channel);
		assertNotNull(result);
	}

	@Test
	public void testEncode_fixture_2()
		throws Exception {
		LogFilesEncoder fixture2 = getFixture();
		ChannelHandlerContext ctx = EasyMock.createMock(ChannelHandlerContext.class);
		Channel channel = EasyMock.createMock(Channel.class);
		Object originalMessage = "0123456789";
		// add mock object expectations here

		EasyMock.replay(ctx);
		EasyMock.replay(channel);

		Object result = fixture2.encode(ctx, channel, originalMessage);

		EasyMock.verify(ctx);
		EasyMock.verify(channel);
		assertEquals(null, result);
	}

	

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}