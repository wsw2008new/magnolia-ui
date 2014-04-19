/**
 * This file Copyright (c) 2012-2014 Magnolia International
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
package info.magnolia.ui.contentapp;


import info.magnolia.event.EventBus;
import info.magnolia.event.SimpleEventBus;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.configuration.ComponentConfigurer;
import info.magnolia.objectfactory.configuration.ComponentProviderConfiguration;
import info.magnolia.objectfactory.configuration.ComponentProviderConfigurationBuilder;
import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;
import info.magnolia.objectfactory.guice.GuiceComponentProvider;
import info.magnolia.objectfactory.guice.GuiceComponentProviderBuilder;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppView;
import info.magnolia.ui.api.app.ChooseDialogCallback;
import info.magnolia.ui.api.app.SubAppDescriptor;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.ChooseDialogEventBus;
import info.magnolia.ui.contentapp.choosedialog.ContentAppChooseDialogPresenter;
import info.magnolia.ui.contentapp.definition.ContentSubAppDescriptor;
import info.magnolia.ui.contentapp.choosedialog.ChooseDialogContentConnectorProvider;
import info.magnolia.ui.dialog.choosedialog.ChooseDialogPresenter;
import info.magnolia.ui.dialog.definition.ChooseDialogDefinition;
import info.magnolia.ui.dialog.definition.ConfiguredChooseDialogDefinition;
import info.magnolia.ui.framework.app.BaseApp;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnectorDefinition;

import java.util.List;

import javax.inject.Inject;

import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/**
 * Extends the {@link BaseApp} by the ability to open a choose dialog.
 */
public class ContentApp extends BaseApp {

    private static final String CHOOSE_DIALOG_COMPONENT_ID = "choosedialog";

    private ComponentProvider componentProvider;

    @Inject
    public ContentApp(AppContext appContext, AppView view, ComponentProvider componentProvider) {
        super(appContext, view);
        this.componentProvider = componentProvider;
    }

    @Override
    public void openChooseDialog(UiContext overlayLayer, String selectedId, final ChooseDialogCallback callback) {
        ChooseDialogPresenter presenter;
        ChooseDialogDefinition chooseDialogDefinition = (appContext.getAppDescriptor() instanceof ContentAppDescriptor) ?
                ((ContentAppDescriptor)appContext.getAppDescriptor()).getChooseDialog() : new ConfiguredChooseDialogDefinition();
        ComponentProvider chooseDialogComponentProvider = createChooseDialogComponentProvider(chooseDialogDefinition);

        if (appContext.getAppDescriptor() instanceof ContentAppDescriptor) {
            ContentAppDescriptor contentAppDescriptor = (ContentAppDescriptor)appContext.getAppDescriptor();
            presenter = chooseDialogComponentProvider.newInstance(contentAppDescriptor.getChooseDialog().getPresenterClass(), chooseDialogComponentProvider);
        } else {
            presenter = chooseDialogComponentProvider.newInstance(ContentAppChooseDialogPresenter.class, chooseDialogComponentProvider);
        }

        presenter.start(callback, chooseDialogDefinition, overlayLayer, selectedId) ;
    }

    ComponentProvider createChooseDialogComponentProvider(final ChooseDialogDefinition chooseDialogDefinition) {
        ModuleRegistry moduleRegistry = componentProvider.getComponent(ModuleRegistry.class);
        final EventBus eventBus = new SimpleEventBus();
        ComponentProviderConfigurationBuilder configurationBuilder = new ComponentProviderConfigurationBuilder();
        List<ModuleDefinition> moduleDefinitions = moduleRegistry.getModuleDefinitions();
        ComponentProviderConfiguration configuration =
            configurationBuilder.getComponentsFromModules(CHOOSE_DIALOG_COMPONENT_ID, moduleDefinitions);

        GuiceComponentProviderBuilder builder = new GuiceComponentProviderBuilder();
        builder.withConfiguration(configuration);
        builder.withParent((GuiceComponentProvider) componentProvider);

        SubAppDescriptor defaultSubAppDescriptor = appContext.getDefaultSubAppDescriptor();
        ContentConnectorDefinition contentConnectorDefinition = chooseDialogDefinition.getContentConnector();
        if (contentConnectorDefinition == null && defaultSubAppDescriptor instanceof ContentSubAppDescriptor) {
            contentConnectorDefinition = ((ContentSubAppDescriptor)defaultSubAppDescriptor).getContentConnector();
        }

        final ContentConnectorDefinition definition = contentConnectorDefinition;
        ComponentConfigurer c = new AbstractGuiceComponentConfigurer() {
            @Override
            protected void configure() {
                bind(EventBus.class).annotatedWith(Names.named(ChooseDialogEventBus.NAME)).toProvider(Providers.of(eventBus));
                bind(ContentConnector.class).toProvider(new ChooseDialogContentConnectorProvider(definition, componentProvider));
            }
        };

        return builder.build(c);
    }
}
