package com.aoml.portlets.ideas.model;

import com.liferay.portal.kernel.exception.SystemException;

import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;

/**
 * The extended model base implementation for the MBMessage service. Represents a row in the &quot;MBMessage&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This class exists only as a container for the default extended model level methods generated by ServiceBuilder. Helper methods and all application logic should be put in {@link MBMessageImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBMessageImpl
 * @see com.liferay.portlet.messageboards.model.MBMessage
 * @generated
 */
public abstract class MBMessageBaseImpl extends MBMessageModelImpl
	implements MBMessage {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a message-boards message model instance should use the {@link MBMessage} interface instead.
	 */
	public void persist() throws SystemException {
		if (this.isNew()) {
			MBMessageLocalServiceUtil.addMBMessage(this);
		}
		else {
			MBMessageLocalServiceUtil.updateMBMessage(this);
		}
	}
}