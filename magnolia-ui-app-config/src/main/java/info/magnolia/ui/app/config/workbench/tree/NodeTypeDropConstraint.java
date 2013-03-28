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
package info.magnolia.ui.app.config.workbench.tree;

import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.workbench.tree.drop.DropConstraint;

import com.vaadin.data.Item;

/**
 * Jcr configuration implementation of {@link DropConstraint} used by the
 * Config-App in order to handle the Drag & Drop events. <br>
 * <b>Constraints</b><br>
 * Properties are not allowed to Move (allowedToMove). A Node can not be set as
 * child as a Property (allowedAsChild).
 */
public class NodeTypeDropConstraint implements DropConstraint {

    @Override
    public boolean allowedAsChild(Item sourceItem, Item targetItem) {
        return ((JcrItemAdapter) targetItem).isNode();
    }

    @Override
    public boolean allowedBefore(Item sourceItem, Item targetItem) {
        return true;
    }

    @Override
    public boolean allowedAfter(Item sourceItem, Item targetItem) {
        return true;
    }

    @Override
    public boolean allowedToMove(Item sourceItem) {
        JcrItemAdapter jcrSourceItem = (JcrItemAdapter) sourceItem;
        return jcrSourceItem.isNode();
    }

}
