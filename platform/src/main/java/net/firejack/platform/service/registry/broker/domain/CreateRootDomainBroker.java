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

package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.RootDomain;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.store.registry.IRootDomainStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createRootDomainBroker")
public class CreateRootDomainBroker extends SaveBroker<RootDomainModel, RootDomain, RegistryNodeTree> {

	@Autowired
	private IRootDomainStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Root Domain has been created successfully.";
	}

	@Override
	protected RootDomainModel convertToEntity(RootDomain rootDomain) {
		return factory.convertFrom(RootDomainModel.class, rootDomain);
	}

	@Override
	protected RegistryNodeTree convertToModel(RootDomainModel rootDomain) {
		return treeNodeFactory.convertTo(rootDomain);
	}

	@Override
	protected void save(RootDomainModel model) throws Exception {
		store.save(model);
	}
}