package Compress;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompressSuite {



	@Test
	public void compressWithLz4() throws IOException {
		String plainText="xsadaeefweadwegsdffdhgrdge";
		InputStream inputStream=new ByteArrayInputStream(plainText.getBytes());

		OutputStream fout = Files.newOutputStream(Paths.get("E:/tmp/archive.tar.lz4"));
		BufferedOutputStream out = new BufferedOutputStream(fout);
		FramedLZ4CompressorOutputStream outputStream=new FramedLZ4CompressorOutputStream(out);

		final byte[] buffer = new byte[64];
		int n = 0;
		while (-1 != (n = inputStream.read(buffer))) {
			outputStream.write(buffer, 0, n);
		}
		outputStream.close();
		inputStream.close();
	}

	@Test
	public void releasWithLz4() throws Exception{
		InputStream fin = Files.newInputStream(Paths.get("E:/tmp/archive.tar.lz4"));
		BufferedInputStream in = new BufferedInputStream(fin);
		OutputStream out = Files.newOutputStream(Paths.get("E:/tmp/archive.txt"));
		FramedLZ4CompressorInputStream zIn = new FramedLZ4CompressorInputStream(in);
		final byte[] buffer = new byte[64];
		int n = 0;
		while (-1 != (n = zIn.read(buffer))) {
			out.write(buffer, 0, n);
		}
		out.close();
		zIn.close();
	}
}
