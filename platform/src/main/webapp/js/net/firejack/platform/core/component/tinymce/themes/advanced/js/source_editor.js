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

tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

function saveContent() {
	tinyMCEPopup.editor.setContent(document.getElementById('htmlSource').value, {source_view : true});
	tinyMCEPopup.close();
}

function onLoadInit() {
	tinyMCEPopup.resizeToInnerSize();

	// Remove Gecko spellchecking
	if (tinymce.isGecko)
		document.body.spellcheck = tinyMCEPopup.editor.getParam("gecko_spellcheck");

	document.getElementById('htmlSource').value = tinyMCEPopup.editor.getContent({source_view : true});

	if (tinyMCEPopup.editor.getParam("theme_advanced_source_editor_wrap", true)) {
		turnWrapOn();
		document.getElementById('wraped').checked = true;
	}

	resizeInputs();
}

function setWrap(val) {
	var v, n, s = document.getElementById('htmlSource');

	s.wrap = val;

	if (!tinymce.isIE) {
		v = s.value;
		n = s.cloneNode(false);
		n.setAttribute("wrap", val);
		s.parentNode.replaceChild(n, s);
		n.value = v;
	}
}

function setWhiteSpaceCss(value) {
	var el = document.getElementById('htmlSource');
	tinymce.DOM.setStyle(el, 'white-space', value);
}

function turnWrapOff() {
	if (tinymce.isWebKit) {
		setWhiteSpaceCss('pre');
	} else {
		setWrap('off');
	}
}

function turnWrapOn() {
	if (tinymce.isWebKit) {
		setWhiteSpaceCss('pre-wrap');
	} else {
		setWrap('soft');
	}
}

function toggleWordWrap(elm) {
	if (elm.checked) {
		turnWrapOn();
	} else {
		turnWrapOff();
	}
}

function resizeInputs() {
	var vp = tinyMCEPopup.dom.getViewPort(window), el;

	el = document.getElementById('htmlSource');

	if (el) {
		el.style.width = (vp.w - 20) + 'px';
		el.style.height = (vp.h - 65) + 'px';
	}
}
