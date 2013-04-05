/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.ui.admincentral.tree.action;

import info.magnolia.cms.core.Path;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.event.EventBus;
import info.magnolia.ui.framework.event.AdmincentralEventBus;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Action for adding a new node.
 *
 * @see AddNodeActionDefinition
 */
public class AddNodeAction extends RepositoryOperationAction<AddNodeActionDefinition> {

    private static final String DEFAULT_NODE_NAME = "untitled";

    public AddNodeAction(AddNodeActionDefinition definition, Item item, @Named(AdmincentralEventBus.NAME) EventBus eventBus) {
        super(definition, item, eventBus);
    }

    @Override
    protected void onExecute(Item item) throws RepositoryException {
        Node node = (Node) item;
        String name = Path.getUniqueLabel(item.getSession(), item.getPath(), DEFAULT_NODE_NAME);
        Node newNode = node.addNode(name, getDefinition().getNodeType());
        NodeTypes.Created.set(newNode);
    }
}
