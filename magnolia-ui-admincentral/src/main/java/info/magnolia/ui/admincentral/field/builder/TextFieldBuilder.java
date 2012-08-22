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

import com.vaadin.data.Item;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.apache.commons.lang.StringUtils;

import info.magnolia.ui.model.field.definition.TextFieldDefinition;
import info.magnolia.ui.model.field.definition.FieldDefinition;

/**
 * Creates and initializes an edit field based on a field definition.
 */
public class TextFieldBuilder extends AbstractFieldBuilder<TextFieldDefinition> {

    public TextFieldBuilder(TextFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    @Override
    protected Field buildField() {
        TextFieldDefinition editDefinition = definition;

        if (editDefinition.getRows() > 1) {
            return createMultiRowEditField(editDefinition);
        }
        return createSingleRowEditField(editDefinition);

    }

    private Field createSingleRowEditField(TextFieldDefinition definition) {
        TextField textField = new TextField();
        textField.setMaxLength(definition.getMaxLength());
        if (StringUtils.isNotEmpty(definition.getWidth())) {
            textField.setWidth(definition.getWidth());
        }
        return textField;
    }

    private Field createMultiRowEditField(TextFieldDefinition definition) {
        TextArea textArea = new TextArea();
        textArea.setRows(definition.getRows());
        if (StringUtils.isNotEmpty(definition.getWidth())) {
            textArea.setWidth(definition.getWidth());
        }
        return textArea;
    }

    @Override
    protected Class<?> getDefaultFieldType(FieldDefinition fieldDefinition) {
        return String.class;
    }
}

