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

Ext.define('OPF.console.domain.model.RelationshipModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/relationship',
        editorClassName: 'OPF.console.domain.view.RelationshipEditor',
        constraintName: 'OPF.registry.Relationship'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'name', type: 'string' },
        { name: 'path', type: 'string' },
        { name: 'lookup', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'type', type: 'string' },

        { name: 'parentId', type: 'int' },

        { name: 'relationshipType', type: 'string' },
        { name: 'relationshipTypeName', type: 'string' },
        { name: 'sourceEntityId', type: 'int' },
        { name: 'sourceEntityName', type: 'string' },
        { name: 'targetEntityId', type: 'int', useNull: true },
        { name: 'targetEntityName', type: 'string', useNull: true },
        { name: 'required', type: 'boolean' },


        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.EntityModel',
            name: 'targetEntity',
            associatedName: 'targetEntity',
            associationKey: 'targetEntity',
            foreignKey: 'id'
        }
    ]
});