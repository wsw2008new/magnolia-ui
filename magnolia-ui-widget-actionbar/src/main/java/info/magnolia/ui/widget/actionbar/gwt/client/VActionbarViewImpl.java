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
package info.magnolia.ui.widget.actionbar.gwt.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class VActionbarViewImpl, GWT implementation for the VActionbarView interface.
 */
public class VActionbarViewImpl extends ComplexPanel implements VActionbarView {

    public static final String CLASSNAME = "v-actionbar";

    private final Element root = DOM.createElement("section");

    private Presenter presenter;

    private final List<VActionbarSection> sections = new LinkedList<VActionbarSection>();

    public VActionbarViewImpl() {
        setElement(root);
        setStyleName(CLASSNAME);

        // Element title = DOM.createElement("h3");
        // title.setClassName("section-title");
        // title.setInnerText("Actions");
        // getElement().appendChild(title);

    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean hasChildComponent(Widget component) {
        return getChildren().contains(component);
    }

    @Override
    public void add(Widget w) {
        add(w, getElement());
    }

    @Override
    public void updateSections() {
        VActionbarSection mainSection = new VActionbarSection("Actions");
        VActionbarSection previewSection = new VActionbarSection("Preview");
        sections.add(mainSection);
        sections.add(previewSection);

        for (VActionbarSection section : sections) {
            add(section);
            section.updateGroups();
        }
    }

}
