/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.ui.api.action;

import static org.junit.Assert.assertEquals;

import info.magnolia.i18nsystem.I18nable;
import info.magnolia.i18nsystem.LocaleProvider;
import info.magnolia.i18nsystem.TranslationService;
import info.magnolia.i18nsystem.proxytoys.ProxytoysI18nizer;
import info.magnolia.ui.api.action.testmodel.TestI18nAbleActionDefinition;
import info.magnolia.ui.api.action.testmodel.TestI18nAbleRootObject;
import info.magnolia.ui.api.action.testmodel.TestI18nAbleRootObjectWithGetIdMethod;
import info.magnolia.ui.api.action.testmodel.TestI18nAbleRootObjectWithGetNameMethod;
import info.magnolia.ui.api.action.testmodel.TestI18nAbleRootObjectWithoutKeyGenerator;
import info.magnolia.ui.api.app.SubAppDescriptor;
import info.magnolia.ui.api.app.registry.ConfiguredAppDescriptor;
import info.magnolia.ui.api.app.registry.ConfiguredSubAppDescriptor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for ActionDefinitionKeyGenerator.
 */
public class ActionDefinitionKeyGeneratorTest {

    private ProxytoysI18nizer i18nizer;
    private TestI18nAbleActionDefinition actionDefinition;
    private ConfiguredAppDescriptor appDescriptor;

    @Before
    public void setup() {
        i18nizer = new ProxytoysI18nizer(new TestTranslationService(), new FixedLocaleProvider(Locale.ITALIAN));

        actionDefinition = new TestI18nAbleActionDefinition();
        actionDefinition.setName("myaction");

        appDescriptor = new ConfiguredAppDescriptor();
        appDescriptor.setName("myapp");

        Map<String, SubAppDescriptor> subappDescriptors = new HashMap<String, SubAppDescriptor>();
        ConfiguredSubAppDescriptor subappDescriptor = new ConfiguredSubAppDescriptor();
        subappDescriptor.setName("browser");

        Map<String, ActionDefinition> actions = new HashMap<String, ActionDefinition>();
        actions.put("1", actionDefinition);

        subappDescriptor.setActions(actions);
        subappDescriptors.put("1", subappDescriptor);
        appDescriptor.setSubApps(subappDescriptors);
    }

    @Test
    public void keysForActionInChooseDialog() throws Exception {
        // GIVEN
        TestContentAppDescriptor contentApp = new TestContentAppDescriptor();
        contentApp.setName("contentApp");
        TestChooseDialogDefinition chooseDialog = new TestChooseDialogDefinition();
        TestI18nAbleActionDefinition chooseDialogAction = new TestI18nAbleActionDefinition();
        chooseDialogAction.setName("chooseDialogAction");
        // hierarchy
        chooseDialog.getActions().put("chooseDialogAction", chooseDialogAction);
        contentApp.setChooseDialog(chooseDialog);

        // WHEN
        contentApp = i18nizer.decorate(contentApp);

        // THEN
        assertEquals("i18n key is [contentApp.chooseDialog.actions.chooseDialogAction.label]", contentApp.getChooseDialog().getActions().get("chooseDialogAction").getLabel());
    }

    @Test
    public void keyGeneratedFromAppDescriptorIsCompliant() throws Exception {
        // WHEN
        appDescriptor = i18nizer.decorate(appDescriptor);

        // THEN
        assertEquals("i18n key is [myapp.browser.actions.myaction.label]", appDescriptor.getSubApps().get("1").getActions().get("1").getLabel());
    }

    @Test
    public void unknownI18nAbleParentPrependAncestorGeneratedKey() throws Exception {
        // GIVEN
        TestI18nAbleRootObject rootObj = new TestI18nAbleRootObject();
        rootObj.setActionDefinition(actionDefinition);

        // WHEN
        rootObj = i18nizer.decorate(rootObj);

        // THEN
        assertEquals("i18n key is [parent-keygen.actions.myaction.label]", rootObj.getActionDefinition().getLabel());
    }

    @Test
    public void unknownI18nAbleParentWithoutKeyGeneratorPrependsNothing() throws Exception {
        // GIVEN
        TestI18nAbleRootObjectWithoutKeyGenerator rootObj = new TestI18nAbleRootObjectWithoutKeyGenerator();
        rootObj.setActionDefinition(actionDefinition);

        // WHEN
        rootObj = i18nizer.decorate(rootObj);

        // THEN
        assertEquals("i18n key is [actions.myaction.label]", rootObj.getActionDefinition().getLabel());
    }

    @Test
    public void unknownI18nAbleParentWithoutKeyGeneratorButWithGetIdPrependsItsId() throws Exception {
        // GIVEN
        TestI18nAbleRootObjectWithGetIdMethod rootObj = new TestI18nAbleRootObjectWithGetIdMethod();
        rootObj.setId("foo");
        rootObj.setActionDefinition(actionDefinition);

        // WHEN
        rootObj = i18nizer.decorate(rootObj);

        // THEN
        assertEquals("i18n key is [foo.actions.myaction.label]", rootObj.getActionDefinition().getLabel());
    }

    @Test
    public void colonInIdIsReplacedByDot() throws Exception {
        // GIVEN
        TestI18nAbleRootObjectWithGetIdMethod rootObj = new TestI18nAbleRootObjectWithGetIdMethod();
        rootObj.setId("foo:bar");
        rootObj.setActionDefinition(actionDefinition);

        // WHEN
        rootObj = i18nizer.decorate(rootObj);

        // THEN
        assertEquals("i18n key is [foo.bar.actions.myaction.label]", rootObj.getActionDefinition().getLabel());
    }

    @Test
    public void unknownI18nAbleParentWithoutKeyGeneratorButWithGetNamePrependsItsName() throws Exception {
        // GIVEN
        TestI18nAbleRootObjectWithGetNameMethod rootObj = new TestI18nAbleRootObjectWithGetNameMethod();
        rootObj.setName("baz:qux");
        rootObj.setActionDefinition(actionDefinition);

        // WHEN
        rootObj = i18nizer.decorate(rootObj);

        // THEN
        assertEquals("i18n key is [baz.qux.actions.myaction.label]", rootObj.getActionDefinition().getLabel());
    }

    /**
     * TestTranslationService.
     */
    public static class TestTranslationService implements TranslationService {
        @Override
        public String translate(LocaleProvider localeProvider, String basename, String[] keys) {
            return "i18n key is [" + keys[0] + "]";
        }
    }

    /**
     * FixedLocaleProvider.
     */
    public static class FixedLocaleProvider implements LocaleProvider {
        private final Locale locale;

        public FixedLocaleProvider(Locale locale) {
            this.locale = locale;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }
    }

    /**
     * Fake ContentAppDescriptor - cannot use the right one here, as it is defined in a dependent artifact.
     */
    public static class TestContentAppDescriptor extends ConfiguredAppDescriptor {
        private TestChooseDialogDefinition chooseDialog;

        public TestChooseDialogDefinition getChooseDialog() {
            return chooseDialog;
        }

        public void setChooseDialog(TestChooseDialogDefinition chooseDialog) {
            this.chooseDialog = chooseDialog;
        }
    }

    /**
     * Fake ChooseDialogDefinition - cannot use the right one here, as it is defined in a dependent artifact.
     */
    @I18nable
    public static class TestChooseDialogDefinition {
        private Map<String, ActionDefinition> actions = new HashMap<String, ActionDefinition>();

        public Map<String, ActionDefinition> getActions() {
            return this.actions;
        }
    }
}
