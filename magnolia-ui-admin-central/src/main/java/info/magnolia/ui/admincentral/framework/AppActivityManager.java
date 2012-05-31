/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.ui.admincentral.framework;

import java.util.HashMap;
import java.util.Map;

import info.magnolia.ui.admincentral.app.AppCategory;
import info.magnolia.ui.admincentral.app.AppController;
import info.magnolia.ui.admincentral.app.AppDescriptor;
import info.magnolia.ui.admincentral.app.AppRegistry;
import info.magnolia.ui.admincentral.app.PlaceActivityMapping;
import info.magnolia.ui.framework.activity.Activity;
import info.magnolia.ui.framework.activity.ActivityManager;
import info.magnolia.ui.framework.activity.ActivityMapperImpl;
import info.magnolia.ui.framework.event.EventBus;
import info.magnolia.ui.framework.place.Place;

/**
 * Activity manager responsible for the app management.
 *
 * @version $Id$
 */
public class AppActivityManager extends ActivityManager {

    private AppController appController;
    private final Map<Class<? extends Place>, String> placeToAppMapping = new HashMap<Class<? extends Place>, String>();

    public AppActivityManager(ActivityMapperImpl appActivityMapper, EventBus eventBus, AppRegistry appRegistry, AppController appController) {
        super(appActivityMapper, eventBus);
        this.appController = appController;

        // Build lookup table for place to app, see #beforeActivityStarts
        for (AppCategory category : appRegistry.getCategories()) {
            for (AppDescriptor descriptor : category.getApps()) {
                for (PlaceActivityMapping mapping : descriptor.getActivityMappings()) {
                    placeToAppMapping.put(mapping.getPlace(), descriptor.getName());
                }
            }
        }
    }

    @Override
    protected void beforeActivityStarts(Activity currentActivity, Place newPlace) {

        // Make sure the app for this place is running before the activity starts
        String appName = placeToAppMapping.get(newPlace.getClass());
        if (appName != null) {
            appController.startIfNotAlreadyRunning(appName);
        }
    }
}
