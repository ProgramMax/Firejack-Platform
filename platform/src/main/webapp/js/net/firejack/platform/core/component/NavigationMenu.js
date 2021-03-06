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

Ext.ns('OPF.core.component');

/**
 * The NavigationMenu class supplies the persistent navigation for the entire
 * Openflame Console application using toggle buttons to indicate the current page.
 * This navigation control appears on all pages of the site. 
 */
Ext.define('OPF.core.component.NavigationMenu', {
    extend: 'Ext.toolbar.Toolbar',
    cls: 'header',
    height: 39,

    alias : 'widget.navigation-menu',

    lookup: '',
    activeButtonLookup: null,
    navigationElements: null,

    /**
     * Initializes the layout of the nav bar and ensures that the appropriate links
     * are in place to support navigation across the application.
     */
    initComponent: function() {

        this.addNavigationButtons();

        var searchField = Ext.ComponentMgr.create({
            xtype: 'textfield',
            cls: 'search',
            emptyText: 'Search',
            enableKeyEvents: true,
            listeners: {
                keyup: function(field, event) {
                    if (event.keyCode == 13) {
                        document.location = OPF.Cfg.OPF_CONSOLE_URL + '/console/search?term=' + field.value;
                    }
                }
            }
        });

        if (OPF.Cfg.PAGE_TYPE == 'SEARCH' && isNotBlank(SEARCH_PHRASE)) {
            searchField.setValue(SEARCH_PHRASE);
        }

        var logoutButton = Ext.ComponentMgr.create({
            xtype:'button',
            ui: 'logout',
            text: '',
            tooltip: 'Logout',
            width: 12,
            height: 15,
            iconCls: 'logout-ico',
            handler: function() {
                document.location = OPF.Cfg.fullUrl('console/logout');
            }
        });

        this.items = [
            {
                xtype: 'component',
                html:
                    '<a class="logo" href="http://www.firejack.net" title="Firejack Platform">' +
                        '<span class="logo-img_w32"></span>' +
                    '</a>'
            },
            '->',
            searchField,
            {
                xtype: 'component',
                cls: 'user-name',
                width: 100,
                html: OPF.Cfg.USER_INFO.username
            },
            logoutButton
        ];


        this.callParent(arguments);
    },

    addNavigationButtons: function() {
        var me = this;

        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('site/navigation/tree-by-lookup/' + this.lookup),
            method: 'GET',
            jsonData: '[]',

            success: function(response, action) {
                var activeButton = null;
                var navigationElementButtons = [];
                me.navigationElements = Ext.decode(response.responseText).data;
                Ext.each(me.navigationElements, function(navigationElement, index) {
                    OPF.NavigationMenuRegister.add(navigationElement);
                    var navigationElements  = navigationElement.navigationElements;
                    var menuButtonXType = navigationElements.length > 0 ? 'splitbutton' : 'button';
                    var name = OPF.isNotEmpty(navigationElement.textResourceVersion) ? navigationElement.textResourceVersion.text : navigationElement.name;

                    var navigationElementButton = Ext.ComponentMgr.create({
                        xtype: menuButtonXType,
                        ui: 'main-menu',
                        margin: '0 0 0 0',
                        text: name,
                        width: me.buttonWidth,
                        height: me.buttonHeight,
                        scale: me.buttonScale,
                        iconAlign: me.buttonIconAlign,
                        iconCls: me.buttonIconCls,
                        enableToggle: true,
                        toggleGroup: 'cs-nav',
                        handler: function() {
                            document.location = me.generateUrl(navigationElement);
                        }
                    });
                    me.navigationElements.push(navigationElement);

                    me.addSubNavigationButtons(navigationElements, navigationElementButton);

//                    var spacerComponent = Ext.ComponentMgr.create({
//                        xtype: 'tbspacer',
//                        width: 0
//                    });
//                    me.insert(index * 2, navigationElementButton);
//                    me.insert(index * 2 + 1, spacerComponent);

                    navigationElementButtons.push(navigationElementButton);

                    if (navigationElement.lookup == me.activeButtonLookup) {
                        activeButton = navigationElementButton;
                        activeButton.pressed=true;
                    }
                });

                navigationElementButtons.push(
                    {
                        xtype: 'button',
                        text: 'Knowledge Center',
                        ui: 'main-menu',
                        scale: me.buttonScale,
                        href: 'http://documents.firejack.net',
                        target: '_blank'
                    }
                );

                me.add(1, navigationElementButtons);

//                me.doLayout();
//                if (OPF.isNotEmpty(activeButton)) {
//                    activeButton.toggle(true);
//                }
            },

            failure: function(response) {
                var errorJsonData = Ext.decode(response.responseText);
                OPF.Msg.setAlert(false, errorJsonData.message);
            }
        });
    },

    addSubNavigationButtons: function(navigationElements, navigationElementButton) {
        var instance = this;

        var menuButtons = [];
        Ext.each(navigationElements, function(navigationElement, index) {
            OPF.NavigationMenuRegister.add(navigationElement);
            var subNavigationElementButton = Ext.ComponentMgr.create({
                xtype: 'menuitem',
                text: navigationElement.name,
                handler: function() {
                    document.location = instance.generateUrl(navigationElement);
                }
            });
            menuButtons.push(subNavigationElementButton);

            instance.addSubNavigationButtons(navigationElement.navigationElements, subNavigationElementButton);
        });
        if (menuButtons.length > 0) {
            navigationElementButton.menu = Ext.menu.MenuMgr.get(menuButtons);
        }
    },

    generateUrl: function(nav) {
        var url = OPF.Cfg.getBaseUrl();
        if (OPF.isNotBlank(nav.serverName) &&
            OPF.isNotEmpty(nav.port) &&
            OPF.isNotBlank(nav.parentPath)) {
            url = 'http://' + nav.serverName + ':' + nav.port + nav.parentPath;
        }
        url += nav.urlPath;
        return url;
    }



});