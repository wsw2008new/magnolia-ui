/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.ui.form;

import info.magnolia.ui.form.definition.TabDefinition;
import info.magnolia.ui.vaadin.form.FormSection;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

/**
 * Represents a tab in a dialog.
 *
 * @see info.magnolia.ui.form.field.factory.FieldFactory
 */
public class FormTab extends AbstractFormItem {

    private TabDefinition definition;

    private final FormSection container = new FormSection();

    public FormTab(TabDefinition definition) {
        this.definition = definition;
    }

    public void addField(Component component) {
        this.container.addComponent(component);
    }

    public FormSection getContainer() {
        return container;
    }

    @Override
    protected String getI18nBasename() {
        return definition.getI18nBasename();
    }

    public void setComponentHelpDescription(Field field, String helpDescription) {
        container.setComponentHelpDescription(field, getMessage(helpDescription));
    }

}
