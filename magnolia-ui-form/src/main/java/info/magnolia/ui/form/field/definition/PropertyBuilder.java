/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.ui.form.field.definition;

import info.magnolia.ui.form.field.property.HasPropertyHandler;
import info.magnolia.ui.form.field.property.PropertyHandler;

/**
 * Definition used to configure a the way a property is initialize and assign to a field. <br>
 * propertyType : Define the Type of the property used by the field ({@link info.magnolia.ui.form.field.property.basic.BasicProperty}, {@link info.magnolia.ui.form.field.property.multi.MultiProperty},... }).<br>
 * propertyHandler : Handler used to retrieve and put the content of the property to the related Field item.
 */
public class PropertyBuilder {

    private Class<? extends PropertyHandler<?>> propertyHandler;
    private Class<? extends HasPropertyHandler<?>> propertyType;

    public Class<? extends PropertyHandler<?>> getPropertyHandler() {
        return propertyHandler;
    }

    public Class<? extends HasPropertyHandler<?>> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<? extends HasPropertyHandler<?>> propertyType) {
        this.propertyType = propertyType;
    }

    public void setPropertyHandler(Class<? extends PropertyHandler<?>> propertyHandler) {
        this.propertyHandler = propertyHandler;
    }
}
