package org.broadleafcommerce.util.web;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

public class ContextModelKeyMarshallingView extends AbstractView implements ApplicationContextAware {
	
	/**
	 * Default content type. Overridable as bean property.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/xml";

	private ConfigureableMarshaller marshaller;

	public ContextModelKeyMarshallingView() {
		setContentType(DEFAULT_CONTENT_TYPE);
	}

	public ContextModelKeyMarshallingView(ConfigureableMarshaller marshaller) {
		Assert.notNull(marshaller, "'marshaller' must not be null");
		setContentType(DEFAULT_CONTENT_TYPE);
		this.marshaller = marshaller;
	}

	public void setMarshaller(ConfigureableMarshaller marshaller) {
		Assert.notNull(marshaller, "'marshaller' must not be null");
		this.marshaller = marshaller;
	}

	@Override
	protected void initApplicationContext() throws BeansException {
		Assert.notNull(marshaller, "Property 'marshaller' is required");
	}

	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Object toBeMarshalled = locateToBeMarshalled(model);
		if (toBeMarshalled == null) {
			throw new ServletException("Unable to locate object to be marshalled in model: " + model);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
		try {
			marshaller.assignThreadLocalParams(model);
			marshaller.marshal(toBeMarshalled, new StreamResult(bos));
		} finally {
			marshaller.clearThreadLocalParams();
		}

		response.setContentType(getContentType());
		response.setContentLength(bos.size());

		FileCopyUtils.copy(bos.toByteArray(), response.getOutputStream());
	}

	protected Object locateToBeMarshalled(Map model) throws ServletException {
		Object modelKey = model.get("_modelKey");
		if (modelKey != null) {
			Object o = model.get(modelKey);
			if (o == null) {
				throw new ServletException("Model contains no object with key [" + modelKey + "]");
			}
			if (!marshaller.supports(o.getClass())) {
				throw new ServletException("Model object [" + o + "] retrieved via key [" + modelKey +
						"] is not supported by the Marshaller");
			}
			return o;
		}
		for (Object o : model.values()) {
			if (o != null && marshaller.supports(o.getClass())) {
				return o;
			}
		}
		return null;
	}


}
