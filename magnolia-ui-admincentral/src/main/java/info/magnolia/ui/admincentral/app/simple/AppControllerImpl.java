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
package info.magnolia.ui.admincentral.app.simple;

import info.magnolia.event.EventBus;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.configuration.ComponentProviderConfiguration;
import info.magnolia.objectfactory.configuration.ComponentProviderConfigurationBuilder;
import info.magnolia.objectfactory.configuration.InstanceConfiguration;
import info.magnolia.objectfactory.guice.GuiceComponentProvider;
import info.magnolia.objectfactory.guice.GuiceComponentProviderBuilder;
import info.magnolia.registry.RegistrationException;
import info.magnolia.ui.framework.app.App;
import info.magnolia.ui.framework.app.AppContext;
import info.magnolia.ui.framework.app.AppController;
import info.magnolia.ui.framework.app.AppDescriptor;
import info.magnolia.ui.framework.app.AppInstanceController;
import info.magnolia.ui.framework.app.AppLifecycleEvent;
import info.magnolia.ui.framework.app.AppLifecycleEventType;
import info.magnolia.ui.framework.app.registry.AppDescriptorRegistry;
import info.magnolia.ui.framework.event.AdminCentralEventBusConfigurer;
import info.magnolia.ui.framework.location.DefaultLocation;
import info.magnolia.ui.framework.location.Location;
import info.magnolia.ui.framework.location.LocationChangeRequestedEvent;
import info.magnolia.ui.framework.location.LocationChangedEvent;
import info.magnolia.ui.framework.location.LocationController;
import info.magnolia.ui.framework.message.Message;
import info.magnolia.ui.framework.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;
import info.magnolia.ui.framework.view.ViewPort;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link AppController}.
 *
 * The App controller that manages the lifecycle of running apps and raises callbacks to the app.
 * It provides methods to start, stop and focus already running {@link App}s.
 * Registers handlers to the following location change events triggered by the {@link LocationController}:
 * <ul>
 * <li>{@link LocationChangedEvent}</li>
 * <li>{@link LocationChangeRequestedEvent}</li>
 * </ul>
 *
 * @see LocationController
 * @see AppContext
 * @see App
 */
@Singleton
public class AppControllerImpl implements AppController, LocationChangedEvent.Handler, LocationChangeRequestedEvent.Handler {

    private static final Logger log = LoggerFactory.getLogger(AppControllerImpl.class);

    private final ModuleRegistry moduleRegistry;

    private final ComponentProvider componentProvider;

    private final AppDescriptorRegistry appDescriptorRegistry;

    private final LocationController locationController;

    private final EventBus eventBus;
    private MessagesManager messagesManager;

    private ViewPort viewPort;

    private final Map<String, AppInstanceController> runningApps = new HashMap<String, AppInstanceController>();

    private final LinkedList<AppInstanceController> appHistory = new LinkedList<AppInstanceController>();

    private AppInstanceController currentAppInstanceController;

    @Inject
    public AppControllerImpl(ModuleRegistry moduleRegistry, ComponentProvider componentProvider,
                             AppDescriptorRegistry appDescriptorRegistry, LocationController locationController, @Named(AdminCentralEventBusConfigurer.EVENT_BUS_NAME) EventBus admincentralEventBus, MessagesManager messagesManager) {
        this.moduleRegistry = moduleRegistry;
        this.componentProvider = componentProvider;
        this.appDescriptorRegistry = appDescriptorRegistry;
        this.locationController = locationController;
        this.eventBus = admincentralEventBus;
        this.messagesManager = messagesManager;

        admincentralEventBus.addHandler(LocationChangedEvent.class, this);
        admincentralEventBus.addHandler(LocationChangeRequestedEvent.class, this);
    }

    @Override
    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    /**
     * This method is called to create an instance of an app independent from the {@link LocationController} and the {@link AppController} handling.
     * It will not open in the {@link ViewPort} and will not register itself to the running apps.
     * This is e.g. used to pass the {@link App} into a dialog and obtain app-specific information from outside the app.
     *
     * @param appId of the {@link App} to instantiate.
     */
    @Override
    public App getAppWithoutStarting(String appId) {
        AppInstanceController appInstanceController = getAppInstance(appId);
        ComponentProvider appComponentProvider = createAppComponentProvider(appInstanceController.getAppDescriptor().getName(), appInstanceController);
        App app = appComponentProvider.newInstance(appInstanceController.getAppDescriptor().getAppClass());

        appInstanceController.setApp(app);
        return app;
    }

    /**
     * This method can be called to launch an {@link App} and then delegate it to the {@link LocationController}.
     * It should have the same effect as calling the {@link LocationController} directly.
     *
     * @param appId of the {@link App} to start.
     * @param location holds information about the subApp to use and the parameters.
     */
    @Override
    public App startIfNotAlreadyRunningThenFocus(String appId, Location location) {
        AppInstanceController appInstanceController = getAppInstance(appId);
        appInstanceController = doStartIfNotAlreadyRunning(appInstanceController, location);
        doFocus(appInstanceController);
        return appInstanceController.getApp();
    }

