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
package info.magnolia.ui.vaadin.gwt.client.tabsheet.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Util class for the collections operations.
 */
public class CollectionUtil {

    public static <T> T getNext(final List<T> items, T item) {
        int index = items.indexOf(item);
        if (index >= 0) {
            int nextIndex = (index + 1) % items.size();
            return items.get(nextIndex);
        }
        return null;
    }

    public static <T> List<T> makeItemFirst(final List<T> items, T item) {
        List<T> newItems = new LinkedList<T>();
        for (int i = items.indexOf(item); i < items.size(); i++) {
            newItems.add(items.get(i));
        }

        for (int i = items.indexOf(item) - 1; i >= 0; i--) {
            newItems.add(items.get(i));
        }

        return newItems;
    }
}
