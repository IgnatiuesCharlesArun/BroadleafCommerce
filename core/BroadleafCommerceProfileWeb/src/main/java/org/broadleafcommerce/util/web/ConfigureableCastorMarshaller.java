package org.broadleafcommerce.util.web;

import java.util.Map;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.oxm.castor.CastorMarshaller;

public class ConfigureableCastorMarshaller extends CastorMarshaller implements ConfigureableMarshaller {
	
	private static ThreadLocal<Map> MODEL = new ThreadLocal<Map>();
	
	public void assignThreadLocalParams(Map model) {
		MODEL.set(model);
	}
	
	public Map retrieveThreadLocalParams() {
		return MODEL.get();
	}
	
	public void clearThreadLocalParams() {
		MODEL.remove();
	}
	
	private Map additionalConfig;

	@Override
	protected void customizeMarshaller(Marshaller marshaller) {
		super.customizeMarshaller(marshaller);
		Map model = retrieveThreadLocalParams();
		if (model != null) {
			String modelKey = (String) model.get("_modelKey");
			if (modelKey != null) {
				Map config = (Map) getAdditionalConfig().get(modelKey);
				if (config != null) {
					String schemaLocation = (String) config.get("schemaLocation");
					if (schemaLocation != null) {
						marshaller.setNoNamespaceSchemaLocation(schemaLocation);
					}
					/*Map<String, String> namespaceMappings = (Map<String, String>) config.get("namespaceMappings");
					if (namespaceMappings != null) {
						for (Map.Entry<String, String> entry : namespaceMappings.entrySet()) {
							marshaller.setNamespaceMapping(entry.getKey(), entry.getValue());
						}
					}*/
				}
			}
		}
		
	}

	@Override
	protected void customizeUnmarshaller(Unmarshaller unmarshaller) {
		super.customizeUnmarshaller(unmarshaller);
	}

	public Map getAdditionalConfig() {
		return additionalConfig;
	}

	public void setAdditionalConfig(Map additionalConfig) {
		this.additionalConfig = additionalConfig;
	}

}
