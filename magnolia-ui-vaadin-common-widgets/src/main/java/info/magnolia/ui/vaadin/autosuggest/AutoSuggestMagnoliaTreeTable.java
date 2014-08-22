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

import info.magnolia.ui.api.autosuggest.AutoSuggester;
import info.magnolia.ui.api.autosuggest.AutoSuggester.AutoSuggesterResult;
import info.magnolia.ui.vaadin.grid.MagnoliaTreeTable;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Property;

/**
 * An extension of {@link MagnoliaTreeTable} that highlights possible mistakes in the tree using a colored squiggly line.
 */
public class AutoSuggestMagnoliaTreeTable extends MagnoliaTreeTable {

    private AutoSuggester autoSuggester = null;

    public AutoSuggestMagnoliaTreeTable() {
        super();
        this.autoSuggester = null;
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
        if (property == null) {
            return "";
        }

        if (autoSuggester != null) {
            Object value = property.getValue();
            if (value != null) {
                AutoSuggesterResult result = autoSuggester.getSuggestionsFor(rowId, colId);
                if (result != null && result.suggestionsAvailable()) {
                    if (result.showErrorHighlighting()) {
                        Collection<String> suggestions = result.getSuggestions();
                        if (suggestions != null) {
                            if (!suggestions.contains(value.toString())) {
                                return "<span class=\"suggestion-warning\">" + super.formatPropertyValue(rowId, colId, property) + "</span>";
                            }
                        }
                    }
                }
            }
        }

        return super.formatPropertyValue(rowId, colId, property);
    }

    public AutoSuggestMagnoliaTreeTable(Container dataSource) {
        super(dataSource);
        this.autoSuggester = null;
    }

    public void setAutoSuggester(AutoSuggester autoSuggester) {
        this.autoSuggester = autoSuggester;
    }
}
