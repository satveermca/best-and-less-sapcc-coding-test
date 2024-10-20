/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package au.com.bestandless.customerinterestsfacades.customer.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test suite for {@link BestAndlLessCustomerFacadeImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BestAndlLessCustomerFacadeImplTest
{
	private BestAndlLessCustomerFacadeImpl bestAndlLessCustomerFacadeImpl;
	@Mock
	private UserService userService;
	@Mock(lenient = true)
	private CustomerAccountService customerAccountService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock(lenient = true)
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private CartService cartService;
	private CurrencyData defaultCurrencyData;

	private LanguageData defaultLanguageData;

	private NotificationPreferenceData notificationPreferenceData;

	final String email = "test@test.com";

	@Mock
	private AbstractPopulatingConverter<UserModel, CustomerData> customerConverter;

	@Before
	public void setUp()
	{
		bestAndlLessCustomerFacadeImpl = new BestAndlLessCustomerFacadeImpl();
		bestAndlLessCustomerFacadeImpl.setUserService(userService);
		bestAndlLessCustomerFacadeImpl.setModelService(mockModelService);
		bestAndlLessCustomerFacadeImpl.setCustomerAccountService(customerAccountService);
		bestAndlLessCustomerFacadeImpl.setCommonI18NService(commonI18NService);

		notificationPreferenceData = new NotificationPreferenceData();
		notificationPreferenceData.setValue(email);
		notificationPreferenceData.setChannel(NotificationChannel.EMAIL);
		notificationPreferenceData.setEnabled(true);
	}

	@Test
	public void testCreateGuestUserForProductInterest() throws DuplicateUidException
	{
		final CustomerModel guestCustomer = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(guestCustomer);
		CustomerModel createCustomer= bestAndlLessCustomerFacadeImpl.createGuestUserForProductInterest(notificationPreferenceData, "Customer");
		Assert.assertEquals(StringUtils.substringAfter(createCustomer.getUid(), "|"), email);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateGuestUserForProductInterestForEmptyId() throws DuplicateUidException
	{
		notificationPreferenceData.setValue(null);
		final CustomerModel guestCustomer = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(guestCustomer);
		bestAndlLessCustomerFacadeImpl.createGuestUserForProductInterest(notificationPreferenceData, "Customer");
	}
}
