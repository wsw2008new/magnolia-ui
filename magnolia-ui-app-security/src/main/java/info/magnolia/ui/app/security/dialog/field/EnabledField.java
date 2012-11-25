/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.ui.app.security.dialog.field;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

import info.magnolia.ui.admincentral.field.builder.CheckBoxFieldBuilder;
import info.magnolia.ui.model.field.definition.CheckboxFieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;

/**
 * Field to handle chackbox bound to a String property.
 */
public class EnabledField extends CheckBoxFieldBuilder {

    public EnabledField(CheckboxFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    @Override
    public com.vaadin.data.Property getOrCreateProperty() {
        Property _old = item.getItemProperty("enabled");
        String stringValue = "true";
        if (_old!=null) {
            stringValue = _old.toString();
        }
        Boolean _new = Boolean.parseBoolean(stringValue);
        DefaultProperty prop = new DefaultProperty("enabled",_new);
        item.removeItemProperty("enabled");
        item.addItemProperty("enabled", prop);
        return prop;
    }


}
