
package au.com.bestandless.customerinterestsocc.controllers


import static groovyx.net.http.ContentType.JSON

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import com.fasterxml.jackson.databind.ObjectMapper
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import spock.lang.Unroll

@ManualTest
@Unroll
class BestAndLessCustomerInterestsOccControllerTests extends AbstractUserTest {

    def "#user should be able to register product interest by providing email id with #withId"()
    {
       
        given: "a non registered and non logged in, anonymous #user"
        authorizeTrustedClient(restClient)

        when: "he requests to change notification preferences"
        def response = restClient.post(path: getBasePathWithSite() + "/users/" + withId + "/productinterests/register",
                query: queryParameters,
                body: [
                        "preferences":
                                [
                                        "channel": "EMAIL",
                                        "value"  : "newEmail@test.com",
                                        "enabled": true
                                ]
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "succeed"
        with(response) {
            status == SC_OK
            data.productInterestEntry.size >= 1
            data.product.code == "1978440_blue"
            data.productInterestEntry[0].interestType == "BACK_IN_STOCK"
        }

       where:
        user                     | withId              | authorizationMethod                    | credential  | queryParameters                                                                                    | statusCode      
       "anonymous"              | "anonymous"         | this.&authorizeTrustedClient           | null        | ['fields': "DEFAULT", "notificationType": "BACK_IN_STOCK", "productCode": "1978440_blue"]          | SC_OK           
    }

}
