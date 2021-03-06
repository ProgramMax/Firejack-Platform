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

Ext.define("OPF.core.component.tinymce.WindowManager", {
//    extend: tinymce.WindowManager,

    constructor: function(config) {
        tinymce.WindowManager.call(this, config.editor);
    },

    alert: function(txt, cb, s) {
        Ext.MessageBox.alert("", txt, function() {
            if (!Ext.isEmpty(cb)) {
                cb.call(this);
            }
        }, s);
    },

    confirm: function(txt, cb, s) {
        Ext.MessageBox.confirm("", txt, function(btn) {
            if (!Ext.isEmpty(cb)) {
                cb.call(this, btn == "yes");
            }
        }, s);
    },

    open: function(s, p) {
        s = s || {};
        p = p || {};
        if (!s.type) {
            this.bookmark = this.editor.selection.getBookmark('simple');
        }
        s.width = parseInt(s.width || 320);
        s.height = parseInt(s.height || 240) + (tinymce.isIE ? 8 : 0);
        s.min_width = parseInt(s.min_width || 150);
        s.min_height = parseInt(s.min_height || 100);
        s.max_width = parseInt(s.max_width || 2000);
        s.max_height = parseInt(s.max_height || 2000);
        s.movable = true;
        s.resizable = true;
        p.mce_width = s.width;
        p.mce_height = s.height;
        p.mce_inline = true;
        this.features = s;
        this.params = p;
        var win = Ext.create("Ext.window.Window", {
            title: s.name,
            width: s.width,
            height: s.height,
            minWidth: s.min_width,
            minHeight: s.min_height,
            resizable: true,
            maximizable: s.maximizable,
            minimizable: s.minimizable,
            modal: true,
            stateful: false,
            constrain: true,
            //border: false,
            layout: "fit",
            items: [Ext.create("Ext.Component", {
                autoEl: {
                    tag: 'iframe',
                    src: s.url || s.file
                },
                style: 'border-width: 0px;'
            })]
        });

        p.mce_window_id = win.getId();
        win.show(null, function() {
            if (s.left && s.top)
                win.setPagePosition(s.left, s.top);
            var pos = win.getPosition();
            s.left = pos[0];
            s.top = pos[1];
            this.onOpen.dispatch(this, s, p);
        }, this);

        return win;
    },

    close: function(win) {
        // Probably not inline
        if (!win.tinyMCEPopup || !win.tinyMCEPopup.id) {
            tinymce.WindowManager.prototype.close.call(this, win);
            return;
        }

        var w = Ext.getCmp(win.tinyMCEPopup.id);

        if (w) {
            this.onClose.dispatch(this);
            w.close();
        }
    },

    setTitle: function(win, ti) {
        if (!win.tinyMCEPopup || !win.tinyMCEPopup.id) {
            tinymce.WindowManager.prototype.setTitle.call(this, win, ti);
            return;
        }

        var w = Ext.getCmp(win.tinyMCEPopup.id);

        if (w)
            w.setTitle(ti);
    },

    resizeBy: function(dw, dh, id) {
        var w = Ext.getCmp(id);

        if (w) {
            var size = w.getSize();
            w.setSize(size.width + dw, size.height + dh);
        }
    },

    focus: function(id) {
        var w = Ext.getCmp(id);
        if (w)
            w.setActive(true);
    }
});

