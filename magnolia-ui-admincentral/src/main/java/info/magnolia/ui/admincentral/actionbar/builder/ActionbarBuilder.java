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
package info.magnolia.ui.admincentral.actionbar.builder;

import info.magnolia.ui.admincentral.actionbar.ActionbarPresenter;
import info.magnolia.ui.model.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.model.actionbar.definition.ActionbarGroupDefinition;
import info.magnolia.ui.model.actionbar.definition.ActionbarSectionDefinition;
import info.magnolia.ui.vaadin.actionbar.Actionbar;
import info.magnolia.ui.vaadin.actionbar.ActionbarView;
import info.magnolia.ui.vaadin.gwt.client.actionbar.shared.ActionbarItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ThemeResource;

/**
 * Basic builder for an action bar widget based on an action bar definition.
 */
public class ActionbarBuilder {

    private static final Logger log = LoggerFactory.getLogger(ActionbarBuilder.class);

    public static ActionbarView build(ActionbarDefinition definition, ActionbarPresenter.Listener listener) {
        Actionbar actionbar = new Actionbar();
        if (definition == null) {
            log.warn("No actionbar definition found. This will result in an empty action bar. Is that intended?");
            return actionbar;
        } else {

            for (ActionbarSectionDefinition section : definition.getSections()) {
                actionbar.addSection(section.getName(), section.getLabel());
                List<String> actionNames = new ArrayList<String>();
                for (ActionbarGroupDefinition group : section.getGroups()) {
                    // standalone groups make no sense
                    log.info("Group actions: " + group.getActions());
                    for (String action : group.getActions()) {

                        if (actionNames.contains(action)) {
                            log.warn("Action was not added: an action with name " + action + "': was already added to the section" + section.getName() + ".");
                            continue;
                        }
                        
                        actionNames.add(action);
                        addItemFromDefinition(listener, actionbar, group.getName(), section.getName(), action);
                    }
                }
            }
        }
        return actionbar;
    }

    public static void addItemFromDefinition(ActionbarPresenter.Listener listener, Actionbar actionBar, String groupName, String sectionName, String actionName) {
        ActionbarItem entry;
        String icon = listener.getIcon(actionName);
        String label = listener.getLabel(actionName);
        if (StringUtils.isBlank(label)) {
            return;
        }
        if (StringUtils.isNotBlank(icon)) {
            if (icon.startsWith("icon-")) {
                entry = new ActionbarItem(actionName, label, icon, groupName);
            } else {
                try {
                    actionBar.registerActionIconResource(actionName, new ThemeResource(icon));
                } catch (NullPointerException e) {
                    log.warn("Icon resource not found for Actionbar item " + actionName + "'.");
                } finally {
                    entry = new ActionbarItem(actionName, label, null, groupName);
                }
            }
        } else {
            entry = new ActionbarItem(actionName, label, null, groupName);
        }
        actionBar.addAction(entry, sectionName);
    }
}
