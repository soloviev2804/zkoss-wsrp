package com.otr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.web.portlet.RenderHttpServletRequest;
import org.zkoss.web.portlet.ResourceHttpServletRequest;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.web.util.resource.ClassWebResource;

import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Обработка урлов в режиме портлета
 * основные мысли как работать с урлами взяты из
 * http://www.oracle.com/technetwork/java/jsr286-2-141964.html
 * @author solovyev.vladimir
 * @since 21.05.2015
 */
public class PortletBridgeURLEncoder implements Encodes.URLEncoder {
	private static final Logger log = LoggerFactory.getLogger(PortletBridgeURLEncoder.class);
	// См http://docs.oracle.com/cd/E16764_01/webcenter.1111/e10148/jpsdg_java_adv.htm#BABGACEG
	// How to Implement Stateless Resource Proxying
	public static final String ORACLE_PORTLET_SERVER_USE_STATELESS_PROXYING = "oracle.portlet.server.useStatelessProxying";
	public static final String ORACLE_WEBCENTER_PORLET_RESPONSE = "oracle.webcenter.porlet.response";

	@Override
	public String encodeURL(final ServletContext ctx, final ServletRequest request, ServletResponse response, String url, Encodes.URLEncoder defaultEncoder) throws Exception {
		if (request instanceof RenderHttpServletRequest) {
			final RenderResponse renderResponse = (RenderResponse) request.getAttribute(ORACLE_WEBCENTER_PORLET_RESPONSE);
			HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response) {
				@Override
				public String encodeURL(String url) {
					//Если статический ресурс
					if (url.contains(ClassWebResource.PATH_PREFIX) || url.endsWith("dsp")) {
						// Ресурсы внутри короторых надо делать rewrite урлов
						if (url.endsWith("wcs") || url.endsWith("zk.wpd")) {
							ResourceURL resourceURL = renderResponse.createResourceURL();
							resourceURL.setResourceID(url);
							resourceURL.setCacheability(ResourceURL.FULL);
							return resourceURL.toString();
						} else {
							request.setAttribute(ORACLE_PORTLET_SERVER_USE_STATELESS_PROXYING, Boolean.TRUE);
							return renderResponse.encodeURL(url);
						}
					} else {
						ResourceURL resourceURL = renderResponse.createResourceURL();
						resourceURL.setResourceID(url);
						return resourceURL.toString();
					}
				}
			};

			return defaultEncoder.encodeURL(ctx, request, wrapper, url, defaultEncoder);
		} else if (request instanceof ResourceHttpServletRequest) {
			final ResourceResponse resourceResponse = (ResourceResponse) request.getAttribute(ORACLE_WEBCENTER_PORLET_RESPONSE);
			HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response) {
				@Override
				public String encodeURL(String url) {
					String updateUrl = ctx.getContextPath() + "/zkau";
					String webResouceUrl = updateUrl + ClassWebResource.PATH_PREFIX;
					boolean isAuExtension = url.startsWith(updateUrl) && !url.startsWith(webResouceUrl);
					if (isAuExtension) {
						ResourceURL resourceURL = resourceResponse.createResourceURL();
						resourceURL.setResourceID(url);
						return resourceURL.toString();
					} else {
						// Запрос ресурса через javax.portlet.ResourceServingPortlet.serveResource()
						// zk.wcs zk.wpd файлы внутри которых есть урлы для rewrite
						request.setAttribute(ORACLE_PORTLET_SERVER_USE_STATELESS_PROXYING, Boolean.TRUE);
						if (!url.startsWith("/")) {
							// если не сделать java.lang.IllegalArgumentException: Relative path [1.jpg] must start with a '/'
							url = ctx.getContextPath() + "/" + url;
						}
						return super.encodeURL(url);
					}
				}
			};
			return defaultEncoder.encodeURL(ctx, request, wrapper, url, defaultEncoder);
		}

		return defaultEncoder.encodeURL(ctx, request, response, url, defaultEncoder);
	}

}
