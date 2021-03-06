/**
 * This file Copyright (c) 2013-2015 Magnolia International
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
package info.magnolia.ui.vaadin.gwt.client.layout.thumbnaillayout.widget;

import info.magnolia.ui.vaadin.gwt.client.icon.widget.IconWidget;
import info.magnolia.ui.vaadin.gwt.client.mgwt.SliderClientBundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.googlecode.mgwt.ui.client.widget.MSlider;

/**
 * Simple client-side slider widget, wraps mgwt's slider.
 */
public class Slider extends Composite implements HasValueChangeHandlers<Integer>, HasValue<Integer> {

    private final SliderClientBundle cssBundle = GWT.create(SliderClientBundle.class);

    private final MSlider slider = new MSlider(cssBundle.css());

    private FlowPanel panel = new FlowPanel();

    public Slider() {
        super();

        IconWidget iconSizeSmall = new IconWidget();
        iconSizeSmall.setIconName("search");
        iconSizeSmall.setSize(20);
        iconSizeSmall.setColor("#aaa");
        iconSizeSmall.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.TOP);

        panel.addStyleName("slider-panel");
        panel.getElement().getStyle().setPadding(10, Style.Unit.PX);
        panel.add(iconSizeSmall);
        panel.add(slider);

        slider.setWidth("125px");
        initWidget(panel);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> integerValueChangeHandler) {
        return slider.addValueChangeHandler(integerValueChangeHandler);
    }

    @Override
    public Integer getValue() {
        return slider.getValue();
    }

    @Override
    public void setValue(Integer value) {
        slider.setValue(value);
    }

    @Override
    public void setValue(Integer value, boolean fireEvents) {
        slider.setValue(value, fireEvents);
    }
}
