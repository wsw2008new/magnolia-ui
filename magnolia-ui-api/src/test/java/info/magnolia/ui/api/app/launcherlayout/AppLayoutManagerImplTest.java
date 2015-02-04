/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
package info.magnolia.ui.api.app.launcherlayout;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.cms.security.MgnlUser;
import info.magnolia.cms.security.operations.ConfiguredAccessDefinition;
import info.magnolia.config.registry.DefinitionMetadata;
import info.magnolia.config.registry.DefinitionMetadataBuilder;
import info.magnolia.config.registry.DefinitionProvider;
import info.magnolia.config.registry.DefinitionQuery;
import info.magnolia.config.registry.DefinitionRawView;
import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.event.SimpleEventBus;
import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.test.MgnlTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.ui.api.app.AppDescriptor;
import info.magnolia.ui.api.app.registry.AppDescriptorRegistry;
import info.magnolia.ui.api.app.registry.AppRegistryEvent;
import info.magnolia.ui.api.app.registry.AppRegistryEventType;
import info.magnolia.ui.api.app.registry.ConfiguredAppDescriptor;
import info.magnolia.ui.api.app.registry.DefinitionTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link info.magnolia.ui.api.app.launcherlayout.AppLauncherLayoutManagerImpl}.
 */
public class AppLayoutManagerImplTest extends MgnlTestCase {

    private AppLauncherGroupDefinition appGroup1;
    private AppLauncherGroupDefinition appGroup2;
    private ConfiguredAppDescriptor appDescriptor1;
    private ConfiguredAppDescriptor appDescriptor2;
    private ConfiguredAppDescriptor appDescriptor3;
    private EventBus systemEventBus;
    private AppLauncherLayoutManagerImpl appLayoutManager;

    private I18nizer i18nizer = new I18nizer() {
        @Override
        public <C> C decorate(C child) {
            return child;
        }
    };

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Init
        appDescriptor1 = AppLauncherLayoutTest.createAppDescriptor("app1");
        appDescriptor2 = AppLauncherLayoutTest.createAppDescriptor("app2");
        appDescriptor3 = AppLauncherLayoutTest.createAppDescriptor("app3");

        appGroup1 = createAppGroup("appGroup1", "app1", "app2");
        appGroup2 = createAppGroup("appGroup2", "app3");

        ConfiguredAppLauncherLayoutDefinition layoutDefinition = new ConfiguredAppLauncherLayoutDefinition();
        layoutDefinition.addGroup(appGroup1);
        layoutDefinition.addGroup(appGroup2);

        Map<String, AppDescriptor> descriptors = new HashMap<>();
        descriptors.put("app1", appDescriptor1);
        descriptors.put("app2", appDescriptor2);
        descriptors.put("app3", appDescriptor3);

        AppDescriptorRegistry registry = mock(AppDescriptorRegistry.class);
        when(registry.getAllDefinitions()).thenReturn(new LinkedList<>(descriptors.values()));

