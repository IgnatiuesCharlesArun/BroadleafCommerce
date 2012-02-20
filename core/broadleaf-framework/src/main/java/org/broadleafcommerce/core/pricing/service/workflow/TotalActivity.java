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

package org.broadleafcommerce.core.pricing.service.workflow;

import java.math.BigDecimal;

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.common.money.Money;

public class TotalActivity extends BaseActivity {

    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext) context).getSeedData();
        
        setTaxSums(order);
        
        Money total = new Money(BigDecimal.ZERO);
        total = total.add(order.getSubTotal());
        total = total.subtract(order.getOrderAdjustmentsValue());
        total = total.add(order.getTotalShipping());
        // There may not be any taxes on the order
        if (order.getOrderTax() != null && order.getOrderTax().getTotalTax() != null) {
        	total = total.add(order.getOrderTax().getTotalTax());
        }

        Money fees = new Money(BigDecimal.ZERO);
        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotal = new Money(0D);
            fgTotal = fgTotal.add(fulfillmentGroup.getMerchandiseTotal());
            fgTotal = fgTotal.add(fulfillmentGroup.getShippingPrice());
            fgTotal = fgTotal.add(fulfillmentGroup.getFulfillmentGroupTax().getTotalTax());
            
            for (FulfillmentGroupFee fulfillmentGroupFee : fulfillmentGroup.getFulfillmentGroupFees()) {
                fgTotal = fgTotal.add(fulfillmentGroupFee.getAmount());
                fees = fees.add(fulfillmentGroupFee.getAmount());
            }
            
            fulfillmentGroup.setTotal(fgTotal);
        }

        total = total.add(fees);
        order.setTotal(total);
        
        context.setSeedData(order);
        return context;
    }
    
    protected void setTaxSums(Order order) {
    	for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
	    	
	    	// Add the shipping taxes into the order total taxes
    		order.getOrderTax().addToCityTax(fg.getShippingTax().getCityTax());
    		order.getOrderTax().addToCountyTax(fg.getShippingTax().getCountyTax());
    		order.getOrderTax().addToDistrictTax(fg.getShippingTax().getDistrictTax());
    		order.getOrderTax().addToStateTax(fg.getShippingTax().getStateTax());
    		order.getOrderTax().addToCountryTax(fg.getShippingTax().getCountryTax());
    		order.getOrderTax().addToTotalTax(fg.getShippingTax().getTotalTax());
	    	
	    	// Add the shipping taxes into this fulfillment group's taxes
    		fg.getFulfillmentGroupTax().addToCityTax(fg.getShippingTax().getCityTax());
    		fg.getFulfillmentGroupTax().addToCountyTax(fg.getShippingTax().getCountyTax());
    		fg.getFulfillmentGroupTax().addToDistrictTax(fg.getShippingTax().getDistrictTax());
    		fg.getFulfillmentGroupTax().addToStateTax(fg.getShippingTax().getStateTax());
    		fg.getFulfillmentGroupTax().addToCountryTax(fg.getShippingTax().getCountryTax());
    		fg.getFulfillmentGroupTax().addToTotalTax(fg.getShippingTax().getTotalTax());
	    	
	    	for (FulfillmentGroupItem item : fg.getFulfillmentGroupItems()) {
	    		// Add the item tax into the total order tax
	    		if (item.getItemTax().getCityTax() != null) order.getOrderTax().addToCityTax(item.getItemTax().getCityTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getCountyTax() != null) order.getOrderTax().addToCountyTax(item.getItemTax().getCountyTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getDistrictTax() != null) order.getOrderTax().addToDistrictTax(item.getItemTax().getDistrictTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getStateTax() != null) order.getOrderTax().addToStateTax(item.getItemTax().getStateTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getCountryTax() != null) order.getOrderTax().addToCountryTax(item.getItemTax().getCountryTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getTotalTax() != null) order.getOrderTax().addToTotalTax(item.getItemTax().getTotalTax().multiply(item.getQuantity()));
	    		
	    		// Add the item tax into this fulfillment group's taxes
	    		if (item.getItemTax().getCityTax() != null) fg.getFulfillmentGroupTax().addToCityTax(item.getItemTax().getCityTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getCountyTax() != null) fg.getFulfillmentGroupTax().addToCountyTax(item.getItemTax().getCountyTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getDistrictTax() != null) fg.getFulfillmentGroupTax().addToDistrictTax(item.getItemTax().getDistrictTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getStateTax() != null) fg.getFulfillmentGroupTax().addToStateTax(item.getItemTax().getStateTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getCountryTax() != null) fg.getFulfillmentGroupTax().addToCountryTax(item.getItemTax().getCountryTax().multiply(item.getQuantity()));
	    		if (item.getItemTax().getTotalTax() != null) fg.getFulfillmentGroupTax().addToTotalTax(item.getItemTax().getTotalTax().multiply(item.getQuantity()));
	    	}
	    	
	    	for (FulfillmentGroupFee fee : fg.getFulfillmentGroupFees()) {
	    		// Add the fee tax into the total order tax
	    		if (fee.getFeeTax().getCityTax() != null) order.getOrderTax().addToCityTax(fee.getFeeTax().getCityTax());
	    		if (fee.getFeeTax().getCountyTax() != null) order.getOrderTax().addToCountyTax(fee.getFeeTax().getCountyTax());
	    		if (fee.getFeeTax().getDistrictTax() != null) order.getOrderTax().addToDistrictTax(fee.getFeeTax().getDistrictTax());
	    		if (fee.getFeeTax().getStateTax() != null) order.getOrderTax().addToStateTax(fee.getFeeTax().getStateTax());
	    		if (fee.getFeeTax().getCountryTax() != null) order.getOrderTax().addToCountryTax(fee.getFeeTax().getCountryTax());
	    		if (fee.getFeeTax().getTotalTax() != null) order.getOrderTax().addToTotalTax(fee.getFeeTax().getTotalTax());
	    		
	    		// Add the fee tax into this fulfillment group's taxes
	    		if (fee.getFeeTax().getCityTax() != null) fg.getFulfillmentGroupTax().addToCityTax(fee.getFeeTax().getCityTax());
	    		if (fee.getFeeTax().getCountyTax() != null) fg.getFulfillmentGroupTax().addToCountyTax(fee.getFeeTax().getCountyTax());
	    		if (fee.getFeeTax().getDistrictTax() != null) fg.getFulfillmentGroupTax().addToDistrictTax(fee.getFeeTax().getDistrictTax());
	    		if (fee.getFeeTax().getStateTax() != null) fg.getFulfillmentGroupTax().addToStateTax(fee.getFeeTax().getStateTax());
	    		if (fee.getFeeTax().getCountryTax() != null) fg.getFulfillmentGroupTax().addToCountryTax(fee.getFeeTax().getCountryTax());
	    		if (fee.getFeeTax().getTotalTax() != null) fg.getFulfillmentGroupTax().addToTotalTax(fee.getFeeTax().getTotalTax());
	    	}
    	}
    }

}
