package au.com.bestandless.customerinterestsfacades.customer;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;


/**
 * Facade to deal with the custom ProductInterests Customer methods and APis by providing their email address and
 * notification preferences
 *
 * @author Satvir Singh
 */
public interface BestAndlLessCustomerFacade
{
	/**
	 * Generate dummy customer data with random customerId to return if user already exists in database.
	 *
	 * @param NotificationPreferenceData
	 *           email or Mobile number id data provided by user during Product Interest registration(current
	 *           implementation is only for email id)
	 *
	 * @param name
	 *           - name of customer if possible
	 *
	 * @return created CustomerModel object
	 */
	CustomerModel createGuestUserForProductInterest(final NotificationPreferenceData notificationPreferenceData,
			final String name);

}
