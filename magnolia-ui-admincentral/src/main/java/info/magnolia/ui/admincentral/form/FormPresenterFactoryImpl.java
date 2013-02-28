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
package info.magnolia.ui.admincentral.form;

import info.magnolia.event.EventBus;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.admincentral.field.builder.FieldFactory;
import info.magnolia.ui.admincentral.form.action.FormActionFactory;
import info.magnolia.ui.admincentral.form.builder.FormBuilder;
import info.magnolia.ui.framework.event.AdminCentralEventBusConfigurer;
import info.magnolia.ui.model.form.definition.FormDefinition;
import info.magnolia.ui.vaadin.form.FormView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Factory for {@link FormPresenter}.
 */
public class FormPresenterFactoryImpl implements FormPresenterFactory {

    private final ComponentProvider componentProvider;
    private final FormBuilder formBuilder;
    private final FieldFactory fieldFactory;
    private final EventBus eventBus;
    private final FormActionFactory actionFactory;

    @Inject
    public FormPresenterFactoryImpl(ComponentProvider componentProvider, FormBuilder formBuilder, FieldFactory fieldFactory, @Named(AdminCentralEventBusConfigurer.EVENT_BUS_NAME) EventBus eventBus, final FormActionFactory actionFactory) {
        this.componentProvider = componentProvider;
        this.formBuilder = formBuilder;
        this.fieldFactory = fieldFactory;
        this.eventBus = eventBus;
        this.actionFactory = actionFactory;
    }

    @Override
    public FormPresenter createFormPresenterByDefinition(FormDefinition definition) {
        FormView view = componentProvider.getComponent(FormView.class);
        return new FormPresenterImpl(view, formBuilder, fieldFactory, definition, eventBus, actionFactory);
    }

}
