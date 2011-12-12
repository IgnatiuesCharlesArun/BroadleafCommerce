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

import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.TaxDetail;
import org.broadleafcommerce.core.order.domain.TaxRateDetail;
import org.broadleafcommerce.core.pricing.service.module.TaxModule;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

public class TaxActivity extends BaseActivity {

    private TaxModule taxModule;

    public ProcessContext execute(ProcessContext context) throws Exception {
        Order order = ((PricingContext)context).getSeedData();
        
        // Reset all taxes for this order
		order.setOrderTax(new TaxDetail());
		for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
			fg.setShippingTax(new TaxDetail());
			fg.setFulfillmentGroupTax(new TaxDetail());
			for (FulfillmentGroupItem fgItem : fg.getFulfillmentGroupItems()) {
				fgItem.setItemTax(new TaxDetail());
				fgItem.setItemTaxRate(new TaxRateDetail());
			}
			for (FulfillmentGroupFee fgFee : fg.getFulfillmentGroupFees()) {
				fgFee.setFeeTax(new TaxDetail());
				fgFee.setFeeTaxRate(new TaxRateDetail());
			}
		}
		
        order = taxModule.calculateTaxForOrder(order);

        context.setSeedData(order);
        return context;
    }

    public TaxModule getTaxModule() {
        return taxModule;
    }

    public void setTaxModule(TaxModule taxModule) {
        this.taxModule = taxModule;
    }

}
