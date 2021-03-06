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


Ext.define('OPF.core.component.resource.ImageResourceEditor', {
    extend: 'Ext.window.Window',

    id: 'imageResourceEditorWindow',
    title: 'Edit Image Resource',
    closeAction: 'hide',
    modal: true,
    minWidth: 530,
    minHeight: 200,
//    layout: 'vbox',
//    layoutConfig: {
//        align: 'center'
//    },
    constrainHeader: true,

    imageWidth: null,
    imageHeight: null,

    editingEl: null,

    constructor: function(id, cfg) {
        cfg = cfg || {};
        OPF.core.component.resource.ImageResourceEditor.superclass.constructor.call(this, Ext.apply({
            id: id
        }, cfg));
    },

    initComponent: function() {
        var instance = this;

        this.imageWidthField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageWidthField'
        });

        this.imageHeightField = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'imageHeightField'
        });

        this.resourceFileTemporaryName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileTemporaryName'
        });

        this.resourceFileOriginalName = Ext.ComponentMgr.create({
            xtype: 'hidden',
            name: 'resourceFileOriginalName'
        });

        this.imageContainer = new Ext.Component({
            html: 'Loading...'
        });

        this.cultureCombo = new OPF.core.component.CultureComboBox();

        this.uploadFileDialog = new OPF.core.component.UploadFileDialog(this, {
            fileTypes: ['jpg', 'jpeg', 'png', 'gif'],
            uploadUrl: OPF.Cfg.restUrl('content/resource/upload/image'),

            successUploaded: function(jsonData) {
                var data = jsonData.data[0];
                instance.resourceFileOriginalName.setValue(data.orgFilename);
                instance.resourceFileTemporaryName.setValue(data.filename);
                var url = OPF.Cfg.restUrl('content/resource/tmp/' + data.filename);
                instance.imageWidthField.setValue(data.width);
                instance.imageHeightField.setValue(data.height);
                instance.loadImageContainer(url, data.width, data.height);
                instance.saveButton.enable();
            }
        });

        this.saveButton = new Ext.Button({
            text: 'Save',
            disabled: true,
            handler: function () {
                instance.save();
            }
        });

        this.bbar = [
            {
                xtype: 'button',
                text: 'Upload new image',
                handler: function () {
                    instance.uploadFileDialog.show();
                }
            },
            ' ',
            'Language:',
            this.cultureCombo,
            '->',
            this.saveButton,
            ' ',
            {
                xtype: 'button',
                text: 'Close',
                handler: function () {
                    instance.hide();
                }
            }
        ];
        
        this.items = [
            this.imageContainer
        ];

        this.callParent(arguments);
    },

    setEditingEl: function(editingEl) {
        this.editingEl = editingEl;
    },

    setIdData: function(idData) {
        this.idData = idData;
    },

    setSizeAccordingToImg: function() {
        var winWidth = this.imageContainer.getSize().width + 14;
        var winHeight = this.imageContainer.getSize().height + 69;

        if (winWidth < this.minWidth) {
            winWidth = this.minWidth;
        }
        if (winHeight < this.minHeight) {
            winHeight = this.minHeight;
        }

        this.setSize({
            width: winWidth,
            height: winHeight
        });

        this.doLayout();
    },

    show: function() {
        var imgSize = this.editingEl.getSize();
        var component = OPF.core.component.resource.ImageResourceEditor.superclass.show.call(this);
        this.imageContainer.update(this.editingEl.el.dom.innerHTML);
        this.setSizeAccordingToImg();
        return component;
    },

    loadImageContainer: function(url, origWidth, origHeight) {
        var me = this;
        var imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, this.imageWidth, this.imageHeight);
        this.imageContainer.update(imageHtml);
        new Ext.util.DelayedTask(function() {
            me.setSizeAccordingToImg();
        }).delay(200);
//        this.setSizeAccordingToImg();
    },

    getJsonData: function() {
        return {
            width: this.imageWidthField.getValue(),
            height: this.imageHeightField.getValue(),
            resourceFileTemporaryName: this.resourceFileTemporaryName.getValue(),
            resourceFileOriginalName: this.resourceFileOriginalName.getValue()
        };
    },

    save: function() {
        var instance = this;

        var jsonData = this.getJsonData();

        if (OPF.isEmpty(jsonData.resourceFileOriginalName)) {
            OPF.Msg.setAlert(false, 'Please upload an image.');
            return false;
        }

        var url;
        var method;
        if (this.idData.resourceId) {
            jsonData.resourceId = this.idData.resourceId;
            jsonData.version = this.idData.version;
            url = OPF.Cfg.restUrl('content/resource/image/version/' + this.idData.resourceId);
            method = 'PUT';
        } else {
            url = OPF.Cfg.restUrl('content/resource/image/version/new-by-lookup/' + this.idData.lookup);
            method = 'POST';
        }

        jsonData.culture = this.cultureCombo.getValue();

        Ext.Ajax.request({
            url: url,
            method: method,
            jsonData: {"data": jsonData},

            success: function(response, action) {
                var vo;
                try {
                    vo = Ext.decode(response.responseText);
                } catch (e) {
                    OPF.Msg.setAlert(false, 'Error saving resource.');
                    return;
                }

                OPF.Msg.setAlert(vo.success, vo.message);

                if (vo.success) {
                    instance.saveButton.disable();

                    if (instance.cultureCombo.getValue() == 'AMERICAN') { // TODO compare with GUI language value
                        var element = instance.editingEl.el.dom.firstChild;
                        var imageHtml;
                        if (element.nodeName == 'DIV') {
                            var data = vo.data[0];
                            var urlSuffix = 'content/resource/image/by-filename/' + data.resourceId + '/' + data.storedFilename + '?_dc=' + new Date().getTime();
                            var url = OPF.Cfg.restUrl(urlSuffix);
                            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(url, instance.imageWidth, instance.imageHeight);
                        } else {
                            var imageSrc = element.src;
                            imageSrc = imageSrc.replace(/_dc=.+/, '');
                            imageSrc += '_dc=' + new Date().getTime();
                            imageHtml = OPF.core.component.resource.ResourceControl.getImgHtml(imageSrc, instance.imageWidth, instance.imageHeight);
                        }
                        instance.editingEl.update(imageHtml);
                    }
                }
            },

            failure:function(response) {
                OPF.Msg.setAlert(false, 'Error occurred while saving image.');
            }
        });
    }

});
