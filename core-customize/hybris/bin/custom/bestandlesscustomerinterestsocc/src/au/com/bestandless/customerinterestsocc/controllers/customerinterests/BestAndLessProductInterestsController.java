package au.com.bestandless.customerinterestsocc.controllers.customerinterests;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.customerinterestsocc.dto.ProductInterestRelationWsDTO;
import de.hybris.platform.customerinterestsocc.validation.ProductInterestsValidator;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationocc.dto.conversation.NotificationPreferenceListWsDTO;
import de.hybris.platform.notificationocc.dto.conversation.NotificationPreferenceWsDTO;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.bestandless.customerinterestsfacades.customer.BestAndlLessCustomerFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Provides Custom RESTful API for product interests related methods like "register interest in the product by sharing
 * their email address"
 *
 * @author Satvir Singh
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/productinterests")
@Tag(name = "Product Interests")
public class BestAndLessProductInterestsController
{
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	private static final String REQUESTPARAM = "RequestParam";
	private static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;

	private static final String PRODUCT_INTEREST_CUSTOMER_NAME = "Customer";

	@Resource(name = "bestAndlLessCustomerFacade")
	private BestAndlLessCustomerFacade bestAndlLessCustomerFacade;

	@Resource(name = "productInterestFacade")
	private ProductInterestFacade productInterestFacade;

	@Resource(name = "productFacade")
	private ProductFacade productFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "productInterestsValidator")
	private ProductInterestsValidator productInterestsValidator;

	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Operation(summary = "Creates product interests by sharing email address.", description = "Creates a product interest for a customer at a time.")
	@ApiBaseSiteIdAndUserIdParam
	public ProductInterestRelationWsDTO registerProductInterest(
			@Parameter(description = "Product identifier.", example = "00001000", required = true)
			@RequestParam(required = true)
			final String productCode, @Parameter(description = "Notification type.", example = "BACK_IN_STOCK", required = true)
			@RequestParam(required = true)
			final String notificationType, @Parameter(description = "Notification preference list", required = true)
			@RequestBody
			final NotificationPreferenceListWsDTO notificationPreferences, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields) throws DuplicateUidException
	{
		NotificationPreferenceWsDTO emailPreference = null;
		if (Objects.nonNull(notificationPreferences) && Objects.nonNull(notificationPreferences.getPreferences()))
		{
			emailPreference = notificationPreferences.getPreferences().stream()
					.filter(preference -> NotificationChannel.EMAIL.getCode().equals(preference.getChannel())).findAny().orElse(null);
		}
		if (emailPreference == null)
		{
			throw new RequestParameterException("Please provide email id for product interests notification registration !",
					RequestParameterException.MISSING, "Subscribe");
		}
		if (!EmailValidator.getInstance().isValid(emailPreference.getValue()))
		{
			throw new RequestParameterException(
					"Email [" + YSanitizer.sanitize(emailPreference.getValue()) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "Subscribe");
		}
		final NotificationPreferenceData notificationPreferenceData = dataMapper.map(emailPreference,
				NotificationPreferenceData.class);
		// register the Product interest customer a guest here by their email id.
		final CustomerModel user = bestAndlLessCustomerFacade.createGuestUserForProductInterest(notificationPreferenceData,
				PRODUCT_INTEREST_CUSTOMER_NAME);
		final String notificationTypeUpper = StringUtils.upperCase(notificationType);
		final Errors errors = new BeanPropertyBindingResult(REQUESTPARAM, REQUESTPARAM);
		// do validations for ProductInterestCreation in local session context for the created or existing registered Product interest user
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getProductInterestsValidator().validateProductInterestCreation(productCode, notificationTypeUpper, errors);
				return null;
			}
		}, user);

		// Save ProductInterest in local session context for the created or existing registered Product interest user.
		final ProductInterestRelationData productInterestRelation = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				return saveProductInterest(productCode, NotificationType.valueOf(notificationTypeUpper));
			}
		}, user);
		return dataMapper.map(productInterestRelation, ProductInterestRelationWsDTO.class, fields);
	}

	/**
	 * Save The Product Interest for input product and customer email id.
	 *
	 * @param productCode
	 *           product code for which Product interest for customer needs to save.
	 * @param notificationType
	 *           type of Notification (ex. 'BACK_IN_STOCK', or COUPON_EXPIRE or 'PRICE_DROP')
	 * @return Saved ProductInterestRelationData object
	 */
	protected ProductInterestRelationData saveProductInterest(final String productCode, final NotificationType notificationType)
	{
		final ProductInterestData productInterestData = this.productInterestFacade
				.getProductInterestDataForCurrentCustomer(productCode, notificationType).orElse(new ProductInterestData());
		if (ObjectUtils.isEmpty(productInterestData.getProduct()))
		{
			final ProductData product = this.productFacade.getProductForCodeAndOptions(productCode,
					Arrays.asList(ProductOption.BASIC));
			productInterestData.setProduct(product);
			productInterestData.setNotificationType(notificationType);

			this.productInterestFacade.saveProductInterest(productInterestData);
		}
		return this.productInterestFacade.getProductInterestRelation(productCode);
	}

	protected void handleErrors(final Errors errors)
	{
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	protected ProductInterestsValidator getProductInterestsValidator()
	{
		return productInterestsValidator;
	}
}
