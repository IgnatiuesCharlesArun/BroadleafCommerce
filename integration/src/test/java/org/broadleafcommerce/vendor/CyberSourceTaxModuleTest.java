/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.vendor;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.pricing.service.module.CyberSourceTaxModule;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.test.BaseTest;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

/**
 * @author jfischer
 *
 */
public class CyberSourceTaxModuleTest extends BaseTest {

	@Resource
    private CyberSourceServiceManager serviceManager;
	
	@Test(groups = { "testSuccessfulCyberSourceTaxModule" })
    @Rollback(false)
    public void testSuccessfulCyberSourceTaxModule() throws Exception {
		if (serviceManager.getMerchantId().equals("?")) {
            return;
        }
		
		CyberSourceTaxModule module = new CyberSourceTaxModule();
		module.setServiceManager(serviceManager);
		ArrayList<String> nexus = new ArrayList<String>();
		nexus.add("TX");
		module.setNexus(nexus);
		module.setOrderAcceptanceCity("Dallas");
		module.setOrderAcceptanceCountry("US");
		module.setOrderAcceptancePostalCode("75244");
		module.setOrderAcceptanceState("TX");
		
		PaymentInfo paymentInfo = createPaymentInfo("8335 Westchester Drive", "Dallas", "US", "John", "Test", "75225", "TX");
		Order order = new OrderImpl();
		order.setEmailAddress("null@cybersource.com");
		paymentInfo.setOrder(order);
		order.getPaymentInfos().add(paymentInfo);
		
		DiscreteOrderItem item1 = new DiscreteOrderItemImpl();
		Sku sku1 = new SkuImpl();
		sku1.setName("a72345b");
		sku1.setDescription("Test Description Product 1");
		sku1.setTaxable(true);
		item1.setSku(sku1);
		item1.setPrice(new Money(10D));
		
		FulfillmentGroup fg1 = new FulfillmentGroupImpl();
		fg1.setId(1L);
		FulfillmentGroupItem fgi1 = new FulfillmentGroupItemImpl();
		fgi1.setOrderItem(item1);
		fgi1.setQuantity(2);
		fg1.addFulfillmentGroupItem(fgi1);
		fg1.setAddress(createDestinationAddress("14930 Midway Rd", "Dallas", "US", "John", "Test", "75001", "TX"));
		order.getFulfillmentGroups().add(fg1);
		
		DiscreteOrderItem item2 = new DiscreteOrderItemImpl();
		Sku sku2 = new SkuImpl();
		sku2.setName("a72345c");
		sku2.setDescription("Test Description Product 2");
		sku2.setTaxable(true);
		item2.setSku(sku2);
		item2.setPrice(new Money(30D));
		
		FulfillmentGroup fg2 = new FulfillmentGroupImpl();
		fg2.setId(2L);
		FulfillmentGroupItem fgi2 = new FulfillmentGroupItemImpl();
		fgi2.setOrderItem(item2);
		fgi2.setQuantity(1);
		fg2.addFulfillmentGroupItem(fgi2);
		fg2.setAddress(createDestinationAddress("14999 Monfort Drive", "Dallas", "US", "John", "Test", "75254", "TX"));
		order.getFulfillmentGroups().add(fg2);
		order.setTotal(new Money(50D));
		
		assert(order.getOrderTax() == null || order.getOrderTax().getTotalTax() == null);
		order = module.calculateTaxForOrder(order);
		assert(fgi1.getItemTax().getTotalTax() != null && fgi1.getItemTax().getTotalTax().greaterThan(new Money(0D)));
		assert(fgi2.getItemTax().getTotalTax() != null && fgi2.getItemTax().getTotalTax().greaterThan(new Money(0D)));
		//assertTaxSumsAreCorrect(order);
	}
	
