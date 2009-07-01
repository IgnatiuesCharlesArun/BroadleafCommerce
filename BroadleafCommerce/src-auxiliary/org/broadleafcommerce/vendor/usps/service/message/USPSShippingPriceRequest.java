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
package org.broadleafcommerce.vendor.usps.service.message;

import java.util.ArrayList;
import java.util.List;

public class USPSShippingPriceRequest {

    protected List<USPSContainerItem> containerItems = new ArrayList<USPSContainerItem>();

    public List<USPSContainerItem> getContainerItems() {
        return containerItems;
    }

    public void setContainerItems(List<USPSContainerItem> containerItems) {
        this.containerItems = containerItems;
    }

}