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
package info.magnolia.ui.workbench;

import info.magnolia.event.EventBus;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.event.SelectionChangedEvent;

import java.util.Set;

import javax.inject.Inject;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * The browser features a status bar at the bottom with selected path and item count information.
 */
public class WorkbenchStatusBarPresenter {

    private final StatusBarView view;

    private ContentConnector contentConnector;

    private EventBus eventBus;

    private final Label selectionLabel = new Label();


    @Inject
    public WorkbenchStatusBarPresenter(StatusBarView view, ContentConnector contentConnector) {
        this.view = view;
        this.contentConnector = contentConnector;
    }

    private void bindHandlers() {
        eventBus.addHandler(SelectionChangedEvent.class, new SelectionChangedEvent.Handler() {

            @Override
            public void onSelectionChanged(SelectionChangedEvent event) {
                setSelectedItems(event.getItemIds());
            }
        });
    }

    public StatusBarView start(EventBus eventBus) {

        this.eventBus = eventBus;

        view.addComponent(selectionLabel, Alignment.TOP_LEFT);
        ((HorizontalLayout) view).setExpandRatio(selectionLabel, 1);

        bindHandlers();

        return view;
    }

    public void setSelectedItems(Set<Object> itemIds) {
        if (!itemIds.isEmpty()) {
            setSelectedItem(itemIds.iterator().next());
        } else {
            setSelectedItem(contentConnector.getDefaultItemId());
        }
    }

    public void setSelectedItem(Object itemId) {
        String newValue = contentConnector.getItemUrlFragment(itemId);
        String newDescription = newValue;
        selectionLabel.setValue(newValue);
        selectionLabel.setDescription(newDescription);
    }
}