Ext.define("OPF.core.component.tinymce.TinyMCEEditor", {
    extend: 'Ext.form.field.TextArea',
    alias: 'widget.tinymceeditor',

    config: {},

    constructor: function(editor, config) {
        var me = this;
        config = config || {};
        config.height = (config.height && config.height >= me.config.height) ? config.height : me.config.height;

        config.tinymceConfig = {};
        Ext.applyIf(config.tinymceConfig, me.getGlobalSettings());

        // Init values we do not want changed
        config.tinymceConfig.mode = 'none';

        me.addEvents({
            "editorcreated": true
        });

        me.callParent([config]);
    },

    getGlobalSettings: function() {
        return {
            accessibility_focus: false,
            language: "en",
            mode: "none",
            theme: "advanced",
            plugins: 'autolink,table',
            theme_advanced_buttons1: 'bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,forecolor,backcolor,|,styleselect,formatselect,fontselect,fontsizeselect,|,link, unlink,|,tablecontrols,removeformat,cleanup,code',
            theme_advanced_buttons2: '',
            theme_advanced_buttons3: '',
            theme_advanced_buttons4: '',

            content_css : [
                OPF.Cfg.fullUrl('css/clds4.css'),
                OPF.Cfg.fullUrl('css/documentation.css'),
                OPF.Cfg.fullUrl('css/simple-html-editor.css'),
                OPF.Cfg.fullUrl('css/tinymce.css')
            ],
            theme_advanced_toolbar_location: 'top',
            theme_advanced_toolbar_align: 'left',
            theme_advanced_statusbar_location: 'none',
            theme_advanced_resizing: false,
            theme_advanced_row_height: 30,

            style_formats : [
                    {title : 'Quote', inline : 'span', classes : 'quote'},
                    {title : 'Code', inline : 'span', classes : 'code'},
                    {title : 'Warning', inline : 'span', classes : 'warn'},
                    {title : 'Important', inline : 'span', classes : 'important'},
                    {title : 'Critical', inline : 'span', classes : 'critical'},
                    {title : 'Subdued', inline : 'span', classes : 'subdued'}
            ]
        }
    },

    afterRender: function() {
        var me = this;
        me.callParent(arguments);
        me.tinymceConfig.height = me.height;
        console.log(me.tinymceConfig.height);
        me.editor = new tinymce.Editor(me.inputEl.id, me.tinymceConfig);

        // Validate value onKeyPress
        var validateContentTask = Ext.Function.createBuffered(me.validate, 250, this);
        me.editor.onKeyPress.add(validateContentTask);

        me.editor.onPostRender.add(Ext.Function.bind(function(editor, controlManager) {
            editor.windowManager = Ext.create("OPF.core.component.tinymce.WindowManager", {
                editor: me.editor
            });
            me.tableEl = Ext.get(me.editor.id + "_tbl");
            me.iframeEl = Ext.get(me.editor.id + "_ifr");
        }, me));

        window.b = me.editor;
        me.on('resize', me.onResize, me);

        me.editor.render();
        tinyMCE.add(me.editor);
    },

    setSize : function(width, height) {
        console.log([width, height]);
        if (this.editor.theme) {
            this.editor.theme.resizeTo(width, height - 30);
        }
        this.callParent(arguments);
    },

    isDirty: function() {
        var me = this;
        if (me.disabled || !me.rendered) {
            return false;
        }
        return me.editor && me.editor.initialized && me.editor.isDirty();
    },

    getValue: function() {
        return this.editor.getContent();
    },

    setValue: function(value) {
        var me = this;
        if (OPF.isNotEmpty(value)) {
            me.value = value;
            if (me.rendered) {
                me.withEd(function() {
                    me.editor.undoManager.clear();
                    me.editor.setContent(value === null || value === undefined ? '' : value);
                    me.editor.startContent = me.editor.getContent({
                        format: 'raw'
                    });
                    me.validate();
                });
            } else {
                new Ext.util.DelayedTask(function(value) {
                    this.setValue(value);
                }, this).delay(100, null, this, [value]);
            }
        }
    },

    getSubmitData: function() {
        var ret = {};
        ret[this.getName()] = this.getValue();
        return ret;
    },

    insertValueAtCursor: function(value) {
        var me = this;

        if (me.editor && me.editor.initialized) {
            me.editor.execCommand('mceInsertContent', false, value);
        }
    },

    onDestroy: function() {
        var me = this;

        me.editor.remove();
        me.editor.destroy();
        me.callParent(arguments);
    },

    onResize: function(component, adjWidth, adjHeight) {
        var width, bodyWidth = component.bodyEl.getWidth();

        if (component.iframeEl) {
            width = bodyWidth - component.iframeEl.getBorderWidth('lr') - 2;
            component.iframeEl.setWidth(width);
        }

        if (component.tableEl) {
            width = bodyWidth - component.tableEl.getBorderWidth('lr') - 2;
            component.tableEl.setWidth(width);
        }
    },

    getEditor: function() {
        return this.editor;
    },

    getRawValue: function() {
        var me = this;

        return (!me.editor || !me.editor.initialized) ? Ext.valueFrom(me.value, '') : me.editor.getContent();
    },

    disable: function() {
        this.withEd(function() {
            var bodyEl = this.editor.getBody();
            bodyEl = Ext.get(bodyEl);
            if (bodyEl.hasCls('mceContentBody')) {
                bodyEl.removeCls('mceContentBody');
                bodyEl.addCls('mceNonEditable');
            }
        });
    },

    enable: function() {
        this.withEd(function() {
            var bodyEl = this.editor.getBody();
            bodyEl = Ext.get(bodyEl);
            if (bodyEl.hasCls('mceNonEditable')) {
                bodyEl.removeCls('mceNonEditable');
                bodyEl.addCls('mceContentBody');
            }
        });
    },

    withEd: function(func) {
        // If editor is not created yet, reschedule this call.
        if (!this.editor)
            this.on("editorcreated", function() {
                this.withEd(func);
            }, this);
        // Else if editor is created and initialized
        else if (this.editor.initialized)
            func.call(this);
        // Else if editor is created but not initialized yet.
        else
            this.editor.onInit.add(Ext.Function.bind(function() {
                Ext.Function.defer(func, 10, this);
            }, this));
    },

    validateValue: function(value) {
        var me = this;

        if (Ext.isFunction(me.validator)) {
            var msg = me.validator(value);
            if (msg !== true) {
                me.markInvalid(msg);
                return false;
            }
        }

        if (value.length < 1 || value === me.emptyText) { // if it's blank
            if (me.allowBlank) {
                me.clearInvalid();
                return true;
            }
            else {
                me.markInvalid(me.blankText);
                return false;
            }
        }

        if (value.length < me.minLength) {
            me.markInvalid(Ext.String.format(me.minLengthText, me.minLength));
            return false;
        }
        else
            me.clearInvalid();

        if (value.length > me.maxLength) {
            me.markInvalid(Ext.String.format(me.maxLengthText, me.maxLength));
            return false;
        }
        else
            me.clearInvalid();

        if (me.vtype) {
            var vt = Ext.form.field.VTypes;
            if (!vt[me.vtype](value, me)) {
                me.markInvalid(me.vtypeText || vt[me.vtype + 'Text']);
                return false;
            }
        }

        if (me.regex && !me.regex.test(value)) {
            me.markInvalid(me.regexText);
            return false;
        }
        return true;
    }
});