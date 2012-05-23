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
package info.magnolia.m5admincentral.framework;

import info.magnolia.m5vaadin.IsVaadinComponent;
import info.magnolia.m5vaadin.tabsheet.ShellTab;
import info.magnolia.m5vaadin.tabsheet.ShellTabSheet;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * Base class for app view implementations.
 * @author apchelintcev
 */
@SuppressWarnings("serial")
public abstract class AppViewImpl implements AppView, IsVaadinComponent {
    
    private ShellTabSheet tabsheet = new ShellTabSheet();
   
    private Presenter presenter;
    
    public AppViewImpl() {
        tabsheet.setSizeFull();
    }
    
    protected Presenter getPresenter() {
        return presenter;
    }
    
    @Override
    public void addTab(ComponentContainer cc, String caption) {
        final ShellTab tab = new ShellTab(caption, cc);
        tabsheet.addComponent(tab);
        tabsheet.setTabClosable(tab, true);
        tabsheet.setActiveTab(tab);
    }

    @Override
    public void closeTab(ComponentContainer c) {
        tabsheet.removeComponent(c);
    }
    
    @Override
    public Component asVaadinComponent() {
        return tabsheet;
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
