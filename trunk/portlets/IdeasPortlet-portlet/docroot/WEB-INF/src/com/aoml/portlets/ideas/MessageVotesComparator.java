package com.aoml.portlets.ideas;

import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.ratings.model.RatingsStats;
import com.liferay.portlet.ratings.service.RatingsStatsLocalServiceUtil;

public class MessageVotesComparator extends OrderByComparator {

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

			RatingsStats ratingsStats1 = RatingsStatsLocalServiceUtil.getStats(
					MBMessage.class.getName(), message1.getMessageId());
			RatingsStats ratingsStats2 = RatingsStatsLocalServiceUtil.getStats(
					MBMessage.class.getName(), message2.getMessageId());

			if (ratingsStats1.getTotalEntries() < ratingsStats2
					.getTotalEntries()) {
				return 1;
			} else if (ratingsStats1.getTotalEntries() == ratingsStats2
					.getTotalEntries()) {
				return 0;
			} else {
				return -1;
			}
		} catch (Exception e) {

		}
		return 0;
	}

}
