/**
 * This file Copyright (c) 2014-2015 Magnolia International
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
package info.magnolia.ui.admincentral.shellapp.pulse;

import info.magnolia.event.EventBus;
import info.magnolia.event.EventHandler;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.registry.RegistrationException;
import info.magnolia.ui.admincentral.shellapp.pulse.item.PulseListDefinition;
import info.magnolia.ui.admincentral.shellapp.pulse.item.detail.PulseItemCategory;
import info.magnolia.ui.admincentral.shellapp.pulse.item.list.PulseListPresenter;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.framework.shell.ShellImpl;
import info.magnolia.ui.vaadin.gwt.client.shared.magnoliashell.ShellAppType;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Presenter of {@link PulseView}.
 */
public final class PulsePresenter implements PulseListPresenter.Listener, PulseView.Listener, EventHandler {

    private static final Logger log = LoggerFactory.getLogger(PulsePresenter.class);

    private PulseView view;
    private ShellImpl shell;
    private PulseItemCategory selectedCategory;
    private boolean isDisplayingDetailView;
    private Map<String, PulseListPresenter> presenters = new HashMap<>();
    private PulseDefinition definition;
    private ComponentProvider componentProvider;

    @Inject
    public PulsePresenter(ConfiguredPulseDefinition definition, @Named(AdmincentralEventBus.NAME) final EventBus admincentralEventBus, final PulseView view, final ShellImpl shell, ComponentProvider componentProvider) {
        this.view = view;
        this.shell = shell;
        this.componentProvider = componentProvider;
        this.definition = definition;

        updatePulseCounter();
    }

    public View start() {
        view.setListener(this);

        for (PulseListDefinition defPresenter : definition.getPresenters()) {
            PulseListPresenter presenter = componentProvider.newInstance(defPresenter.getPresenterClass(), definition);
            presenter.setListener(this);
            presenters.put(defPresenter.getName(), presenter);
        }

        if (presenters.size() > 0) {
            selectedCategory = presenters.values().iterator().next().getCategory();
            view.setPulseSubView(presenters.values().iterator().next().start());
        }
        return view;
    }

    @Override
    public void onCategoryChange(PulseItemCategory category) {
        selectedCategory = category;
        showList();
    }

    @Override
    public void showList() {
        for (PulseListPresenter presenter : presenters.values()) {
            if (selectedCategory == presenter.getCategory()) {
                view.setPulseSubView(presenter.start());
                break;
            }
        }
        isDisplayingDetailView = false;
    }

    public boolean isDisplayingDetailView() {
        return isDisplayingDetailView;
    }

    @Override
    public void openItem(String identifier, String itemId) {
        try {
            view.setPulseSubView(presenters.get(identifier).openItem(itemId));
            isDisplayingDetailView = true;

        } catch (RegistrationException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void updatePulseCounter() {
        int totalItem = 0;
        for (PulseListPresenter presenter : presenters.values()) {
            int pendingItem = presenter.getPendingItemCount();
            view.updateCategoryBadgeCount(presenter.getCategory(), pendingItem);
            totalItem += pendingItem;

        }

        shell.setIndication(ShellAppType.PULSE, totalItem);
    }

    @Override
    public void updateView(PulseItemCategory activeTab) {
        // update top navigation and load new tasks
        selectedCategory = PulseItemCategory.TASKS;
        view.setTabActive(PulseItemCategory.TASKS);
        if (isDisplayingDetailView) {
            showList();
        }
    }
}
