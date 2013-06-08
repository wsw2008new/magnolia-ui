/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.ui.app.pages.action;

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.ui.admincentral.dialog.action.CallbackDialogActionDefinition;
import info.magnolia.ui.admincentral.dialog.action.CancelDialogActionDefinition;
import info.magnolia.ui.api.ModelConstants;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.app.pages.field.TemplateSelectorFieldBuilder;
import info.magnolia.ui.dialog.FormDialogPresenter;
import info.magnolia.ui.dialog.definition.ConfiguredDialogDefinition;
import info.magnolia.ui.dialog.definition.DialogDefinition;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.definition.ConfiguredFormDefinition;
import info.magnolia.ui.form.definition.ConfiguredTabDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.framework.app.SubAppContext;
import info.magnolia.ui.framework.app.SubAppEventBus;
import info.magnolia.ui.framework.event.ContentChangedEvent;
import info.magnolia.ui.vaadin.gwt.client.shared.AreaElement;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds a component underneath the area passed in {@link AreaElement}.
 * Gets a list of available components for this area and creates a select field.
 */
public class CreateComponentAction extends AbstractAction<CreateComponentActionDefinition> {

    private static final Logger log = LoggerFactory.getLogger(CreateComponentAction.class);

    private AreaElement area;
    private EventBus eventBus;
    private TemplateDefinitionRegistry templateDefinitionRegistry;
    private SubAppContext subAppContext;
    private ComponentProvider componentProvider;

    @Inject
    public CreateComponentAction(CreateComponentActionDefinition definition, AreaElement area, @Named(SubAppEventBus.NAME) EventBus eventBus, TemplateDefinitionRegistry templateDefinitionRegistry,
                                 SubAppContext subAppContext, ComponentProvider componentProvider) {
        super(definition);
        this.area = area;
        this.eventBus = eventBus;
        this.templateDefinitionRegistry = templateDefinitionRegistry;
        this.subAppContext = subAppContext;
        this.componentProvider = componentProvider;
    }

    @Override
    public void execute() throws ActionExecutionException {
        final DialogDefinition dialogDefinition = buildNewComponentDialog(area.getAvailableComponents());

        final FormDialogPresenter formDialogPresenter = componentProvider.getComponent(FormDialogPresenter.class);
        try {
            String workspace = area.getWorkspace();
            String path = area.getPath();
            Session session = MgnlContext.getJCRSession(area.getWorkspace());
            if (path == null || !session.itemExists(path)) {
                path = "/";
            }
            session = MgnlContext.getJCRSession(workspace);

            Node areaNode = session.getNode(path);

            final JcrNodeAdapter item = new JcrNewNodeAdapter(areaNode, NodeTypes.Component.NAME);
            DefaultProperty<String> property = new DefaultProperty<String>(String.class, "0");
            item.addItemProperty(ModelConstants.JCR_NAME, property);

            // perform custom chaining of dialogs
            formDialogPresenter.start(item, dialogDefinition, subAppContext, new EditorCallback() {

                @Override
                public void onSuccess(String actionName) {
                    String templateId = String.valueOf(item.getItemProperty("mgnl:template").getValue());
                    try {
                        TemplateDefinition templateDef = templateDefinitionRegistry.getTemplateDefinition(templateId);
                        String dialogName = templateDef.getDialog();


                        final FormDialogPresenter dialogPresenter = componentProvider.getComponent(FormDialogPresenter.class);

                        openDialog(item, dialogName, dialogPresenter);
                    } catch (RegistrationException e) {
                        log.error("Exception caught: {}", e.getMessage(), e);
                    } finally {
                        formDialogPresenter.closeDialog();
                    }
                }

                @Override
                public void onCancel() {
                    formDialogPresenter.closeDialog();
                }
            });
        } catch (RepositoryException e) {
            throw new ActionExecutionException(e);
        }
    }

    private void openDialog(final JcrNodeAdapter item, String dialogName, final FormDialogPresenter dialogPresenter) {
        dialogPresenter.start(item, dialogName, subAppContext, new EditorCallback() {

            @Override
            public void onSuccess(String actionName) {
                eventBus.fireEvent(new ContentChangedEvent(item.getWorkspace(), item.getItemId()));
                dialogPresenter.closeDialog();
            }

            @Override
            public void onCancel() {
                dialogPresenter.closeDialog();
            }
        });
    }

    /**
     * Builds a new {@link DialogDefinition} containing actions and {@link info.magnolia.ui.form.definition.FormDefinition}.
     * The definition will hold a {@link info.magnolia.ui.form.field.definition.SelectFieldDefinition} with the available components as options.
     */
    private DialogDefinition buildNewComponentDialog(String availableComponents) {

        ConfiguredFormDefinition form = new ConfiguredFormDefinition();
        form.setDescription("Select the Component to add to the page.");

        ConfiguredTabDefinition tab = new ConfiguredTabDefinition();
        tab.setName("Components");
        tab.setLabel("Components");

        SelectFieldDefinition select = new SelectFieldDefinition();
        select.setName("mgnl:template");
        select.setLabel("Component");
        tab.addField(select);

        String[] tokens = availableComponents.split(",");

        for (String token : tokens) {
            try {
                TemplateDefinition templateDefinition = templateDefinitionRegistry.getTemplateDefinition(token);

                SelectFieldOptionDefinition option = new SelectFieldOptionDefinition();
                option.setValue(templateDefinition.getId());
                option.setLabel(TemplateSelectorFieldBuilder.getI18nTitle(templateDefinition));
                select.addOption(option);

            } catch (RegistrationException e) {
                log.error("Exception caught: {}", e.getMessage(), e);
            }
        }

        ConfiguredDialogDefinition dialog = new ConfiguredDialogDefinition();
        dialog.setId("newComponent");
        dialog.setForm(form);

        CallbackDialogActionDefinition callbackAction = new CallbackDialogActionDefinition();
        callbackAction.setName("commit");
        callbackAction.setLabel("choose");
        dialog.getActions().put(callbackAction.getName(), callbackAction);

        CancelDialogActionDefinition cancelAction = new CancelDialogActionDefinition();
        cancelAction.setName("cancel");
        cancelAction.setLabel("cancel");
        dialog.getActions().put(cancelAction.getName(), cancelAction);

        return dialog;
    }
}
