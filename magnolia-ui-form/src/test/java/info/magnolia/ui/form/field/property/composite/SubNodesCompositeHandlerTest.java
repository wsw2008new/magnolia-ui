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
package info.magnolia.ui.form.field.property.composite;

import static org.junit.Assert.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertiesImportExport;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockComponentProvider;
import info.magnolia.ui.form.field.definition.CompositeFieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.PropertysetItem;

/**
 * .
 */
public class SubNodesCompositeHandlerTest extends RepositoryTestCase {
    private Node rootNode;
    private final String fieldName = "fieldName";
    private List<String> fieldsName = Arrays.asList("field1", "field2", "field3");
    private CompositeFieldDefinition definition = new CompositeFieldDefinition();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Init parent Node
        String nodeProperties =
                "/parent.@type=mgnl:content\n" +
                        "/parent.propertyString=hello\n" +
                        "/parent/fieldName.@type=mgnl:content\n" +
                        "/parent/fieldName.field2=hello2\n";

        Session session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        new PropertiesImportExport().createNodes(session.getRootNode(), IOUtils.toInputStream(nodeProperties));
        session.save();
        definition.setName(fieldName);

        rootNode = session.getRootNode().getNode("parent");
    }

    @Test
    public void testWriteMultiProperty() throws RepositoryException {
        // GIVEN
        JcrNodeAdapter parent = new JcrNodeAdapter(rootNode);
        SubNodesCompositeHandler delegate = new SubNodesCompositeHandler(parent, definition, new MockComponentProvider(), fieldsName);
        PropertysetItem itemSet = new PropertysetItem();
        itemSet.addItemProperty("field1", new DefaultProperty<String>("hello1"));

        // WHEN
        delegate.writeToDataSourceItem(itemSet);
        parent.applyChanges();

        // THEN
        assertTrue(rootNode.hasNode("fieldName"));
        Node child = rootNode.getNode("fieldName");
        assertTrue(child.hasProperty("field1"));
        assertEquals("hello1", child.getProperty("field1").getString());
        assertTrue(child.hasProperty("field2"));
        assertEquals("hello2", child.getProperty("field2").getString());
    }

    @Test
    public void testWriteMultiPropertyChildCreation() throws RepositoryException {
        // GIVEN
        rootNode.getNode("fieldName").remove();
        rootNode.getSession().save();
        JcrNodeAdapter parent = new JcrNodeAdapter(rootNode);
        SubNodesCompositeHandler delegate = new SubNodesCompositeHandler(parent, definition, new MockComponentProvider(), fieldsName);
        PropertysetItem itemSet = new PropertysetItem();
        itemSet.addItemProperty("field1", new DefaultProperty<String>("hello1"));

        // WHEN
        delegate.writeToDataSourceItem(itemSet);
        parent.applyChanges();

        // THEN
        assertTrue(rootNode.hasNode("fieldName"));
        Node child = rootNode.getNode("fieldName");
        assertTrue(child.hasProperty("field1"));
        assertEquals("hello1", child.getProperty("field1").getString());
        assertFalse(child.hasProperty("field2"));
    }

    @Test
    public void testReadMultiProperty() throws RepositoryException {
        // GIVEN

        JcrNodeAdapter parent = new JcrNodeAdapter(rootNode);
        SubNodesCompositeHandler delegate = new SubNodesCompositeHandler(parent, definition, new MockComponentProvider(), fieldsName);

        // WHEN
        PropertysetItem res = delegate.readFromDataSourceItem();

        // THEN
        assertNotNull(res.getItemProperty("field2"));
        assertEquals("hello2", res.getItemProperty("field2").getValue());

    }

    @Test
    public void testReadWriteMultiProperty() throws RepositoryException {
        // GIVEN
        JcrNodeAdapter parent = new JcrNodeAdapter(rootNode);
        SubNodesCompositeHandler delegate = new SubNodesCompositeHandler(parent, definition, new MockComponentProvider(), fieldsName);

        // WHEN
        PropertysetItem itemSet = delegate.readFromDataSourceItem();
        itemSet.addItemProperty("field1", new DefaultProperty<String>("hello1"));
        itemSet.getItemProperty("field2").setValue(null);
        delegate.writeToDataSourceItem(itemSet);
        parent.applyChanges();

        // THEN
        assertTrue(rootNode.hasNode("fieldName"));
        Node child = rootNode.getNode("fieldName");
        assertTrue(child.hasProperty("field1"));
        assertEquals("hello1", child.getProperty("field1").getString());
        assertFalse(child.hasProperty("field2"));
    }

}
