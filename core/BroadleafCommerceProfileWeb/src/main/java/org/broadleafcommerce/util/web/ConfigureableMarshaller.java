package org.broadleafcommerce.util.web;

import java.util.Map;

import org.springframework.oxm.Marshaller;

public interface ConfigureableMarshaller extends Marshaller {
	
	public void assignThreadLocalParams(Map model);
	
	public Map retrieveThreadLocalParams();
	
	public void clearThreadLocalParams();

}