	private void assertTaxSumsAreCorrect(Order order) {
    	Money cityTaxAmount = new Money(0D);
    	Money countyTaxAmount = new Money(0D);
    	Money districtTaxAmount = new Money(0D);
    	Money stateTaxAmount = new Money(0D);
    	Money countryTaxAmount = new Money(0D);
    	Money totalTaxAmount = new Money(0D);
    	
    	for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
	    	Money fgCityTaxAmount = new Money(0D);
	    	Money fgCountyTaxAmount = new Money(0D);
	    	Money fgDistrictTaxAmount = new Money(0D);
	    	Money fgStateTaxAmount = new Money(0D);
	    	Money fgCountryTaxAmount = new Money(0D);
	    	Money fgTotalTaxAmount = new Money(0D);
	    	
	    	// Add the shipping taxes into the order total taxes
    		cityTaxAmount = cityTaxAmount.add(fg.getShippingTax().getCityTax());
    		countyTaxAmount = countyTaxAmount.add(fg.getShippingTax().getCountyTax());
    		districtTaxAmount = districtTaxAmount.add(fg.getShippingTax().getDistrictTax());
    		stateTaxAmount = stateTaxAmount.add(fg.getShippingTax().getStateTax());
    		countryTaxAmount = countryTaxAmount.add(fg.getShippingTax().getCountryTax());
    		totalTaxAmount = totalTaxAmount.add(fg.getShippingTax().getTotalTax());
	    	
	    	// Add the shipping taxes into this fulfillment group's taxes
    		fgCityTaxAmount = fgCityTaxAmount.add(fg.getShippingTax().getCityTax());
    		fgCountyTaxAmount = fgCountyTaxAmount.add(fg.getShippingTax().getCountyTax());
    		fgDistrictTaxAmount = fgDistrictTaxAmount.add(fg.getShippingTax().getDistrictTax());
    		fgStateTaxAmount = fgStateTaxAmount.add(fg.getShippingTax().getStateTax());
    		fgCountryTaxAmount = fgCountryTaxAmount.add(fg.getShippingTax().getCountryTax());
    		fgTotalTaxAmount = fgTotalTaxAmount.add(fg.getShippingTax().getTotalTax());
	    	
	    	for (FulfillmentGroupItem item : fg.getFulfillmentGroupItems()) {
	    		// Add the item tax into the total order tax
	    		cityTaxAmount = cityTaxAmount.add(item.getItemTax().getCityTax());
	    		countyTaxAmount = countyTaxAmount.add(item.getItemTax().getCountyTax());
	    		districtTaxAmount = districtTaxAmount.add(item.getItemTax().getDistrictTax());
	    		stateTaxAmount = stateTaxAmount.add(item.getItemTax().getStateTax());
	    		countryTaxAmount = countryTaxAmount.add(item.getItemTax().getCountryTax());
	    		totalTaxAmount = totalTaxAmount.add(item.getItemTax().getTotalTax());
	    		
	    		// Add the item tax into this fulfillment group's taxes
	    		fgCityTaxAmount = fgCityTaxAmount.add(item.getItemTax().getCityTax());
	    		fgCountyTaxAmount = fgCountyTaxAmount.add(item.getItemTax().getCountyTax());
	    		fgDistrictTaxAmount = fgDistrictTaxAmount.add(item.getItemTax().getDistrictTax());
	    		fgStateTaxAmount = fgStateTaxAmount.add(item.getItemTax().getStateTax());
	    		fgCountryTaxAmount = fgCountryTaxAmount.add(item.getItemTax().getCountryTax());
	    		fgTotalTaxAmount = fgTotalTaxAmount.add(item.getItemTax().getTotalTax());
	    	}
	    	
	    	for (FulfillmentGroupFee fee : fg.getFulfillmentGroupFees()) {
	    		// Add the fee tax into the total order tax
	    		cityTaxAmount = cityTaxAmount.add(fee.getFeeTax().getCityTax());
	    		countyTaxAmount = countyTaxAmount.add(fee.getFeeTax().getCountyTax());
	    		districtTaxAmount = districtTaxAmount.add(fee.getFeeTax().getDistrictTax());
	    		stateTaxAmount = stateTaxAmount.add(fee.getFeeTax().getStateTax());
	    		countryTaxAmount = countryTaxAmount.add(fee.getFeeTax().getCountryTax());
	    		totalTaxAmount = totalTaxAmount.add(fee.getFeeTax().getTotalTax());
	    		
	    		// Add the fee tax into this fulfillment group's taxes
	    		fgCityTaxAmount = fgCityTaxAmount.add(fee.getFeeTax().getCityTax());
	    		fgCountyTaxAmount = fgCountyTaxAmount.add(fee.getFeeTax().getCountyTax());
	    		fgDistrictTaxAmount = fgDistrictTaxAmount.add(fee.getFeeTax().getDistrictTax());
	    		fgStateTaxAmount = fgStateTaxAmount.add(fee.getFeeTax().getStateTax());
	    		fgCountryTaxAmount = fgCountryTaxAmount.add(fee.getFeeTax().getCountryTax());
	    		fgTotalTaxAmount = fgTotalTaxAmount.add(fee.getFeeTax().getTotalTax());
	    	}
	    	
	    	// Assert that this fulfillment group's taxes are correct
	    	assert(fgCityTaxAmount.equals(fg.getFulfillmentGroupTax().getCityTax()));
	    	assert(fgCountyTaxAmount.equals(fg.getFulfillmentGroupTax().getCountyTax()));
	    	assert(fgDistrictTaxAmount.equals(fg.getFulfillmentGroupTax().getDistrictTax()));
	    	assert(fgStateTaxAmount.equals(fg.getFulfillmentGroupTax().getStateTax()));
	    	assert(fgCountryTaxAmount.equals(fg.getFulfillmentGroupTax().getCountryTax()));
	    	assert(fgTotalTaxAmount.equals(fg.getFulfillmentGroupTax().getTotalTax()));
    	}
    	
