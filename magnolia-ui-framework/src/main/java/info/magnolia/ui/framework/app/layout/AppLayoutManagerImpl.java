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
package info.magnolia.ui.framework.app.layout;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.ui.framework.app.AppDescriptor;
import info.magnolia.ui.framework.app.layout.event.LayoutEvent;
import info.magnolia.ui.framework.app.layout.event.LayoutEventType;
import info.magnolia.ui.framework.app.registry.AppDescriptorRegistry;
import info.magnolia.ui.framework.app.registry.AppRegistryEvent;
import info.magnolia.ui.framework.app.registry.AppRegistryEventHandler;
import info.magnolia.ui.framework.event.SystemEventBus;

/**
 * Default {@link AppLayoutManager} implementation.
 */
@Singleton
public class AppLayoutManagerImpl implements AppLayoutManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Reference to the injected AppDescriptorRegistry.
     */
    private AppDescriptorRegistry appDescriptorRegistry;

    /**
     * Reference to the injected SystemEventBus, used to handle events from the AppDescriptorRegistry events.
     */
    private SystemEventBus systemEventBus;

    @Inject
    public AppLayoutManagerImpl(AppDescriptorRegistry appDescriptorRegistry, SystemEventBus systemEventBus) {
        this.appDescriptorRegistry = appDescriptorRegistry;
        this.systemEventBus = systemEventBus;

        /**
         * Propagate events from {@link AppDescriptorRegistry} to notify listeners that the layout has changed.
         */
        systemEventBus.addHandler(AppRegistryEvent.class, new AppRegistryEventHandler() {

            @Override
            public void onAppRegistered(AppRegistryEvent event) {
                String name = event.getAppDescriptor().getName();
                logger.debug("Got AppLifecycleEvent." + event.getEventType() + " for the following appDescriptor " + name);
                sendEvent(new LayoutEvent(LayoutEventType.RELOAD_APP, name));
            }

            @Override
            public void onAppReregistered(AppRegistryEvent event) {
                String name = event.getAppDescriptor().getName();
                logger.debug("Got AppLifecycleEvent." + event.getEventType() + " for the following appDescriptor " + name);
                sendEvent(new LayoutEvent(LayoutEventType.RELOAD_APP, name));
            }

            @Override
            public void onAppUnregistered(AppRegistryEvent event) {
                String name = event.getAppDescriptor().getName();
                logger.debug("Got AppLifecycleEvent." + event.getEventType() + " for the following appDescriptor " + name);
                sendEvent(new LayoutEvent(LayoutEventType.RELOAD_APP, name));
            }
        });
    }

    @Override
    public AppLayout getLayout() {

        Map<String, AppCategory> categories = new HashMap<String, AppCategory>();
        AppLayout appLayout = new AppLayoutImpl(categories);

        Collection<AppDescriptor> appDescriptors = this.appDescriptorRegistry.getAppDescriptors();

        for (AppDescriptor app : appDescriptors) {
            if (hasToAddApp(app, appLayout)) {
                handleCategory(categories, app);
            }
        }
        return appLayout;
    }

    @Override
    public boolean isAppDescriptorRegistered(String name) {
        return true;
    }

    @Override
    public boolean isCategoryRegistered(String name) {
        return true;
    }

    /**
     * Filter out disabled apps and apps with identical names.
     */
    private boolean hasToAddApp(AppDescriptor app, AppLayout appLayout) {
        // Filter out disabled apps and apps with identical names
        if (!app.isEnabled() || appLayout.isAppAlreadyRegistered(app.getName()) || !isAppDescriptorRegistered(app.getName())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Add the AppDescriptor to an existing or newly created AppCategory.
     */
    private void handleCategory(Map<String, AppCategory> categories, AppDescriptor app) {
        AppCategory category;
        String catName = app.getCategoryName();
        logger.debug("Handle app " + app.getName() + " for category " + catName);
        if (categories.containsKey(catName)) {
            // Add to Category
            category = categories.get(catName);
        } else {
            // Create
            category = new AppCategory();
            category.setLabel(catName);
            categories.put(catName, category);
        }
        category.addApp(app);
    }

    /**
     * Send an event to the system event bus.
     */
    private void sendEvent(LayoutEvent event) {
        logger.debug("Sending LayoutEvent." + event.getEventType() + " to the system bus");
        systemEventBus.fireEvent(event);
    }
}
