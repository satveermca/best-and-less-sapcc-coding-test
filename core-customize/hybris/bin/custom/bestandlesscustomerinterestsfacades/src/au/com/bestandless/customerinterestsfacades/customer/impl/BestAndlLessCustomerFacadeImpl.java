package au.com.bestandless.customerinterestsfacades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import au.com.bestandless.customerinterestsfacades.customer.BestAndlLessCustomerFacade;

/**
 * Default implementation for the {@link BestAndlLessCustomerFacade}.
 */
public class BestAndlLessCustomerFacadeImpl implements BestAndlLessCustomerFacade
{

	/** Prefix for registering guest user for Product Interest */
	private static final String PRODUCT_INTEREST_CUSTOMER_UID_PREFIX = "product_interest";

	private static final Logger LOG = Logger.getLogger(BestAndlLessCustomerFacadeImpl.class);

	private CustomerAccountService customerAccountService;

	private ModelService modelService;

	private CommonI18NService commonI18NService;

	private UserService userService;

	@Override
	public CustomerModel createGuestUserForProductInterest(final NotificationPreferenceData notificationPreferenceData,
			final String name)
	{
		validateParameterNotNullStandardMessage("email or mobileNumber", notificationPreferenceData.getValue());
		CustomerModel productInterestUser = null;
		try
		{
			productInterestUser = (CustomerModel) userService
					.getUserForUID(PRODUCT_INTEREST_CUSTOMER_UID_PREFIX + "|" + notificationPreferenceData.getValue());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.info("Product Interest user does not exists with UID " + PRODUCT_INTEREST_CUSTOMER_UID_PREFIX + "|"
					+ notificationPreferenceData.getValue());
		}
		if (productInterestUser == null)
		{
			productInterestUser = getModelService().create(CustomerModel.class);
			//takes care of localizing the name based on the site language
			productInterestUser.setUid(PRODUCT_INTEREST_CUSTOMER_UID_PREFIX + "|" + notificationPreferenceData.getValue());
			productInterestUser.setName(name);
			productInterestUser.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
			productInterestUser.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
			productInterestUser.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
			final Set<NotificationChannel> preferences = new HashSet();
			// Setting Customer Notification channel as email , because customer registered interest in the product by sharing their email address
			preferences.add(notificationPreferenceData.getChannel());
			productInterestUser.setNotificationChannels(preferences);
			try
			{
				getCustomerAccountService().registerGuestForAnonymousCheckout(productInterestUser, UUID.randomUUID().toString());
			}
			catch (final DuplicateUidException duiException)
			{
				LOG.info("Product Interest user alreday exists with UID " + PRODUCT_INTEREST_CUSTOMER_UID_PREFIX + "|"
						+ notificationPreferenceData.getValue());
			}
		}
		return productInterestUser;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	/**
	 * @return the customerAccountService
	 */
	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}


	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}


	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
