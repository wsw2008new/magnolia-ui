/**
 * This file Copyright (c) 2010-2012 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.ui.admincentral.field.builder;

import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;

import info.magnolia.ui.model.field.definition.FieldDefinition;
import info.magnolia.ui.model.field.definition.RichTextFieldDefinition;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;

/**
 * Creates and initializes an edit field based on a field definition.
 */
public class RichTextFieldBuilder extends AbstractFieldBuilder<RichTextFieldDefinition> {

    public RichTextFieldBuilder(RichTextFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    @Override
    protected Field buildField() {
        RichTextFieldDefinition editDefinition = definition;
        CKEditorConfig config = new CKEditorConfig();
//        config.addToExtraPlugins("demo");
        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append("{ name: 'basicstyles', items: ['Bold','Italic','Underline','SpecialChar','-','RemoveFormat'] },");
        strbuilder.append("{ name: 'paragraph', items: ['NumberedList','BulletedList','-','Outdent','Indent'] },");
        strbuilder.append("{ name: 'insert', items: ['Image','Link','Unlink'] },");
        strbuilder.append("{ name: 'clipboard', items: ['Cut','Copy','Paste','PasteText','PasteFromWord'] },");
        strbuilder.append("{ name: 'table', items: ['Table'] },");
        strbuilder.append("{ name: 'special', items: ['Undo','Redo'] }");
        config.addCustomToolbarLine(strbuilder.toString());
        
        CKEditorTextField richtexteditor = new CKEditorTextField(config);
        return richtexteditor;

    }

    @Override
    protected Class<?> getDefaultFieldType(FieldDefinition fieldDefinition) {
        return String.class;
    }
}

