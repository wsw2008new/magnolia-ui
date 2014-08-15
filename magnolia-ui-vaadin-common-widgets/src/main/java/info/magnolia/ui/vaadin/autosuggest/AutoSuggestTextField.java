/**
 * This file Copyright (c) 2014 Magnolia International
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
package info.magnolia.ui.vaadin.autosuggest;

import info.magnolia.ui.api.autosuggest.AutoSuggester.AutoSuggesterResult;
import info.magnolia.ui.vaadin.gwt.client.autosuggest.AutoSuggestTextFieldState;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * An extension of {@link TextField} that displays a drop-down of suggestions for possible values.
 */
public class AutoSuggestTextField extends TextField {

    public AutoSuggestTextField() {
        super();
    }

    public AutoSuggestTextField(String caption) {
        super(caption);
    }

    public AutoSuggestTextField(Property dataSource) {
        super(dataSource);
    }

    public AutoSuggestTextField(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    public AutoSuggestTextField(String caption, String value) {
        super(caption, value);
    }

    public AutoSuggestTextField(AutoSuggesterResult autoSuggesterResult) {
        super();
        setStateAccordingToAutoSuggesterResult(autoSuggesterResult);
    }

    public AutoSuggestTextField(String caption, AutoSuggesterResult autoSuggesterResult) {
        super(caption);
        setStateAccordingToAutoSuggesterResult(autoSuggesterResult);
    }

    public AutoSuggestTextField(Property dataSource, AutoSuggesterResult autoSuggesterResult) {
        super(dataSource);
        setStateAccordingToAutoSuggesterResult(autoSuggesterResult);
    }

    public AutoSuggestTextField(String caption, Property dataSource, AutoSuggesterResult autoSuggesterResult) {
        super(caption, dataSource);
        setStateAccordingToAutoSuggesterResult(autoSuggesterResult);
    }

    public AutoSuggestTextField(String caption, String value, AutoSuggesterResult autoSuggesterResult) {
        super(caption, value);
        setStateAccordingToAutoSuggesterResult(autoSuggesterResult);
    }

    private void setStateAccordingToAutoSuggesterResult(AutoSuggesterResult autoSuggesterResult) {
        if (autoSuggesterResult != null) {
            AutoSuggestTextFieldState state = this.getState();
            state.suggestionsAvailable = autoSuggesterResult.suggestionsAvailable();
            state.suggestions = new ArrayList<String>();
            Collection<String> suggestions = autoSuggesterResult.getSuggestions();
            if (state.suggestionsAvailable && suggestions != null) {
                state.suggestions.addAll(suggestions);
            }
            state.matchMethod = autoSuggesterResult.getMatchMethod();
            state.showMismatchedSuggestions = autoSuggesterResult.showMismatchedSuggestions();
            state.showErrorHighlighting = autoSuggesterResult.showErrorHighlighting();
        }
    }

    @Override
    protected AutoSuggestTextFieldState getState() {
        return (AutoSuggestTextFieldState) super.getState();
    }
}
