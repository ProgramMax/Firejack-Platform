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

package net.firejack.platform.service.directory.broker.directory;

import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.web.security.directory.DirectoryServiceRepository;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readOrderedDirectoryListBroker")
public class ReadOrderedDirectoryListBroker extends ListBroker<DirectoryModel, Directory, NamedValues> {

	@Autowired
	private IDirectoryStore store;
	@Autowired
	private DirectoryServiceRepository directoryServiceRepository;

	@Override
	protected List<DirectoryModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		return store.findOrderedDirectoryList();
	}

	@Override
	protected List<Directory> result(List<Directory> dtoList, List<DirectoryModel> modelList) {
		for (Directory directory : dtoList) {
			directory.setDirectoryServiceTitle(directoryServiceRepository.getDirectoryServiceTitle(directory.getLookup()));
		}
		return dtoList;
	}
}
