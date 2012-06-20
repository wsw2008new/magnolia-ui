/**
 * This file Copyright (c) 2011 Magnolia International
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
package info.magnolia.ui.admincentral.tree.view;

import info.magnolia.ui.admincentral.container.JcrContainer;
import info.magnolia.ui.admincentral.jcr.view.JcrView;
import info.magnolia.ui.admincentral.tree.model.TreeModel;
import info.magnolia.ui.model.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.vaadin.integration.view.IsVaadinComponent;



import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TreeTable;

/**
 * Vaadin UI component that displays a tree.
 *
 */
public class TreeViewImpl implements TreeView, IsVaadinComponent {

    private MagnoliaTreeTable jcrBrowser;

    private JcrView.Presenter presenter;

    public TreeViewImpl(WorkbenchDefinition workbenchDefinition, TreeModel treeModel) {

        jcrBrowser = new MagnoliaTreeTable(workbenchDefinition, treeModel);
        // next two lines are required to make the browser (TreeTable) react on selection change via mouse
        jcrBrowser.setImmediate(true);
        jcrBrowser.setNullSelectionAllowed(false);
        jcrBrowser.setSizeFull();
        jcrBrowser.addListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {

                // TODO JcrBrowser should have a click event of its own that sends a JCR item instead of a
                // ContainerItemId
                presenterOnItemSelection((String) event.getItemId());
            }
        });

        jcrBrowser.addListener(new TreeTable.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                presenterOnItemSelection((String) event.getProperty().getValue());
            }
        });
    }

    private void presenterOnItemSelection(String id) {
        if (presenter != null) {
            presenter.onItemSelection(jcrBrowser.getItem(id));
        }
    }

    /**
     *
     * @param path
     *            relative to the tree root, must start with /
     */
    @Override
    public void select(String path) {
        jcrBrowser.select(path);
    }

    @Override
    public void refresh() {
        jcrBrowser.refresh();
    }


    @Override
    public Component asVaadinComponent() {
        return jcrBrowser;
    }

    @Override
    public void setPresenter(JcrView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public JcrContainer getContainer() {
        throw new UnsupportedOperationException(getClass().getName() + " does not support this operation");
    }

    @Override
    public void refreshItem(final Item item) {
        jcrBrowser.updateItem(item);
    }
}
