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

Ext.define('OPF.console.domain.model.CaseObjectModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'process/case-object',
        constraintName: 'OPF.process.CaseObject'
    },

    idProperty: 'id',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'entityId', type: 'int' },
        { name: 'entityType', type: 'string' },
        {
            name: 'updateDate',
            type: 'string',
            convert: OPF.convertDate,
            useNull: false,
            persist: true,
            dateFormat: 'time'
        }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.inbox.model.TaskModel',
            name: 'task',
            associatedName: 'task',
            associationKey: 'task',
            foreignKey: 'id',
            displayName: 'Task',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.inbox.model.CaseModel',
            name: 'processCase',
            associatedName: 'processCase',
            associationKey: 'processCase',
            foreignKey: 'id',
            displayName: 'Case',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'createdBy',
            associatedName: 'createdBy',
            associationKey: 'createdBy',
            foreignKey: 'id',
            displayName: 'Created By',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.directory.model.UserModel',
            name: 'updatedBy',
            associatedName: 'updatedBy',
            associationKey: 'updatedBy',
            foreignKey: 'id',
            displayName: 'Updated By',
            displayDescription: '',
            searchMode: []
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.StatusModel',
            name: 'status',
            associatedName: 'status',
            associationKey: 'status',
            foreignKey: 'id',
            displayName: 'Status',
            displayDescription: ''
        }
    ]

});