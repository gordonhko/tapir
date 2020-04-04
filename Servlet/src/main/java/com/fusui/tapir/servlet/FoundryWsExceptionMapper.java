package com.fusui.tapir.servlet;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.FoundryWsErrorMessage;
import com.fusui.tapir.common.dto.FoundryConstants;

/*
 * @author gko
 */

// http://stackoverflow.com/questions/15185299/jax-rs-jersey-exceptionmappers-user-defined-exception
// http://www.codingpedia.org/ama/error-handling-in-rest-api-with-jersey/

public class FoundryWsExceptionMapper implements ExceptionMapper<Throwable> {

	//@Override
	public Response toResponse(Throwable t) {

		int code = FoundryConstants.ERROR_CODE_SYSTEM;
		if ( t instanceof TapirException ) {
			code = FoundryConstants.ERROR_CODE_APPLICATION;
		}
		
		FoundryWsErrorMessage errorMessage = new FoundryWsErrorMessage();
		errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		errorMessage.setCode(code);
		errorMessage.setMessage(t.getMessage());
		StringWriter errorStackTrace = new StringWriter();
		t.printStackTrace(new PrintWriter(errorStackTrace));
		errorMessage.setDeveloperMessage(errorStackTrace.toString());
//		errorMessage.setLink(AppConstants.BLOG_POST_URL);
		return Response.status(errorMessage.getStatus()).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();

	}

}
