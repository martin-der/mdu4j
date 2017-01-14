package net.tetrakoopa.mdu4j.test.service;

import java.io.File;

import net.tetrakoopa.mdu4j.util.log.AbstractLogReaderService;

import org.junit.Test;

public class AbstractLogReaderServiceTest {


	@Test
	public void dummy() {

	}

	public void test() {

	
		final File file = new File("/tmp/zzzzz");
		long delay = 1000;
	
		AbstractLogReaderService service = new AbstractLogReaderService(file, delay) {
		};
		
		System.out.println("starting...");
		service.run();
		System.out.println("started");

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
		}

		System.out.println("stoping...");
		service.stop();
		System.out.println("started");

	}

	public static void main(String[] args) {
		new AbstractLogReaderServiceTest().test();
	}

}
