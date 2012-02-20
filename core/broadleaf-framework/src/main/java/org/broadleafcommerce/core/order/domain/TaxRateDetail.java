package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

@Embeddable
public class TaxRateDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Column(name = "CITY_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="City Tax Rate", group="Tax", order=7, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal cityTaxRate;

    @Column(name = "COUNTY_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="County Tax Rate", group="Tax", order=8, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countyTaxRate;

    @Column(name = "STATE_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="State Tax Rate", group="Tax", order=9, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal stateTaxRate;
    
    @Column(name = "DISTRICT_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="District Tax Rate", group="Tax", order=10, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal districtTaxRate;

    @Column(name = "COUNTRY_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="Country Tax Rate", group="Tax", order=11, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal countryTaxRate;

    @Column(name = "TOTAL_TAX_RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName="Total Tax Rate", group="Tax", order=12, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTaxRate;

	public BigDecimal getCityTaxRate() {
		return cityTaxRate;
	}

	public void setCityTaxRate(BigDecimal cityTaxRate) {
		this.cityTaxRate = cityTaxRate;
	}

	public BigDecimal getCountyTaxRate() {
		return countyTaxRate;
	}

	public void setCountyTaxRate(BigDecimal countyTaxRate) {
		this.countyTaxRate = countyTaxRate;
	}

	public BigDecimal getStateTaxRate() {
		return stateTaxRate;
	}

	public void setStateTaxRate(BigDecimal stateTaxRate) {
		this.stateTaxRate = stateTaxRate;
	}

	public BigDecimal getDistrictTaxRate() {
		return districtTaxRate;
	}

	public void setDistrictTaxRate(BigDecimal districtTaxRate) {
		this.districtTaxRate = districtTaxRate;
	}

	public BigDecimal getCountryTaxRate() {
		return countryTaxRate;
	}

	public void setCountryTaxRate(BigDecimal countryTaxRate) {
		this.countryTaxRate = countryTaxRate;
	}

	public BigDecimal getTotalTaxRate() {
		return totalTaxRate;
	}

	public void setTotalTaxRate(BigDecimal totalTaxRate) {
		this.totalTaxRate = totalTaxRate;
	}

}
