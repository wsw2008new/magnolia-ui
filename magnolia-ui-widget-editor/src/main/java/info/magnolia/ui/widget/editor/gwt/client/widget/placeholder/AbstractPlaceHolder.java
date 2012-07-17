/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.ui.widget.editor.gwt.client.widget.placeholder;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import info.magnolia.ui.widget.editor.gwt.client.dom.MgnlElement;
import info.magnolia.ui.widget.editor.gwt.client.model.Model;

/**
 * Abstract Widget for area and component placeholder.
 */
public class AbstractPlaceHolder extends FlowPanel {

    private MgnlElement mgnlElement;
    private Model model;
    private EventBus eventBus;

    public AbstractPlaceHolder(Model model, EventBus eventBus, MgnlElement mgnlElement) {
        super();
        this.model = model;
        this.eventBus = eventBus;

        this.setMgnlElement(mgnlElement);

        setStyleName("mgnlEditor mgnlEditorPlaceholder");
    }

    public void toggleVisible() {
        isVisible();
        setVisible(!isVisible());
    }

    @Override
    protected void onAttach() {
        getModel().addElements(this.getMgnlElement(), getElement());
        super.onAttach();
    }

    public void setMgnlElement(MgnlElement mgnlElement) {
        this.mgnlElement = mgnlElement;
    }

    public MgnlElement getMgnlElement() {
        return mgnlElement;
    }

    public Model getModel() {
        return model;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}