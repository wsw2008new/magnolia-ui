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
package info.magnolia.ui.vaadin.integration.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import info.magnolia.context.MgnlContext;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.jcr.MockSession;

import java.math.BigDecimal;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PropertyType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property;

public class JcrNodeAdapterTypedPropertyTest {


    private String worksapceName = "workspace";
    private MockSession session;


    @Before
    public void setUp() {
        session = new MockSession(worksapceName);
        MockContext ctx = new MockContext();
        ctx.addSession(worksapceName, session);
        MgnlContext.setInstance(ctx);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }




    @Test
    public void testGetItemProperty_Untyped_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        String value = "test";
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, null, value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.STRING), property.getType().getSimpleName());
        assertEquals(value, property.getValue().toString());
    }

    @Test
    public void testGetItemProperty_String_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        String value = "test";
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "String", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.STRING), property.getType().getSimpleName());
        assertEquals(value, property.getValue().toString());
    }


    @Test
    public void testGetItemProperty_Long_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Long value = Long.decode("10000");
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "Long", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.LONG), property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGetItemProperty_Double_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Double value = Double.valueOf("10000.99");
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "Double", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.DOUBLE), property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGetItemProperty_Date_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Date value = new Date(11111111);
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "Date", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.DATE), property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGetItemProperty_Boolean_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Boolean value = Boolean.TRUE;
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "Boolean", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals(PropertyType.nameFromValue(PropertyType.BOOLEAN), property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGetItemProperty_Decimal_CheckInitialization() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        BigDecimal value = new BigDecimal("1111111");
        Node parentNode = session.getRootNode().addNode(nodeName);
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        //Create the property
        Property propertyInitial = DefaultPropertyUtil.newDefaultProperty(id, "Decimal", value);
        adapter.addItemProperty(id,propertyInitial);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertSame(property, propertyInitial);
        assertEquals("BigDecimal", property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }


    @Test
    public void testGetItemProperty_String_FromJcr() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Boolean value = Boolean.TRUE;
        Node parentNode = session.getRootNode().addNode(nodeName);
        //Create the JCR property
        parentNode.setProperty(id, value);
        session.save();

        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);

        // WHEN
        Property property = adapter.getItemProperty(id);

        // THEN
        assertEquals(PropertyType.nameFromValue(PropertyType.BOOLEAN), property.getType().getSimpleName());
        assertEquals(value, property.getValue());
    }

    @Test
    public void testGetItemProperty_String_FromJcrModifyVaadin() throws Exception {
        // GIVEN
        //Create a NewNodeAdapter
        String nodeName = "rootNode";
        String id = "propertyID";
        Boolean value = Boolean.TRUE;
        Node parentNode = session.getRootNode().addNode(nodeName);
        //Create the JCR property
        parentNode.setProperty(id, value);
        session.save();
        //Get the Jcr Property as a Vaadin props
        JcrNodeAdapter adapter = new JcrNodeAdapter(parentNode);
        Property property = adapter.getItemProperty(id);
        // Modify
        property.setValue(Boolean.FALSE);

        // WHEN
        Node res = adapter.getNode();

        // THEN
        assertEquals(PropertyType.nameFromValue(PropertyType.BOOLEAN), property.getType().getSimpleName());
        assertEquals(res.getProperty(id).getType(), PropertyType.BOOLEAN);
        assertEquals(res.getProperty(id).getBoolean(), property.getValue());

    }

}

