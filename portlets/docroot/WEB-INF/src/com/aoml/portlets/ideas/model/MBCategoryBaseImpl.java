package com.aoml.portlets.ideas.model;

import com.liferay.portal.kernel.exception.SystemException;

import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.service.MBCategoryLocalServiceUtil;

/**
 * The extended model base implementation for the MBCategory service. Represents a row in the &quot;MBCategory&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This class exists only as a container for the default extended model level methods generated by ServiceBuilder. Helper methods and all application logic should be put in {@link MBCategoryImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBCategoryImpl
 * @see com.liferay.portlet.messageboards.model.MBCategory
 * @generated
 */
public abstract class MBCategoryBaseImpl extends MBCategoryModelImpl
	implements MBCategory {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a message boards category model instance should use the {@link MBCategory} interface instead.
	 */
	public void persist() throws SystemException {
		if (this.isNew()) {
			MBCategoryLocalServiceUtil.addMBCategory(this);
		}
		else {
			MBCategoryLocalServiceUtil.updateMBCategory(this);
		}
	}
}