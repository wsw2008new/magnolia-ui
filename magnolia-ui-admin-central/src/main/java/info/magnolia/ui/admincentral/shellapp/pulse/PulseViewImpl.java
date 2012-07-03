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
package info.magnolia.ui.admincentral.shellapp.pulse;

import info.magnolia.ui.admincentral.components.ActivityItem;
import info.magnolia.ui.admincentral.components.SplitFeed;
import info.magnolia.ui.vaadin.integration.view.IsVaadinComponent;
import info.magnolia.ui.widget.tabsheet.ShellTab;
import info.magnolia.ui.widget.tabsheet.ShellTabSheet;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Default view implementation for Pulse.
 * 
 * @version $Id$
 */
@SuppressWarnings("serial")
public class PulseViewImpl implements PulseView, IsVaadinComponent {

    private ShellTabSheet tabsheet = new ShellTabSheet() {

        @Override
        public void onActiveTabSet(String tabId) {
            super.onActiveTabSet(tabId);
            presenter.onPulseTabChanged(m.getKey(getTabById(tabId)).toString().toLowerCase());
        }
    };

    private enum PulseTabType {
        DASHBOARD, STATISTIC, MESSAGES;

        public static PulseTabType getDefault() {
            return MESSAGES;
        }
    }

    private Presenter presenter;

    private BidiMap m = new DualHashBidiMap();

    private final PulseMessagesView messagesView;

    @Inject
    public PulseViewImpl(final PulseMessagesView messagesView) {

        this.messagesView = messagesView;
        final ShellTab stats = tabsheet.addTab("stats".toUpperCase(), createStatsLayout());
        final ShellTab messages = tabsheet.addTab("messages".toUpperCase(), messagesView.asVaadinComponent());
        final ShellTab dashboard = tabsheet.addTab("dashboard".toUpperCase(), createPulseFeedLayout());

        tabsheet.addStyleName("v-pulse");
        tabsheet.setSizeFull();
        tabsheet.setWidth("900px");

        m.put(PulseTabType.DASHBOARD, dashboard);
        m.put(PulseTabType.STATISTIC, stats);
        m.put(PulseTabType.MESSAGES, messages);
    }

    @Override
    public Component asVaadinComponent() {
        return tabsheet;
    }

    @Override
    public String setCurrentPulseTab(final String tabId, final List<String> params) {
        PulseTabType type = null;
        String finalDisplayedTabId = tabId;
        try {
            type = PulseTabType.valueOf(String.valueOf(tabId).toUpperCase());
        } catch (IllegalArgumentException e) {
            type = PulseTabType.getDefault();
            finalDisplayedTabId = type.name().toLowerCase();
        }
        final ShellTab tab = (ShellTab) m.get(type);
        if (tab != null) {
            tabsheet.setActiveTab(tab);
        }

        switch (type) {
        case MESSAGES:
            messagesView.update(params);
            break;
        default:
            break;
        }
        return finalDisplayedTabId;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private SplitFeed createPulseFeedLayout() {
        final SplitFeed pulseFeed = new SplitFeed();
        pulseFeed.getLeftContainer().setTitle("Activity Stream");
        pulseFeed.getLeftContainer().setTitleLinkEnabled(true);

        pulseFeed.getRightContainer().setTitle("Pages I changed recently");
        pulseFeed.getRightContainer().setTitleLinkEnabled(true);

        final Label l = new Label("Today");
        l.addStyleName("category-separartor");
        pulseFeed.getLeftContainer().addComponent(l);
        pulseFeed.getLeftContainer().addComponent(new ActivityItem("Test", "Lorem ipsum...", "Say hi", "green", new Date()));
        pulseFeed.getLeftContainer().addComponent(new ActivityItem("Test", "Lorem ipsum once again", "Say hi", "red", new Date()));
        return pulseFeed;
    }

    private Panel createStatsLayout() {
        final Panel layout = new Panel();
        layout.setSizeFull();
        layout.addComponent(new Label("Test2".toUpperCase()));
        return layout;
    }
}
