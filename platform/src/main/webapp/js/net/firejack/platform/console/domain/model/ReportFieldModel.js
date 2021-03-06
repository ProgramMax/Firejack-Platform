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

Ext.define('OPF.console.domain.model.ReportFieldModel', {
    extend: 'Ext.data.Model',

    statics: {
        pageSuffixUrl: 'console/domain',
        restSuffixUrl: 'registry/report',
        constraintName: 'OPF.registry.ReportField'
    },

    fields: [
        { name: 'id', type: 'int', useNull: true },

        { name: 'displayName', type: 'string' },
//        { name: 'function', type: 'string' },

        { name: 'visible', type: 'boolean' },
        { name: 'searchable', type: 'boolean' },

        { name: 'relationships', type: 'auto' },

        { name: 'created', type: 'int' },
        { name: 'canUpdate', type: 'boolean' },
        { name: 'canDelete', type: 'boolean' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.ReportModel',
            name: 'report',
            associatedName: 'report',
            associatedKey: 'report',
            foreignKey: 'id'
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.FieldModel',
            name: 'field',
            associatedName: 'field',
            associatedKey: 'field',
            foreignKey: 'id'
        },
        {
            type: 'hasMany',
            model: 'OPF.console.domain.model.RelationshipModel',
            name: 'relationships',
            associatedName: 'relationships',
            associatedKey: 'relationships',
            foreignKey: 'id'
        }
    ]

});