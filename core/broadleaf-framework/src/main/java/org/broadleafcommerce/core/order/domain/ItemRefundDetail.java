package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.broadleafcommerce.openadmin.audit.Auditable;
import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;
import org.broadleafcommerce.presentation.AdminPresentation;

@Embeddable
public class ItemRefundDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Column(name = "QTY_REFUNDED")
    @AdminPresentation(friendlyName="Quantity Refunded", group="Refund", order=1, fieldType=SupportedFieldType.INTEGER)
	protected Integer quantityRefunded;
    
    @Column(name = "AMT_REFUNDED", precision=19, scale=5)
    @AdminPresentation(friendlyName="Amount Refunded", group="Refund", order=2, fieldType=SupportedFieldType.MONEY)
	protected BigDecimal amountRefunded;
    
    @Column(name = "TAX_REFUNDED", precision=19, scale=5)
    @AdminPresentation(friendlyName="Tax Refunded", group="Refund", order=3, fieldType=SupportedFieldType.MONEY)
	protected BigDecimal taxRefunded;
    
    @Column(name = "REASON_CODE")
    @AdminPresentation(friendlyName="Reason Code", group="Refund", order=4, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION)
	protected ItemRefundReason reasonCode;
    
    @Column(name = "REASON_DESC")
    @AdminPresentation(friendlyName="Reason Description", group="Refund", order=5, fieldType=SupportedFieldType.STRING)
	protected String reasonDescription;
	
    @Embedded
    protected Auditable auditable = new Auditable();

	public Integer getQuantityRefunded() {
		return quantityRefunded;
	}

	public void setQuantityRefunded(Integer quantityRefunded) {
		this.quantityRefunded = quantityRefunded;
	}

	public BigDecimal getAmountRefunded() {
		return amountRefunded;
	}

	public void setAmountRefunded(BigDecimal amountRefunded) {
		this.amountRefunded = amountRefunded;
	}

	public BigDecimal getTaxRefunded() {
		return taxRefunded;
	}

	public void setTaxRefunded(BigDecimal taxRefunded) {
		this.taxRefunded = taxRefunded;
	}

	public ItemRefundReason getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(ItemRefundReason reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonDescription() {
		return reasonDescription;
	}

	public void setReasonDescription(String reasonDescription) {
		this.reasonDescription = reasonDescription;
	}

	public Auditable getAuditable() {
		return auditable;
	}

	public void setAuditable(Auditable auditable) {
		this.auditable = auditable;
	}
	
}
