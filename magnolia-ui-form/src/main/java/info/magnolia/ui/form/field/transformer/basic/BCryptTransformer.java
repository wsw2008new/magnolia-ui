/**
 * This file Copyright (c) 2014-2015 Magnolia International
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
package info.magnolia.ui.form.field.transformer.basic;

import info.magnolia.cms.security.SecurityUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * {@link BasicTransformer} implementation that BCrypted (encode) the String property.
 */
public class BCryptTransformer extends BasicTransformer<String> {

    /**
     * @deprecated since 5.4.2 - use {@link #BCryptTransformer(Item, ConfiguredFieldDefinition, Class, I18NAuthoringSupport)} instead.
     */
    @Deprecated
    public BCryptTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<String> type) {
        super(relatedFormItem, definition, type, Components.getComponent(I18NAuthoringSupport.class));
    }

    public BCryptTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<String> type, I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }

    @Override
    public void writeToItem(String clearPassword) {
        Property<String> p = getOrCreateProperty(type);
        p.setValue(SecurityUtil.getBCrypt(clearPassword));
    }

}
