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
package info.magnolia.ui.actionbar.definition;

import info.magnolia.cms.security.operations.AccessDefinition;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.availability.AvailabilityRule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple implementation for {@link SectionAvailabilityDefinition}.
 */
public class ConfiguredSectionAvailabilityDefinition implements SectionAvailabilityDefinition {

    private boolean root = false;
    private boolean properties = false;
    private boolean nodes = true;
    private AccessDefinition access = Components.newInstance(AccessDefinition.class, new Object[] {});
    private Collection<String> nodeTypes = new ArrayList<String>();
    private AvailabilityRule rule;

    @Override
    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    @Override
    public boolean isProperties() {
        return properties;
    }

    public void setProperties(boolean properties) {
        this.properties = properties;
    }

    @Override
    public boolean isNodes() {
        return this.nodes;
    }

    public void setNodes(boolean nodes) {
        this.nodes = nodes;
    }

    @Override
    public Collection<String> getNodeTypes() {
        return nodeTypes;
    }

    public void setNodeTypes(Collection<String> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    public void addNodeType(String nodeType) {
        nodeTypes.add(nodeType);
    }

    public void setAccess(AccessDefinition access) {
        this.access = access;
    }

    @Override
    public AccessDefinition getAccess() {
        return this.access;
    }

    public void setRule(AvailabilityRule rule) {
        this.rule = rule;
    }

    @Override
    public AvailabilityRule getRule() {
        return this.rule;
    }
}
