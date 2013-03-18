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
package info.magnolia.ui.vaadin.gwt.client.form.widget;

import info.magnolia.ui.vaadin.gwt.client.editorlike.widget.EditorLikeHeaderWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * VFormHeader.
 */
public class FormHeaderWidget extends EditorLikeHeaderWidget {

    private static final String ClASSNAME_ERROR = "form-error";

    private FlowPanel errorPanel;

    public FormHeaderWidget(FormHeaderCallback callback) {
        super(callback);
    }

    @Override
    public void construct() {
        super.construct();
        errorPanel = new FlowPanel();
        errorPanel.addStyleName(ClASSNAME_ERROR);
        add(errorPanel);
        errorPanel.setVisible(false);
    }

    /**
     * Callback interface for the Form header.
     */
    public interface FormHeaderCallback extends EditorLikeHeaderWidget.VEditorLikeHeaderCallback {

        void jumpToNextError();
    }

    public void setErrorAmount(int totalProblematicFields) {
        errorPanel.setVisible(totalProblematicFields > 0);
        if (totalProblematicFields > 0) {
            errorPanel.getElement().setInnerHTML("<span>Please correct the <b>" + totalProblematicFields + " errors </b> in this form </span>");

            final HTML errorButton = new HTML("[Jump to next error]");
            errorButton.setStyleName("action-jump-to-next-error");
            DOM.sinkEvents(errorButton.getElement(), Event.MOUSEEVENTS);
            errorButton.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ((FormHeaderCallback) callback).jumpToNextError();
                }
            }, ClickEvent.getType());
            errorPanel.add(errorButton);
        }
    }

}
