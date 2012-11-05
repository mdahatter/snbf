package com.aoml.portlets.util;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * @author Shuyang Zhou
 */
public class ServletOutputStreamAdapter extends ServletOutputStream {

	public ServletOutputStreamAdapter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void close() throws IOException {
		try {
			flush();
		}
		catch (IOException ioe) {
		}

		outputStream.close();
	}

	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		outputStream.write(bytes, 0, bytes.length);
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
		outputStream.write(bytes, offset, length);
	}

	@Override
	public void write(int b) throws IOException {
		outputStream.write(b);
	}

	protected OutputStream outputStream;

}