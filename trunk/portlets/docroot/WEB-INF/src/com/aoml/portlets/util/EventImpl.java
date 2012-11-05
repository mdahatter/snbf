package com.aoml.portlets.util;

import java.io.Serializable;

import javax.portlet.Event;

import javax.xml.namespace.QName;

/**
 * @author Brian Wing Shun Chan
 */
public class EventImpl implements Event, Serializable {

	public EventImpl(String name, QName qName, Serializable value) {
		_name = name;
		_qName = qName;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public QName getQName() {
		return _qName;
	}

	public Serializable getValue() {
		return _value;
	}

	private String _name;
	private QName _qName;
	private Serializable _value;

}