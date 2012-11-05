package com.aoml.portlets.ideas;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;

public class MessageCreationComparator extends OrderByComparator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Object obj1, Object obj2) {
		MBMessage message1 = null;
		MBMessage message2 = null;

		try {
			if (obj1 instanceof MBMessage) {
				message1 = (MBMessage) obj1;
				message2 = (MBMessage) obj2;
			} else if (obj1 instanceof MBThread) {
				MBThread thread1 = (MBThread) obj1;
				MBThread thread2 = (MBThread) obj2;
				message1 = MBMessageLocalServiceUtil.getMessage(thread1
						.getRootMessageId());
				message2 = MBMessageLocalServiceUtil.getMessage(thread2
						.getRootMessageId());
			}

			return (message1.getCreateDate().compareTo(message2.getCreateDate()))*-1;
		} catch (Exception e) {

		}
		return 0;
	}

}
