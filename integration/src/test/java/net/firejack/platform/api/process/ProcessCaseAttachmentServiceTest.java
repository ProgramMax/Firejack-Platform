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

package net.firejack.platform.api.process;


import net.firejack.platform.api.Elements;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.OPFEngineInitializeExecutionListener;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.listener.ProcessExecutionListener;
import net.firejack.platform.api.registry.listener.PackageExecutionListener;
import net.firejack.platform.api.registry.listener.RootDomainExecutionListener;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlame;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service-test-config.xml"})
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
        OPFEngineInitializeExecutionListener.class,
        RootDomainExecutionListener.class,
        PackageExecutionListener.class,
        ProcessExecutionListener.class
})
public class ProcessCaseAttachmentServiceTest {
	protected static Logger logger = Logger.getLogger(ProcessCaseAttachmentServiceTest.class);

	@Value("${sts.base.url}")
	private String baseUrl;

    @Resource(name = "testContextAttributes")
    private Map<Elements, AbstractDTO> testContextAttributes;

	@Before
	public void setUp() {
    }

	@After
	public void tearDown() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

    private Long startCase(String entityType, Long entityId) {
        Process process = (Process) testContextAttributes.get(Elements.PROCESS);
        String processLookup = process.getLookup();

        User user1 = (User) testContextAttributes.get(Elements.USER1);

        CaseOperationsParams startCaseParams = new CaseOperationsParams();
        startCaseParams.setAssigneeId(user1.getId());
        startCaseParams.setAllowNullAssignee(false);
        startCaseParams.setCaseDescription("test_case");
        startCaseParams.setNoteText("note");
        startCaseParams.setProcessLookup(processLookup);

        List<CaseObject> caseObjects = new ArrayList<CaseObject>();
        CaseObject caseObject = new CaseObject();
        caseObject.setEntityId(entityId);
        caseObject.setEntityType(entityType);
        caseObject.setProcessFields(Collections.<ProcessFieldCaseValue>emptyList());
        caseObjects.add(caseObject);
        startCaseParams.setCaseObjects(caseObjects);

        ServiceResponse<SimpleIdentifier<Long>> response = OPFEngine.ProcessService.startCase(startCaseParams);

        Assert.assertNotNull("Response shouldn't be null.", response);
        logger.info(response.getMessage());
        Long caseId = response.getItem().getIdentifier();
        Assert.assertNotNull("Case id shouldn't be null", caseId);

        return caseId;
    }

	@Test
	public void crud() throws IOException {
        Long entityId = 1234l;

        Long caseId = startCase(OpenFlame.SYSTEM, entityId);

        String testFilename = "testFilename";
        byte[] inputData = {'s','r','3','2','q'};

        ServiceResponse serviceResponse = OPFEngine.ProcessService.uploadCaseAttachment(caseId, "testAttachment", "attDesc", testFilename, new ByteArrayInputStream(inputData));
        logger.info(serviceResponse.getMessage());
        Assert.assertTrue("Upload attachment response success should be true", serviceResponse.isSuccess());

        ServiceResponse<CaseAttachment> readAttachmentsResponse = OPFEngine.ProcessService.readCaseAttachmentsByCase(caseId);
        logger.info(readAttachmentsResponse.getMessage());
        Assert.assertEquals("Number of attachments should be 1", new Integer(1), readAttachmentsResponse.getTotal());

        CaseAttachment firstAttachment = readAttachmentsResponse.getData().iterator().next();

        ServiceResponse<FileInfo> downloadAttachmentResponse = OPFEngine.ProcessService.downloadCaseAttachment(firstAttachment.getId());
        logger.info(downloadAttachmentResponse.getMessage());

        FileInfo fileInfo = downloadAttachmentResponse.getItem();

        Assert.assertEquals("Supplied filename should match the read one", testFilename, fileInfo.getFilename());
        for (int i = 0; i < inputData.length; i++) {
            byte b = inputData[i];
            byte readByte = fileInfo.getData()[i];
            Assert.assertTrue("Read data should match the supplied data", b == readByte);
        }

        String testUpdateDesc = "testUpdateDesc";

        firstAttachment.setDescription(testUpdateDesc);
        ServiceResponse<CaseAttachment> updateAttachmentResponse = OPFEngine.ProcessService.updateCaseAttachment(firstAttachment.getId(), firstAttachment);
        logger.info(updateAttachmentResponse.getMessage());
        Assert.assertTrue("Update attachment should return success true", updateAttachmentResponse.isSuccess());

        ServiceResponse<CaseAttachment> readAttachmentsAfterUpdateResponse = OPFEngine.ProcessService.readCaseAttachmentsByCase(caseId);
        logger.info(readAttachmentsAfterUpdateResponse.getMessage());
        CaseAttachment firstUpdatedAttachment = readAttachmentsAfterUpdateResponse.getData().iterator().next();
        Assert.assertEquals("After update attachment description should match the supplied one", testUpdateDesc, firstUpdatedAttachment.getDescription());

        ServiceResponse deleteAttachmentResponse = OPFEngine.ProcessService.deleteCaseAttachment(firstAttachment.getId());
        logger.info(deleteAttachmentResponse.getMessage());
        Assert.assertTrue("Delete attachment should return success true", deleteAttachmentResponse.isSuccess());

        ServiceResponse<CaseAttachment> readAttachmentsAfterDeletionResponse = OPFEngine.ProcessService.readCaseAttachmentsByCase(caseId);
        logger.info(readAttachmentsAfterDeletionResponse.getMessage());
        Assert.assertTrue("Attachment should be null after deletion", readAttachmentsAfterDeletionResponse.getTotal() == 0);
    }
}