/**
 * This file Copyright (c) 2011 Magnolia International
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
package info.magnolia.ui.framework.activity;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.configuration.ComponentProviderConfiguration;
import info.magnolia.objectfactory.guice.GuiceComponentProvider;
import info.magnolia.objectfactory.guice.GuiceComponentProviderBuilder;
import info.magnolia.ui.framework.event.EventBus;
import info.magnolia.ui.framework.event.HandlerRegistration;
import info.magnolia.ui.framework.event.SimpleEventBus;
import info.magnolia.ui.framework.place.Place;
import info.magnolia.ui.framework.place.PlaceController;
import info.magnolia.ui.framework.place.PlaceHistoryHandler;
import info.magnolia.ui.framework.place.PlaceHistoryMapperImpl;
import info.magnolia.ui.framework.shell.Shell;
import info.magnolia.ui.framework.view.ViewPort;


/**
 * Builds an inner MVP container having its own {@link ActivityManager}, {@link PlaceController} and
 * {@link EventBus}. {@link PlaceChangeEvent} events are fired to the outer {@link PlaceController}
 * and vice versa.
 *
 * @param <A> the inner activity the container will delegate to.
 */
public abstract class AbstractMVPSubContainer<A extends Activity> extends AbstractActivity {

    private String id;

    private EventBus innerEventBus;

    private PlaceController innerPlaceController;

    private Shell shell;

    private PlaceHistoryHandler historyHandler;

    private Shell subShell;

    private HandlerRegistration historyReg;

    private A activity;

    private ComponentProvider componentProvider;
    private ComponentProvider parentComponentProvider;


    public AbstractMVPSubContainer(String id, Shell shell, ComponentProvider parentComponentProvider) {
        this.id = id;
        this.shell = shell;
        this.parentComponentProvider = parentComponentProvider;
    }

    @Override
    public void start(ViewPort viewPort, EventBus outerEventBus) {

        // configure the component provider
        ComponentProviderConfiguration configuration = configureComponentProvider();

        configuration.registerImplementation(EventBus.class, SimpleEventBus.class);
        // TODO use IoC with parameters instead? newInstance(SubShell.class, id)
        configuration.registerInstance(Shell.class, shell.createSubShell(id));
        configuration.registerImplementation(PlaceController.class, PlaceController.class);

//        this.componentProvider = ((HierarchicalComponentProvider)parentComponentProvider).createChild(configuration);

        this.componentProvider = new GuiceComponentProviderBuilder().withConfiguration(configuration).withParent((GuiceComponentProvider) parentComponentProvider).build();

        subShell = componentProvider.getComponent(Shell.class);
        innerEventBus = componentProvider.getComponent(EventBus.class);
        innerPlaceController = componentProvider.getComponent(PlaceController.class);

        activity = this.componentProvider.newInstance(getActivityClass(), getActivityParameters());
        activity.start(viewPort, innerEventBus);

        historyHandler = new PlaceHistoryHandler(new PlaceHistoryMapperImpl(getSupportedPlaces()), subShell);
        historyReg = historyHandler.register(innerPlaceController, innerEventBus, getDefaultPlace());

        historyHandler.handleCurrentHistory();
    }

    @Override
    public void onStop() {
        activity.onStop();
        historyReg.removeHandler();
        subShell.setFragment(null);
    }

    @Override
    public String mayStop() {
        return activity.mayStop();
    }

    public String getId() {
        return id;
    }

    protected abstract Class<A> getActivityClass();

    protected abstract Object[] getActivityParameters();

    /**
     * Prepare the IoC container.
     */
    protected abstract ComponentProviderConfiguration configureComponentProvider();

    protected abstract Class< ? extends Place>[] getSupportedPlaces();

    protected abstract Place getDefaultPlace();


}
