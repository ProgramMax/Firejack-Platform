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

/**
 *
 */
Ext.define('OPF.console.inbox.view.processcase.PerformCaseDialog', {
    extend: 'OPF.console.inbox.view.AbstractPerformRollbackDialog',

    title: 'Perform Case',
    objectIdPropertyName: 'caseId',
    restUrl: OPF.core.utils.RegistryNodeType.CASE.generateUrl('/perform-by-id'),
    getTeamMemberRestUrl: OPF.core.utils.RegistryNodeType.CASE.generateUrl('/read-next-team-member/'),
    //isCaseDialog: true,

    constructor: function(id, grid, cfg) {
        cfg = cfg || {};
        OPF.console.inbox.view.processcase.PerformCaseDialog.superclass.constructor.call(this, id, grid, true, true, cfg);
    }

});