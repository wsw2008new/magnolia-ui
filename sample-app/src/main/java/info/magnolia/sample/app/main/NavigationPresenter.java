/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.sample.app.main;

import info.magnolia.event.EventBus;
import info.magnolia.ui.api.app.AppEventBus;
import info.magnolia.ui.api.app.SubAppContext;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Presenter for the navigation.
 */
public class NavigationPresenter implements NavigationView.Listener {

    private EventBus appEventBus;
    private NavigationView view;
    private SubAppContext subAppContext;

    @Inject
    public NavigationPresenter(@Named(AppEventBus.NAME) EventBus appEventBus, NavigationView view, SubAppContext subAppContext) {
        this.appEventBus = appEventBus;
        this.view = view;
        this.subAppContext = subAppContext;
    }

    public NavigationView start() {
        view.setListener(this);
        return view;
    }

    @Override
    public void onItemSelected(String name) {
        appEventBus.fireEvent(new ContentItemSelectedEvent(name));
    }
}
