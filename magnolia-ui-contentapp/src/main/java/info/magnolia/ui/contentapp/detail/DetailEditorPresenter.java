/**
 * This file Copyright (c) 2012-2013 Magnolia International
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
package info.magnolia.ui.contentapp.detail;

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.actionbar.ActionbarView;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.contentapp.definition.EditorDefinition;
import info.magnolia.ui.vaadin.integration.datasource.DataSource;
import info.magnolia.ui.vaadin.integration.datasource.SupportsCreation;
import info.magnolia.ui.vaadin.integration.datasource.SupportsVersions;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Presenter for the workbench displayed in the {@link info.magnolia.ui.contentapp.detail.DetailSubApp}.
 * Contains the {@link ActionbarPresenter} for handling action events and the {@link DetailPresenter} for displaying the actual item.
 */
public class DetailEditorPresenter implements DetailEditorView.Listener, ActionbarPresenter.Listener {

    private static final Logger log = LoggerFactory.getLogger(DetailEditorPresenter.class);

    private final ActionExecutor actionExecutor;
    private final AppContext appContext;
    private final DetailEditorView view;
    private final DetailPresenter detailPresenter;
    private final ActionbarPresenter actionbarPresenter;
    private final DetailSubAppDescriptor subAppDescriptor;
    private final EditorDefinition editorDefinition;
    private final SimpleTranslator i18n;
    private String nodePath;
    private DataSource dataSource;
    private Item item;

    @Inject
    public DetailEditorPresenter(final ActionExecutor actionExecutor, final SubAppContext subAppContext, final DetailEditorView view, final DetailPresenter detailPresenter, final ActionbarPresenter actionbarPresenter, final SimpleTranslator i18n) {
        this.actionExecutor = actionExecutor;
        this.view = view;
        this.detailPresenter = detailPresenter;
        this.actionbarPresenter = actionbarPresenter;
        this.appContext = subAppContext.getAppContext();
        this.subAppDescriptor = (DetailSubAppDescriptor) subAppContext.getSubAppDescriptor();
        this.editorDefinition = subAppDescriptor.getEditor();
        this.i18n = i18n;
    }

    public View start(String nodePath, DetailView.ViewType viewType, DataSource dataSource) {

        return start(nodePath, viewType, dataSource, null);
    }

    public View start(String nodePath, DetailView.ViewType viewType, DataSource dataSource, String versionName) {
        this.dataSource = dataSource;
        this.nodePath = nodePath;
        this.item = null;

        view.setListener(this);
        Object itemId = dataSource.getItemIdFromPath(nodePath);


        if (dataSource.itemExists(itemId) /* && session.getNode(nodePath).getPrimaryNodeType().getName().equals(editorDefinition.getNodeType().getName())*/) {
            if (StringUtils.isNotEmpty(versionName) && DetailView.ViewType.VIEW.equals(viewType) && dataSource instanceof SupportsVersions) {
                item = ((SupportsVersions)dataSource).getItemVersion(itemId, versionName);
            } else if (dataSource instanceof SupportsCreation) {
                item = dataSource.getItem(itemId);
            }
        } else {
            if (dataSource instanceof SupportsCreation) {
                /**
                 * TODO - consider passing parent's id.
                 */
                item = ((SupportsCreation)dataSource).createNew(nodePath);
            }
        }

        DetailView itemView = detailPresenter.start(editorDefinition, item, viewType, itemId);

        view.setItemView(itemView);
        actionbarPresenter.setListener(this);
        ActionbarView actionbar = actionbarPresenter.start(subAppDescriptor.getActionbar(), subAppDescriptor.getActions());

        view.setActionbarView(actionbar);

        return view;
    }

    public View update(DetailLocation location) {
        return this.start(location.getNodePath(), location.getViewType(), dataSource, location.getVersion());
    }

    public String getNodePath() {
        return nodePath;
    }

    public ActionbarPresenter getActionbarPresenter() {
        return actionbarPresenter;
    }

    @Override
    public void onViewTypeChanged(final DetailView.ViewType viewType) {
        // eventBus.fireEvent(new ViewTypeChangedEvent(viewType));
    }

    @Override
    public void onActionbarItemClicked(String actionName) {
        try {
           actionExecutor.execute(actionName, item);
        }
        catch (ActionExecutionException e) {
            Message error = new Message(MessageType.ERROR, i18n.translate("ui-contentapp.error.action.execution"), e.getMessage());
            appContext.sendLocalMessage(error);
        }
    }

}
