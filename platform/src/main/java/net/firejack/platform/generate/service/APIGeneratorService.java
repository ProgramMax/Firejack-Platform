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

package net.firejack.platform.generate.service;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.element.action.ActionElement;
import net.firejack.platform.core.config.meta.element.action.ActionParameterElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.generate.beans.Base;
import net.firejack.platform.generate.beans.annotation.Properties;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.api.LocalMethod;
import net.firejack.platform.generate.beans.web.api.LocalService;
import net.firejack.platform.generate.beans.web.api.ServiceParam;
import net.firejack.platform.generate.beans.web.broker.Broker;
import net.firejack.platform.generate.beans.web.model.Model;
import net.firejack.platform.generate.beans.web.model.key.Key;
import net.firejack.platform.generate.beans.web.store.MethodType;
import net.firejack.platform.generate.beans.web.store.Param;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class APIGeneratorService extends BaseGeneratorService implements IAPIGeneratorService {

    @ProgressStatus(weight = 3, description = "Generate API objects")
    public Api generateAPIService(IPackageDescriptor descriptor, List<Model> models, String url, Structure structure) throws IOException {
        String path = DiffUtils.lookup(descriptor.getPath(), descriptor.getName());

        ActionElement[] elements = descriptor.getActionElements();

        Api api = new Api(url, path, descriptor.getName());
        Map<String, LocalService> services = new HashMap<String, LocalService>();

        if (elements != null) {
            for (ActionElement element : elements) {
                Model model = getCacheItem(element.getPath());
                if (model != null && !model.isAbstracts()) {
                    Broker broker = getCacheItem(DiffUtils.lookup(element.getPath(), element.getName()));
                    if (broker != null) {
                        String name = model.getDomainLookup();
                        LocalService service = services.get(name);

                        if (service == null) {
                            service = new LocalService(model);
                            services.put(name, service);
                        }

                        LocalMethod method = new LocalMethod(broker);
                        FieldType type = broker.getType() != null ? broker.getType().getReturnType() : FieldType.OBJECT;
                        method.setReturnType(new Param(null, type, broker.getResponse().getDomain()));
                        service.addImport(broker.getResponse().getDomain());

                        if (element.getSoapMethod() != null) {
                            method.setName(element.getSoapMethod());
                        } else {
                            method.setName(Utils.fieldFormatting(element.getName()));
                        }

                        String urlPath = element.getUrlPath();
                        String entityPath = model.getEntityPath().replaceAll("\\.", "/").trim();
                        String suffix = urlPath.replaceAll(".*/" + model.getNormalize() + "(/.*|)", "$1");

                        if (StringUtils.isNotBlank(entityPath)) entityPath = "/" + entityPath;

                        method.setPath(entityPath + "/" + model.getNormalize() + suffix);
                        method.setSoapPath(element.getSoapUrlPath());
                        method.setDescription(Utils.documentFormatting(element.getDescription(), Properties.DOCUMENT_LENGTH));

                        List<ActionParameterElement> parameters = element.getParameters();
                        if (parameters != null) {
                            for (ActionParameterElement parameter : parameters) {
                                ServiceParam param = new ServiceParam();

                                param.setName(parameter.getName());
                                param.setOrder(parameter.getOrderPosition());
                                param.setLocation(parameter.getLocation());

                                if (!model.isSingle() && parameter.getName().equals("id")) {
                                    Key key = model.getKey();

                                    param.setType(key.getType());
                                    if (key.isComposite())
                                        param.setDomain((Base) key);
                                } else {
                                    param.setType(parameter.getType());
                                }

                                method.addParam(param);
                            }
                        } else if (!broker.getHttpMethod().equals(HTTPMethod.POST) && broker.getType() != null && !broker.isType(MethodType.readAll)) {
                            Key key = model.getKey();

                            ServiceParam param = new ServiceParam();
                            param.setOrder(method.getNextOrder());
                            param.setName("id");
                            param.setType(key.getType());
                            param.setLocation(ParameterTransmissionType.PATH);
                            if (key.isComposite())
                                param.setDomain((Base) key);

                            method.addParam(param);
                        }

                        if (broker.getHttpMethod().equals(HTTPMethod.POST) || broker.getHttpMethod().equals(HTTPMethod.PUT)) {
                            ServiceParam param = new ServiceParam();

                            param.setOrder(method.getNextOrder());
                            param.setName("data");
                            param.setType(FieldType.OBJECT);
                            param.setDomain(broker.getRequest().getDomain());
                            service.addImport(broker.getRequest().getDomain());

                            method.addParam(param);
                        }

                        service.addMethod(method);
                        service.addImport(model.getKey());
                        service.addImport(broker);
                    }
                }
            }
        }

        for (LocalService service : services.values()) {
            service.createService();
            service.createProxy();

            generate(service, "templates/code/server/api/local.vsl", structure.getSrc());
            generate(service.getService(), "templates/code/server/api/service.vsl", structure.getSrc());
            generate(service.getProxy(), "templates/code/server/api/proxy.vsl", structure.getSrc());

            api.addService(service);
        }
        generate(api, "templates/code/server/api/api.vsl", structure.getSrc());
        api.setModels(models);
        return api;
    }
}