    	assert(cityTaxAmount.equals(order.getOrderTax().getCityTax()));
    	assert(countyTaxAmount.equals(order.getOrderTax().getCountyTax()));
    	assert(districtTaxAmount.equals(order.getOrderTax().getDistrictTax()));
    	assert(stateTaxAmount.equals(order.getOrderTax().getStateTax()));
    	assert(countryTaxAmount.equals(order.getOrderTax().getCountryTax()));
    	assert(totalTaxAmount.equals(order.getOrderTax().getTotalTax()));
	}
	
	private PaymentInfo createPaymentInfo(String line1, String city, final String country, String name, String lastName, String postalCode, final String state) {
		PaymentInfo paymentInfo = new PaymentInfoImpl();
		Address address = new AddressImpl();
		address.setAddressLine1(line1);
		address.setCity(city);
		address.setCountry(new CountryImpl() {
			@Override
			public String getAbbreviation() {
				return country;
			}
		}
		);
		address.setFirstName(name);
		address.setLastName(lastName);
		address.setPostalCode(postalCode);
		address.setState(new StateImpl() {
			@Override
			public String getAbbreviation() {
				return state;
			}
		});
		paymentInfo.setAddress(address);
		paymentInfo.setCustomerIpAddress("10.7.111.111");
		
		return paymentInfo;
	}
	
	private Address createDestinationAddress(String line1, String city, final String country, String name, String lastName, String postalCode, final String state) {
		Address address = new AddressImpl();
		address.setAddressLine1(line1);
		address.setCity(city);
		address.setCountry(new CountryImpl() {
			@Override
			public String getAbbreviation() {
				return country;
			}
		}
		);
		address.setFirstName(name);
		address.setLastName(lastName);
		address.setPostalCode(postalCode);
		address.setState(new StateImpl() {
			@Override
			public String getAbbreviation() {
				return state;
			}
		});
		return address;
	}
	
}