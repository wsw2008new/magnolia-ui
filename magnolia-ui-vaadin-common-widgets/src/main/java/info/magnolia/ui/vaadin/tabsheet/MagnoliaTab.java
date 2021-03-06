/**
 * This file Copyright (c) 2010-2015 Magnolia International
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
package info.magnolia.ui.vaadin.tabsheet;

import info.magnolia.ui.vaadin.gwt.client.tabsheet.tab.connector.MagnoliaTabState;

import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.SelectiveRenderer;

/**
 * A tab in the shell tabsheet.
 */
public class MagnoliaTab extends AbstractSingleComponentContainer implements SelectiveRenderer {

    public MagnoliaTab(final String caption, final Component c) {
        setSizeFull();
        setImmediate(true);
        setCaption(caption);
        setContent(c);
    }

    public boolean isClosable() {
        return getState(false).isClosable;
    }

    @Override
    protected MagnoliaTabState getState(boolean markAsDirty) {
        return (MagnoliaTabState) super.getState(markAsDirty);
    }

    @Override
    protected MagnoliaTabState getState() {
        return (MagnoliaTabState) super.getState();
    }

    @Override
    public boolean isRendered(Component childComponent) {
        return getState().isActive;
    }

    public void setClosable(boolean isClosable) {
        getState().isClosable = isClosable;
    }

    public void setNotification(String text) {
        getState().notification = text;
    }

    public void setHasError(boolean hasError) {
        getState().hasError = hasError;
    }

    public void hideNotification() {
        getState().isNotificationHidden = true;
    }

    public String getNotification() {
        return getState(false).notification;
    }

    public boolean hasNotification() {
        return getNotification() != null;
    }

    public boolean hasError() {
        return getState(false).hasError;
    }

    @Override
    public MagnoliaTabSheet getParent() {
        return (MagnoliaTabSheet) super.getParent();
    }

    @Override
    public void setContent(Component content) {
        final Component currentContent = super.getContent();
        if (currentContent != null) {
            content.setVisible(currentContent.isVisible());
        }
        super.setContent(content);
    }

    public void setActive(boolean active) {
        getState().isActive = active;
    }
}
