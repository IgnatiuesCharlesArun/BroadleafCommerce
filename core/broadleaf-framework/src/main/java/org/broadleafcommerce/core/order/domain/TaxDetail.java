package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.presentation.AdminPresentation;

@Embeddable
public class TaxDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Column(name = "CITY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="City Tax", group="Tax", order=1, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal cityTax;

    @Column(name = "COUNTY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="County Tax", group="Tax", order=2, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countyTax;

    @Column(name = "STATE_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="State Tax", group="Tax", order=3, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal stateTax;
    
    @Column(name = "DISTRICT_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="District Tax", group="Tax", order=4, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal districtTax;

    @Column(name = "COUNTRY_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="Country Tax", group="Tax", order=5, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countryTax;

    @Column(name = "TOTAL_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="Total Tax", group="Tax", order=6, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    public Money getCityTax() {
        return cityTax == null ? null : new Money(cityTax);
    }

    public void setCityTax(Money cityTax) {
        this.cityTax = Money.toAmount(cityTax);
    }

    public Money getCountyTax() {
        return countyTax == null ? null : new Money(countyTax);
    }

    public void setCountyTax(Money countyTax) {
        this.countyTax = Money.toAmount(countyTax);
    }

    public Money getStateTax() {
        return stateTax == null ? null : new Money(stateTax);
    }

    public void setStateTax(Money stateTax) {
        this.stateTax = Money.toAmount(stateTax);
    }
    
    public Money getDistrictTax() {
        return districtTax == null ? null : new Money(districtTax);
    }

    public void setDistrictTax(Money districtTax) {
        this.districtTax = Money.toAmount(districtTax);
    }

    public Money getCountryTax() {
        return countryTax == null ? null : new Money(countryTax);
    }

    public void setCountryTax(Money countryTax) {
        this.countryTax = Money.toAmount(countryTax);
    }

    public Money getTotalTax() {
        return totalTax == null ? null : new Money(totalTax);
    }

    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }
    
    public void addToCityTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (cityTax == null) {
	    		cityTax = Money.toAmount(new Money(0D));
	    	}
    		cityTax = cityTax.add(Money.toAmount(taxAmount));
    	}
    }
    
    public void addToCountyTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (countyTax == null) {
	    		countyTax = Money.toAmount(new Money(0D));
	    	}
    		countyTax = countyTax.add(Money.toAmount(taxAmount));
    	}	
    }
    
    public void addToDistrictTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (districtTax == null) {
	    		districtTax = Money.toAmount(new Money(0D));
	    	}
    		districtTax = districtTax.add(Money.toAmount(taxAmount));
    	}
    }
    
    public void addToStateTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (stateTax == null) {
	    		stateTax = Money.toAmount(new Money(0D));
	    	}
    		stateTax = stateTax.add(Money.toAmount(taxAmount));
    	}
    }
    	
    public void addToCountryTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (countryTax == null) {
	    		countryTax = Money.toAmount(new Money(0D));
	    	}
    		countryTax = countryTax.add(Money.toAmount(taxAmount));
    	}
    }
    
    public void addToTotalTax(Money taxAmount) {
    	if (taxAmount != null) {
	    	if (totalTax == null) {
	    		totalTax = Money.toAmount(new Money(0D));
	    	}
    		totalTax = totalTax.add(Money.toAmount(taxAmount));
    	}
    }
}
