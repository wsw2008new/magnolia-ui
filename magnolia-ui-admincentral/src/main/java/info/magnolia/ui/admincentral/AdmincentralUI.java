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
package info.magnolia.ui.admincentral;

import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.objectfactory.Components;
import info.magnolia.objectfactory.configuration.ComponentProviderConfiguration;
import info.magnolia.objectfactory.configuration.ComponentProviderConfigurationBuilder;
import info.magnolia.objectfactory.configuration.ImplementationConfiguration;
import info.magnolia.objectfactory.configuration.InstanceConfiguration;
import info.magnolia.objectfactory.guice.GuiceComponentProvider;
import info.magnolia.objectfactory.guice.GuiceComponentProviderBuilder;
import info.magnolia.ui.framework.event.EventBusProtector;
import info.magnolia.ui.framework.shell.Shell;
import info.magnolia.ui.model.context.UiContext;
import info.magnolia.ui.model.overlay.View;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * The Application's "main" class.
 */
@Theme("admincentral")
@PreserveOnRefresh
public class AdmincentralUI extends UI {

    private static final Logger log = LoggerFactory.getLogger(AdmincentralUI.class);
    private GuiceComponentProvider componentProvider;
    private EventBusProtector eventBusProtector;

    @Override
    protected void init(VaadinRequest request) {
        log.debug("Init AdminCentralApplication...");

        log.debug("Read component configurations from module descriptors...");
        ComponentProviderConfigurationBuilder configurationBuilder = new ComponentProviderConfigurationBuilder();
        List<ModuleDefinition> moduleDefinitions = Components.getComponent(ModuleRegistry.class).getModuleDefinitions();
        ComponentProviderConfiguration admincentralConfig = configurationBuilder.getComponentsFromModules("admincentral", moduleDefinitions);

        ComponentProviderConfiguration configuration = admincentralConfig.clone();
        configuration.addComponent(InstanceConfiguration.valueOf(UI.class, this));
        configuration.addComponent(ImplementationConfiguration.valueOf(UiContext.class, Shell.class));

        eventBusProtector = new EventBusProtector();
        configuration.addConfigurer(eventBusProtector);

        log.debug("Creating the component provider...");
        GuiceComponentProviderBuilder builder = new GuiceComponentProviderBuilder();
        builder.withConfiguration(configuration);
        GuiceComponentProvider parent = (GuiceComponentProvider) Components.getComponentProvider();
        builder.withParent(parent);
        componentProvider = builder.build();

        getPage().setTitle("Magnolia 5.0");

        AdmincentralPresenter presenter = componentProvider.newInstance(AdmincentralPresenter.class);
        View view = presenter.start();
        setContent(view.asVaadinComponent());
    }

    @Override
    public void detach() {
        super.detach();
        eventBusProtector.resetEventBuses();
        componentProvider.destroy();
    }

}