    /**
     * This method is called to launch an app independent from the {@link LocationController}.
     * It will not open in the {@link ViewPort}.
     * This is e.g. used to pass the {@link App} into a dialog and obtain app-specific information from outside the app.
     *
     * See MGNLUI-379.
     *
     * @param appId of the {@link App} to start.
     * @param location holds information about the subApp to use and the parameters.
     * @deprecated since introduction of {@link #getAppWithoutStarting(String appId) getAppWithoutStarting}
     */
    @Deprecated
    @Override
    public App startIfNotAlreadyRunning(String appId, Location location) {
        AppInstanceController appInstanceController = getAppInstance(appId);

        return doStartIfNotAlreadyRunning(appInstanceController, location).getApp();
    }

    @Override
    public void stopApp(String appId) {
        AppInstanceController appInstanceController = runningApps.get(appId);
        if (appInstanceController != null) {
            doStop(appInstanceController);
        }
    }

    @Override
    public void stopCurrentApp() {
        final AppInstanceController appInstanceController = appHistory.peekFirst();
        doStop(appInstanceController);
    }

    @Override
    public boolean isAppStarted(String appId) {
        return runningApps.containsKey(appId);
    }

    @Override
    public void focusCurrentApp() {
        doFocus(currentAppInstanceController);
    }

    @Override
    public App getCurrentApp() {
        return currentAppInstanceController.getApp();
    }

    /**
     * Returns the current location of the focused app. This can differ from the actual location of the admin central, e.g. when a shell app is open.
     * @see info.magnolia.ui.framework.location.LocationController#getWhere()
     */
    @Override
    public Location getCurrentAppLocation() {
        return currentAppInstanceController == null ? null : currentAppInstanceController.getCurrentLocation();
    }

    /**
     * Returns the current location of a running app instance or null, if it is not running. The App does not have to be focused.
     */
    @Override
    public Location getAppLocation(String appId) {
        AppInstanceController appInstanceController = runningApps.get(appId);
        return appInstanceController == null ? null : appInstanceController.getCurrentLocation();
    }

    /**
     * Delegates the starting of an {@link App} to the {@link AppContext}. In
     * case the app is already started, it will update its location.
     */
    private AppInstanceController doStartIfNotAlreadyRunning(AppInstanceController appInstanceController, Location location) {
        if (isAppStarted(appInstanceController.getAppDescriptor().getName())) {
            appInstanceController.onLocationUpdate(location);
            return appInstanceController;
        }

        runningApps.put(appInstanceController.getAppDescriptor().getName(), appInstanceController);
        appInstanceController.start(location);
        sendEvent(AppLifecycleEventType.STARTED, appInstanceController.getAppDescriptor());
        return appInstanceController;
    }

    /**
     * Focuses an already running {@link App} by passing it to the
     * {@link LocationController}.
     */
    private void doFocus(AppInstanceController appInstanceController) {
        locationController.goTo(appInstanceController.getCurrentLocation());
        appHistory.addFirst(appInstanceController);
        sendEvent(AppLifecycleEventType.FOCUSED, appInstanceController.getAppDescriptor());
    }

    private void doStop(AppInstanceController appInstanceController) {
        appInstanceController.stop();
        while (appHistory.remove(appInstanceController)) {
            ;
        }

        runningApps.remove(appInstanceController.getAppDescriptor().getName());
        if (currentAppInstanceController == appInstanceController) {
            currentAppInstanceController = null;
            viewPort.setView(null);
        }
        sendEvent(AppLifecycleEventType.STOPPED, appInstanceController.getAppDescriptor());
        if (!appHistory.isEmpty()) {
            doFocus(appHistory.peekFirst());
        }
    }

    private void sendEvent(AppLifecycleEventType appEventType, AppDescriptor appDescriptor) {
        eventBus.fireEvent(new AppLifecycleEvent(appDescriptor, appEventType));
    }