        DefinitionQuery namedQuery = mock(DefinitionQuery.class);
        final Iterator<Map.Entry<String, AppDescriptor>> it = descriptors.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, AppDescriptor> entry = it.next();
            DefinitionQuery findsConcreteAppQuery = mock(DefinitionQuery.class);
            String appName = entry.getKey();
            AppDescriptor appDescriptor = entry.getValue();
            doReturn(findsConcreteAppQuery).when(namedQuery).named(appName);
            doReturn(new DummyAppDescriptorProvider(appName, "module", "/apps", appDescriptor)).when(findsConcreteAppQuery).findSingle();
        }

        doReturn(namedQuery).when(registry).query();

        systemEventBus = new SimpleEventBus();

        appLayoutManager = new AppLauncherLayoutManagerImpl(registry, systemEventBus, i18nizer);
        appLayoutManager.setLayout(layoutDefinition);

        // Set up context with a user having only one role
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("testRole");

        MgnlUser user = new MgnlUser("testUser", null, new ArrayList<String>(), roles, new HashMap<String, String>()) {

            // Overridden to avoid querying the group manager in test
            @Override
            public Collection<String> getAllRoles() {
                return super.getRoles();
            }
        };

        MockWebContext context = (MockWebContext) MgnlContext.getInstance();
        context.setUser(user);
    }

    @Test
    public void testGetAppLayout() {

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertEquals(2, layout.getGroups().size());
        assertEquals(2, layout.getGroups().get(0).getApps().size());
        assertEquals(1, layout.getGroups().get(1).getApps().size());
    }

    @Test
    public void testSendsLauncherLayoutChangedEvent() {

        final ArrayList<AppLauncherLayoutChangedEvent> events = new ArrayList<AppLauncherLayoutChangedEvent>();
        systemEventBus.addHandler(AppLauncherLayoutChangedEvent.class, new AppLauncherLayoutChangedEventHandler() {

            @Override
            public void onAppLayoutChanged(AppLauncherLayoutChangedEvent event) {
                events.add(event);
            }
        });

        // WHEN
        systemEventBus.fireEvent(new AppRegistryEvent(appDescriptor1, AppRegistryEventType.REGISTERED));
        systemEventBus.fireEvent(new AppRegistryEvent(appDescriptor2, AppRegistryEventType.REREGISTERED));
        systemEventBus.fireEvent(new AppRegistryEvent(appDescriptor3, AppRegistryEventType.UNREGISTERED));

        // THEN
        assertEquals(3, events.size());
    }

    @Test
    public void testHidesAppsThatAreNotAccessibleForTheCurrentUser() {

        // GIVEN
        ConfiguredAccessDefinition permissions = new ConfiguredAccessDefinition();
        permissions.addRole("roleThatUserDoesNotHave");
        appDescriptor1.setPermissions(permissions);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertFalse(layout.containsApp("app1"));
        assertEquals(2, layout.getGroups().size());
        assertEquals(1, layout.getGroups().get(0).getApps().size());
        assertEquals(1, layout.getGroups().get(1).getApps().size());
    }

    @Test
    public void testIncludesAppsThatAreAccessibleForTheCurrentUser() {

        // GIVEN
        ConfiguredAccessDefinition permissions = new ConfiguredAccessDefinition();
        permissions.addRole("testRole");
        appDescriptor1.setPermissions(permissions);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertTrue(layout.containsApp("app1"));
        assertEquals(2, layout.getGroups().size());
        assertEquals(2, layout.getGroups().get(0).getApps().size());
        assertEquals(1, layout.getGroups().get(1).getApps().size());
    }

    @Test
    public void testHidesAppsThatAreNotEnabled() {

        // GIVEN
        appDescriptor1.setEnabled(false);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertFalse(layout.containsApp("app1"));
        assertEquals(2, layout.getGroups().size());
        assertEquals(1, layout.getGroups().get(0).getApps().size());
        assertEquals(1, layout.getGroups().get(1).getApps().size());
    }

    @Test
    public void testHidesEntriesThatAreNotEnabled() {

        // GIVEN
        ((ConfiguredAppLauncherGroupEntryDefinition) this.appGroup1.getApps().get(0)).setEnabled(false);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertFalse(layout.containsApp("app1"));
        assertEquals(2, layout.getGroups().size());
        assertEquals(1, layout.getGroups().get(0).getApps().size());
        assertEquals(1, layout.getGroups().get(1).getApps().size());
    }

    @Test
    public void testExcludesEmptyGroups() {

        // GIVEN
        appDescriptor3.setEnabled(false);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertEquals(1, layout.getGroups().size());
        assertEquals(2, layout.getGroups().get(0).getApps().size());
    }

    @Test
    public void testExcludesAppWithoutLabel() {

        // GIVEN
        appDescriptor1.setLabel(null);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertEquals(1, layout.getGroups().get(0).getApps().size());
        assertFalse(layout.getGroups().get(0).getApps().get(0).getName().equals("app1"));
    }

    @Test
    public void testExcludesAppWithoutIcon() {

        // GIVEN
        appDescriptor1.setIcon(null);

        // WHEN
        AppLauncherLayout layout = appLayoutManager.getLayoutForCurrentUser();

        // THEN
        assertEquals(1, layout.getGroups().get(0).getApps().size());
        assertFalse(layout.getGroups().get(0).getApps().get(0).getName().equals("app1"));
    }

    public static AppLauncherGroupDefinition createAppGroup(String name, String... appNames) {
        ConfiguredAppLauncherGroupDefinition group = new ConfiguredAppLauncherGroupDefinition();
        group.setName(name);
        for (String appName : appNames) {
            ConfiguredAppLauncherGroupEntryDefinition entry = new ConfiguredAppLauncherGroupEntryDefinition();
            entry.setName(appName);
            entry.setEnabled(true);
            group.addApp(entry);
        }
        return group;
    }

    class DummyAppDescriptorProvider implements DefinitionProvider<AppDescriptor> {

        private final DefinitionMetadata metadata;
        private final AppDescriptor appDescriptor;
        private final boolean valid;

        public DummyAppDescriptorProvider(String appName, String moduleName, String relativeLocation, AppDescriptor appDescriptor) {
            this(appName, moduleName, relativeLocation, appDescriptor, true);
        }

        public DummyAppDescriptorProvider(String appName, String moduleName, String relativeLocation, AppDescriptor appDescriptor, boolean valid) {
            this.metadata = DefinitionMetadataBuilder.newBuilder().type(DefinitionTypes.APP).name(appName).module(moduleName).relativeLocation(relativeLocation).build();
            this.appDescriptor = appDescriptor;
            this.valid = valid;
        }

        @Override
        public DefinitionMetadata getMetadata() {
            return metadata;
        }

        @Override
        public AppDescriptor get() {
            return appDescriptor;
        }

        @Override
        public DefinitionRawView getRaw() {
            throw new IllegalStateException("not implemented yet"); // TODO
        }

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public List<String> getErrorMessages() {
            throw new IllegalStateException("not implemented yet"); // TODO
        }
    }
}
