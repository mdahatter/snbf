package com.aoml.portlets.util;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * @author Shuyang Zhou
 */
public class ServletInputStreamAdapter extends ServletInputStream {

	public ServletInputStreamAdapter(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public void mark(int readLimit) {
		inputStream.mark(readLimit);
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return inputStream.read(bytes);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		return inputStream.read(bytes, offset, length);
	}

	@Override
	public void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public long skip(long skip) throws IOException {
		return inputStream.skip(skip);
	}

	protected InputStream inputStream;

}
