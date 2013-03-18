/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.ui.mediaeditor;

import info.magnolia.event.HandlerRegistration;
import info.magnolia.ui.mediaeditor.editmode.event.MediaEditorCompletedEvent;
import info.magnolia.ui.mediaeditor.editmode.field.MediaField;
import info.magnolia.ui.mediaeditor.editmode.provider.EditModeProvider;
import info.magnolia.ui.model.action.ActionExecutor;
import info.magnolia.ui.mediaeditor.definition.MediaEditorDefinition;
import info.magnolia.ui.vaadin.view.View;

import java.io.InputStream;

/**
 * Entry point of the media editor. Capable of creating the view, firing callbacks on media editing completion, 
 * switching between edit modes etc. Currently requires an {@link ActionExecutor} to be set externally.
 * Normally instance of this class are created by {@link MediaEditorPresenterFactory}.
 */
public interface MediaEditorPresenter {

    View start(InputStream stream);

    HandlerRegistration addCompletionHandler(MediaEditorCompletedEvent.Handler handler);

    MediaEditorDefinition getDefinition();

    void switchEditMode(EditModeProvider provider);

    MediaField getCurrentMediaField();
    
    void setActionExecutor(ActionExecutor actionExecutor);

}
