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
package info.magnolia.ui.admincentral.list.view;

import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.ui.admincentral.column.Column;
import info.magnolia.ui.admincentral.column.EditHandler;
import info.magnolia.ui.admincentral.container.ContainerItemId;
import info.magnolia.ui.admincentral.container.JcrContainer;
import info.magnolia.ui.admincentral.jcr.view.JcrView;
import info.magnolia.ui.admincentral.list.container.FlatJcrContainer;
import info.magnolia.ui.admincentral.tree.model.TreeModel;
import info.magnolia.ui.model.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.vaadin.integration.view.IsVaadinComponent;

import javax.jcr.Item;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * Vaadin UI component that displays a list.
 *
 */
public class ListViewImpl implements ListView, IsVaadinComponent {

    private JcrView.Presenter presenter;

    private final Table table;

    private final JcrContainer container;

    private final TreeModel treeModel;

    private static final Logger log = LoggerFactory.getLogger(ListViewImpl.class);

    public ListViewImpl(WorkbenchDefinition workbenchDefinition, TreeModel treeModel){
        this.treeModel = treeModel;
        table = new Table();
        table.setSizeUndefined();
        table.setHeight(100, UNITS_PERCENTAGE);
        table.addStyleName("striped");

        // next two lines are required to make the browser (Table) react on selection change via mouse
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        // table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.setMultiSelect(false);

        //Important do not set page length and cache ratio on the Table, rather set them by using JcrContainer corresponding methods. Setting
        //those value explicitly on the Table will cause the same jcr query to be repeated twice thus degrading performance greatly.
        //TODO investigate cause for this behavior.
        //table.setPageLength(200); recipe for slowness, don't do this!

        table.addListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                presenterOnItemSelection((ContainerItemId) event.getItemId());
            }
        });
        table.addListener(new Table.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                presenterOnItemSelection((ContainerItemId) event.getProperty().getValue());
            }
        });
        new EditHandler(table);

        table.setEditable(false);
        table.setSelectable(true);
        table.setColumnCollapsingAllowed(true);

        // TODO: check Ticket http://dev.vaadin.com/ticket/5493
        table.setColumnReorderingAllowed(true);

        container = new FlatJcrContainer(treeModel,workbenchDefinition);

        for (Column<?> treeColumn : treeModel.getColumns().values()) {
            String columnName = treeColumn.getDefinition().getName();
            table.setColumnExpandRatio(columnName, treeColumn.getWidth() <= 0 ? 1 : treeColumn.getWidth());
            container.addContainerProperty(columnName, Component.class, "");
            table.setColumnHeader(columnName, treeColumn.getLabel());
        }

        table.setContainerDataSource(container);
    }

    @Override
    public void select(String path) {
        ContainerItemId itemId = container.getItemByPath(path);
        table.select(itemId);
    }

    @Override
    public void refresh() {
        container.fireItemSetChange();
    }

    @Override
    public Component asVaadinComponent() {
        return table;
    }

    @Override
    public String getPathInTree(Item jcrItem) {
        try {
            return treeModel.getPathInTree(jcrItem);
        } catch (RepositoryException e) {
            throw new RuntimeRepositoryException(e);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public JcrContainer getContainer() {
        return container;
    }

    private void presenterOnItemSelection(ContainerItemId id) {
        if (presenter != null) {
            Item item = null;
            try {
                item = container.getJcrItem(id);
            } catch (RepositoryException e) {
                throw new RuntimeRepositoryException(e);
            }
            presenter.onItemSelection(item);
        }
    }
}
