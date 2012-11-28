/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.ui.admincentral.workbench;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import info.magnolia.ui.admincentral.content.item.ItemView;
import info.magnolia.ui.admincentral.content.view.ContentView;
import info.magnolia.ui.framework.view.View;
import info.magnolia.ui.vaadin.actionbar.ActionbarView;

import java.util.EnumMap;
import java.util.Map;

/**
 * Implementation of {@link ItemWorkbenchView}.
 * Holds the {@link ActionbarView} and {@link ItemView}
 * Currently lacking some functionality planned. See MGNLUI-154.
 */
public class ItemWorkbenchViewImpl extends CustomComponent implements ItemWorkbenchView {

    private final HorizontalLayout root = new HorizontalLayout();
    private final CssLayout itemViewContainer = new CssLayout();
    private Map<ContentView.ViewType, ContentView> contentViews = new EnumMap<ContentView.ViewType, ContentView>(ContentView.ViewType.class);

    private ActionbarView actionbar;

    private ItemView.ViewType currentViewType = ItemView.ViewType.VIEW;

    private ItemWorkbenchView.Listener contentWorkbenchViewListener;


    public ItemWorkbenchViewImpl() {
        super();
        setCompositionRoot(root);
        setSizeFull();

        root.setSizeFull();
        root.setStyleName("workbench");
        root.addComponent(itemViewContainer);
        root.setExpandRatio(itemViewContainer, 1);
        root.setSpacing(true);
        root.setMargin(false);
    }

    @Override
    public Component asVaadinComponent() {
        return root;
    }

    @Override
    public void setListener(ItemWorkbenchView.Listener listener) {
        this.contentWorkbenchViewListener = listener;
    }

    @Override
    public void setViewType(final ItemView.ViewType type) {

        itemViewContainer.removeComponent(contentViews.get(currentViewType).asVaadinComponent());
        final Component c = contentViews.get(type).asVaadinComponent();

        c.setSizeFull();
        itemViewContainer.addComponent(c);

        this.currentViewType = type;
        refresh();
        this.contentWorkbenchViewListener.onViewTypeChanged(currentViewType);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void addItemView(ItemView.ViewType type, ItemView view) {

    }

    @Override
    public void setItemView(final View itemView) {
        itemView.asVaadinComponent().setWidth(Sizeable.SIZE_UNDEFINED, 0);
        itemViewContainer.addComponent(itemView.asVaadinComponent());
    }

    @Override
    public void setActionbarView(final ActionbarView actionbar) {
        actionbar.asVaadinComponent().setWidth(Sizeable.SIZE_UNDEFINED, 0);
        if (this.actionbar == null) {
            root.addComponent(actionbar.asVaadinComponent());
        } else {
            root.replaceComponent(this.actionbar.asVaadinComponent(), actionbar.asVaadinComponent());
        }
        this.actionbar = actionbar;
    }

    @Override
    public ContentView getSelectedView() {
        return null;
    }
}
