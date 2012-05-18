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
package info.magnolia.ui.model.workbench.registry;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.ui.model.workbench.definition.WorkbenchDefinition;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Provides the tree definition for a tree configured in the repository.
 */
public class ConfiguredWorkbenchDefinitionProvider implements WorkbenchDefinitionProvider {

    private String id;
    private WorkbenchDefinition definition;

    public ConfiguredWorkbenchDefinitionProvider(String id, Node configNode) throws RepositoryException, Content2BeanException{
        this.id = id;
        Content content = ContentUtil.asContent(configNode);
        this.definition = (WorkbenchDefinition) Content2BeanUtil.toBean(content, true, WorkbenchDefinition.class);
    }

    @Override
    public WorkbenchDefinition getDefinition() {
        return definition;
    }

    @Override
    public String getId() {
        return id;
    }
}
