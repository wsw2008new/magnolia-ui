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

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.iterator.FilteringPropertyIterator;
import info.magnolia.jcr.predicate.JCRMgnlPropertyHidingPredicate;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.form.field.builder.TwinColSelectFieldBuilder;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.TwinColSelect;

/**
 * GUI builder for the Role Management field.
 */
public class RoleManagementField extends TwinColSelectFieldBuilder<RoleManagementFieldDefinition> {

    /**
     * Internal bean to represent basic role data.
     */
    private static class Role {
        public String name;
        public String uuid;

        public Role(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(RoleManagementField.class);

    public RoleManagementField(RoleManagementFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
        definition.setOptions(getSelectFieldOptionDefinition());
    }

    @Override
    protected AbstractSelect buildField() {
        super.buildField();
        select.setMultiSelect(true);
        select.setNullSelectionAllowed(true);
        return select;
    }

    @Override
    protected AbstractSelect createSelectionField() {
        return new TwinColSelect();
    }

    /**
     * Returns the available roles with those already assigned marked selected, according to the current node.
     */
    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> options = new ArrayList<SelectFieldOptionDefinition>();
        List<Role> allRoles = getAllRoles(); // name,uuid
        Set<String> assignedGroups = getAssignedRoles();
        for (Role role : allRoles) {
            SelectFieldOptionDefinition option = new SelectFieldOptionDefinition();
            option.setValue(role.uuid);
            option.setLabel(role.name);
            if (assignedGroups.contains(role.uuid)) {
                option.setSelected(true);
            }
            options.add(option);
        }
        return options;
    }

    private List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<Role>();
        try {
            NodeIterator ni = QueryUtil.search(RepositoryConstants.USER_ROLES, "SELECT * FROM [" + NodeTypes.Role.NAME + "] ORDER BY name()");
            while (ni.hasNext()) {
                Node n = ni.nextNode();
                String name = n.getName();
                String uuid = n.getIdentifier();
                roles.add(new Role(name, uuid));
            }
        } catch (RepositoryException e) {
            log.error("Cannot read roles from the [" + RepositoryConstants.USER_ROLES + "] workspace.", e);
        }
        return roles;
    }

    private Set<String> getAssignedRoles() {
        Set<String> roles = new HashSet<String>();
        Node mainNode = getRelatedNode(item);
        try {
            if (mainNode.hasNode("roles")) {
                Node rolesNode = mainNode.getNode("roles");
                if (rolesNode == null) {
                    // shouldn't happen, just in case
                    return roles;
                }
                for (PropertyIterator iter = new FilteringPropertyIterator(rolesNode.getProperties(), new JCRMgnlPropertyHidingPredicate());  iter.hasNext();) {
                    Property p = iter.nextProperty();
                    roles.add(p.getString());
                }
            }
        } catch (RepositoryException re) {
            log.error("Cannot read assigned roles of the node [" + mainNode + "].", re);
        }
        return roles;
    }

    @Override
    public com.vaadin.data.Property<Set> getOrCreateProperty() {
        DefaultProperty<Set> prop = new DefaultProperty<Set>("roles", Set.class, getAssignedRoles());
        item.addItemProperty("roles", prop);
        return prop;
    }

}
