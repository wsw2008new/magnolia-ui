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
package info.magnolia.ui.admincentral.shellapp.favorites;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.ui.api.shell.Shell;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.util.Map;
import java.util.Map.Entry;

/**
 * FavoritesForm.
 */
public final class FavoritesForm extends CustomComponent {

    private FavoritesView.Listener listener;
    private Shell shell;
    private TabSheet tabsheet;
    private Label arrowIcon;
    private InternalFavoriteEntryForm favoriteEntryForm;
    private InternalFavoriteGroupForm favoriteGroupForm;

    public FavoritesForm(final JcrNewNodeAdapter newFavorite, final JcrNewNodeAdapter newGroup, final Map<String, String> availableGroups, final FavoritesView.Listener listener, final Shell shell) {
        addStyleName("favorites-form");
        this.listener = listener;
        this.shell = shell;

        final VerticalLayout favoriteForm = new VerticalLayout();
        favoriteEntryForm = new InternalFavoriteEntryForm(newFavorite, availableGroups);
        favoriteGroupForm = new InternalFavoriteGroupForm(newGroup);

        tabsheet = new TabSheet();
        tabsheet.addStyleName("favorites-tabs");
        tabsheet.addTab(favoriteEntryForm, MessagesUtil.get("favorites.form.favorite.add"));
        tabsheet.addTab(favoriteGroupForm, MessagesUtil.get("favorites.form.group.add"));

        tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (event.getTabSheet().getSelectedTab() instanceof InternalFavoriteEntryForm) {
                    favoriteGroupForm.removeEnterKeyShortcutListener();
                    favoriteEntryForm.addEnterKeyShortcutListener();
                } else {
                    favoriteEntryForm.removeEnterKeyShortcutListener();
                    favoriteGroupForm.addEnterKeyShortcutListener();
                }
            }
        });

        final CssLayout header = new CssLayout();
        header.addStyleName("dialog-header");
        header.setSizeFull();
        header.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                if (isOpen()) {
                    close();
                } else {
                    open();
                }
            }
        });

        final Label addNewIcon = new Label();
        addNewIcon.setSizeUndefined();
        addNewIcon.addStyleName("icon");
        addNewIcon.addStyleName("icon-add-fav");

        final Label addNewLabel = new Label(MessagesUtil.get("favorites.form.add"));
        addNewLabel.setSizeUndefined();
        addNewLabel.addStyleName("title");

        arrowIcon = new Label();
        arrowIcon.setSizeUndefined();
        arrowIcon.addStyleName("icon");
        arrowIcon.addStyleName("arrow");
        arrowIcon.addStyleName("icon-arrow2_n");

        header.addComponent(addNewIcon);
        header.addComponent(addNewLabel);
        header.addComponent(arrowIcon);
        favoriteForm.addComponent(header);
        favoriteForm.addComponent(tabsheet);

        // form is closed initially
        close();

        setCompositionRoot(favoriteForm);
    }

    public void close() {
        tabsheet.setVisible(false);
        arrowIcon.removeStyleName("icon-arrow2_s");
        arrowIcon.addStyleName("icon-arrow2_n");
        // remove key shortcut listener or this might compete with the next element getting the focus.
        favoriteEntryForm.removeEnterKeyShortcutListener();
        favoriteGroupForm.removeEnterKeyShortcutListener();
    }

    public void open() {
        tabsheet.setVisible(true);
        arrowIcon.removeStyleName("icon-arrow2_n");
        arrowIcon.addStyleName("icon-arrow2_s");
        favoriteEntryForm.addEnterKeyShortcutListener();
        // the group form will get the key shortcut listener attached on selecting it.
    }

    public boolean isOpen() {
        return tabsheet.isVisible();
    }

    /**
     * The form component displayed in the favorite tab.
     */
    private class InternalFavoriteEntryForm extends CustomComponent {

        private TextField url = new TextField(MessagesUtil.get("favorites.form.location"));

        private TextField title = new TextField(MessagesUtil.get("favorites.form.title"));

        private ComboBox group;

        private ShortcutListener enterShortcutListener;

        public InternalFavoriteEntryForm(final JcrNewNodeAdapter newFavorite, final Map<String, String> availableGroups) {
            addStyleName("favorites-form-content");
            FormLayout layout = new FormLayout();
            layout.setSizeUndefined();

            title.setRequired(true);
            url.setRequired(true);
            layout.addComponent(title);
            layout.addComponent(url);

            group = new ComboBox(MessagesUtil.get("favorites.form.groups"));
            for (Entry<String, String> entry : availableGroups.entrySet()) {
                String id = entry.getKey();
                group.addItem(id);
                group.setItemCaption(id, entry.getValue());
            }
            layout.addComponent(group);

            // Now use a binder to bind the members
            final FieldGroup binder = new FieldGroup(newFavorite);
            binder.bindMemberFields(this);

            final CssLayout buttons = new CssLayout();
            buttons.addStyleName("buttons");

            final Button addButton = new Button(MessagesUtil.get("buttons.add"), new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    addFavorite(newFavorite, binder);
                }
            });
            addButton.addStyleName("commit");
            buttons.addComponent(addButton);
            layout.addComponent(buttons);


            this.enterShortcutListener = new ShortcutListener("", KeyCode.ENTER, null) {

                @Override
                public void handleAction(Object sender, Object target) {
                    addFavorite(newFavorite, binder);
                }
            };

            setCompositionRoot(layout);
        }

        public void addEnterKeyShortcutListener() {
            addShortcutListener(enterShortcutListener);
            title.focus();
        }

        public void removeEnterKeyShortcutListener() {
            removeShortcutListener(enterShortcutListener);
        }

        private void addFavorite(final JcrNewNodeAdapter newFavorite, final FieldGroup binder) {
            try {
                binder.commit();
                listener.addFavorite(newFavorite);
            } catch (CommitException e) {
                shell.openNotification(MessageStyleTypeEnum.ERROR, true, MessagesUtil.get("favorites.fields.required"));
            }
        }
    }

    /**
     * The form component displayed in the group tab.
     */
    private class InternalFavoriteGroupForm extends CustomComponent {

        private TextField title = new TextField(MessagesUtil.get("favorites.form.title"));
        private ShortcutListener enterShortcutListener;

        public InternalFavoriteGroupForm(final JcrNewNodeAdapter newGroup) {
            addStyleName("favorites-form-content");
            FormLayout layout = new FormLayout();

            title.setRequired(true);
            layout.addComponent(title);

            // Now use a binder to bind the members
            final FieldGroup binder = new FieldGroup(newGroup);
            binder.bindMemberFields(this);

            final CssLayout buttons = new CssLayout();
            buttons.addStyleName("buttons");

            final Button addButton = new Button(MessagesUtil.get("buttons.add"), new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    addGroup(newGroup, binder);
                }
            });
            addButton.addStyleName("v-button-commit");
            buttons.addComponent(addButton);
            layout.addComponent(buttons);

            this.enterShortcutListener = new ShortcutListener("", KeyCode.ENTER, null) {

                @Override
                public void handleAction(Object sender, Object target) {
                    addGroup(newGroup, binder);
                }
            };

            setCompositionRoot(layout);
        }

        public void addEnterKeyShortcutListener() {
            addShortcutListener(enterShortcutListener);
            title.focus();
        }

        public void removeEnterKeyShortcutListener() {
            removeShortcutListener(enterShortcutListener);
        }

        private void addGroup(final JcrNewNodeAdapter newGroup, final FieldGroup binder) {
            try {
                binder.commit();
                listener.addGroup(newGroup);
            } catch (CommitException e) {
                shell.openNotification(MessageStyleTypeEnum.ERROR, true, MessagesUtil.get("favorites.fields.required"));
            }
        }
    }
}
