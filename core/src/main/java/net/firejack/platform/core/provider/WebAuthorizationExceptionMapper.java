/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.provider;

import net.firejack.platform.core.domain.ServerError;
import net.firejack.platform.core.exception.WebAuthorizationException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.MessageResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Component
@Provider
public class WebAuthorizationExceptionMapper implements ExceptionMapper<WebAuthorizationException> {

	@Context
	private HttpHeaders headers;

	@Override
	public Response toResponse(WebAuthorizationException e) {
		String msg = e.getMessage();
		if (StringUtils.isNotBlank(e.getMsgKey())) {
			msg = MessageResolver.messageFormatting(e.getMsgKey(), headers.getLanguage(), e.getMessageArguments());
		}
		ServerError serverError = new ServerError(e.getMsgKey(), msg);
		ServiceResponse responderModel = new ServiceResponse(serverError, "", false);

		return Response.status(e.getStatus()).entity(responderModel).type(headers.getMediaType()).build();
	}

}