    /**
     * Takes care of {@link LocationChangedEvent}s by:
     * <ul>
     * <li>Obtaining the {@link AppDescriptor} associated with the {@link Location}.</li>
     * <li>Creating a new {@link AppContext} if not running, otherwise obtain it from the running apps.</li>
     * <li>Updating the {@Link Location} and redirecting in case of missing subAppId.</li>
     * <li>Starting the App.</li>
     * <li>Adding the {@link AppContext} to the appHistory.</li>
     * <li>Setting the viewPort and updating the current running app.</li>
     * </ul>
     */
    @Override
    public void onLocationChanged(LocationChangedEvent event) {
        Location newLocation = event.getNewLocation();

        if (!newLocation.getAppType().equals(Location.LOCATION_TYPE_APP)) {
            return;
        }

        AppDescriptor nextApp = getAppForLocation(newLocation);

        if (nextApp == null) {
            return;
        }

        if (currentAppInstanceController != null) {
            ((AppContext) currentAppInstanceController).exitFullScreenMode();
        }

        AppInstanceController nextAppContext = getAppInstance(nextApp.getName());

        // update location
        Location updateLocation = updateLocation(nextAppContext, newLocation);
        if (!updateLocation.equals(newLocation)) {
            locationController.goTo(updateLocation);
            return;
        }

        if (currentAppInstanceController != nextAppContext) {
            appHistory.addFirst(nextAppContext);
        }
        currentAppInstanceController = nextAppContext;
        nextAppContext = doStartIfNotAlreadyRunning(nextAppContext, newLocation);


        viewPort.setView(nextAppContext.getApp().getView());
        // focus on locationChanged?
        // focusCurrentApp();
    }

    /**
     * Updates the {@link Location} in case of missing subAppId:
     * <ul>
     * <li>If the app is running, it will fetch the current Location associated with the App.</li>
     * <li>Will fetch the configured default subAppId otherwise.</li>
     * </ul>
     */
    private Location updateLocation(AppInstanceController appInstanceController, Location location) {
        String appType = location.getAppType();
        String appId = location.getAppId();
        String subAppId = location.getSubAppId();
        String params = location.getParameter();

        if (StringUtils.isBlank(subAppId)) {

            if (isAppStarted(appId)) {
                AppInstanceController runningAppContext = runningApps.get(appId);
                subAppId = runningAppContext.getCurrentLocation().getSubAppId();
            } else if (StringUtils.isBlank(subAppId)) {
                subAppId = appInstanceController.getDefaultLocation().getSubAppId();

            }
        }

        return new DefaultLocation(appType, appId, subAppId, params);
    }


    private AppInstanceController getAppInstance(String appId) {
        if (isAppStarted(appId)) {
            return runningApps.get(appId);
        } else {
            AppDescriptor descriptor = getAppDescriptor(appId);
            if (descriptor == null) {
                return null;
            }

            AppInstanceController appInstanceController = componentProvider.newInstance(AppInstanceController.class, descriptor);
            createAppComponentProvider(descriptor.getName(), appInstanceController);
            return appInstanceController;
        }
    }

    @Override
    public void onLocationChangeRequested(LocationChangeRequestedEvent event) {
        if (currentAppInstanceController != null) {
            final String message = currentAppInstanceController.mayStop();
            if (message != null) {
                event.setWarning(message);
            }
        }
    }

    private AppDescriptor getAppForLocation(Location location) {
        return getAppDescriptor(location.getAppId());
    }

    private AppDescriptor getAppDescriptor(String name) throws RuntimeException {
        try {
            return appDescriptorRegistry.getAppDescriptor(name);
        } catch (RegistrationException e) {

            Message errorMessage = new Message();
            errorMessage.setType(MessageType.ERROR);
            errorMessage.setSubject("Error occurred when trying to read App Descriptor");
            errorMessage.setMessage("There is no app registered with name: " + name);

            messagesManager.sendLocalMessage(errorMessage);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a ComponentProvider dedicated for the app with the AdminCentral ComponentProvider as its parent. This
     * gives us the ability to inject the AppContext into App components. The components are read from module
     * descriptors using the convention "app-" + name of the app and merged with the components defined for all apps
     * with the id "app".
     */
    private ComponentProvider createAppComponentProvider(String name, AppInstanceController appInstanceController) {

        ComponentProviderConfigurationBuilder configurationBuilder = new ComponentProviderConfigurationBuilder();
        List<ModuleDefinition> moduleDefinitions = moduleRegistry.getModuleDefinitions();

        // Get components common to all apps
        ComponentProviderConfiguration configuration = configurationBuilder.getComponentsFromModules(APP_PREFIX, moduleDefinitions);

        // Get components for this specific app
        final String componentsId = APP_PREFIX + "-" + name;
        log.debug("Reading component configurations from module descriptors for " + componentsId);
        ComponentProviderConfiguration appComponents = configurationBuilder.getComponentsFromModules(componentsId, moduleDefinitions);

        configuration.combine(appComponents);

        // Add the AppContext instance into the component provider.
        configuration.addComponent(InstanceConfiguration.valueOf(AppContext.class, appInstanceController));

        log.debug("Creating component provider for app " + name);
        GuiceComponentProviderBuilder builder = new GuiceComponentProviderBuilder();
        builder.withConfiguration(configuration);
        builder.withParent((GuiceComponentProvider) componentProvider);
        ComponentProvider appComponentProvider = builder.build();

        appInstanceController.setAppComponentProvider(appComponentProvider);

        return appComponentProvider;
    }

}
