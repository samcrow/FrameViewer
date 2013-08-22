/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.samcrow.frameviewer.ui;

import javafx.beans.property.IntegerProperty;
import junit.framework.TestCase;

import static junit.framework.Assert.assertEquals;

/**
 *
 * @author samcrow
 */
public class TimeFieldTest extends TestCase {
    
    public TimeFieldTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseDuration1() throws Exception {
        
        System.out.println("parseDuration");
        String time = "00:05";
        int expResult = 5;
        int result = TimeField.parseDuration(time);
        assertEquals(expResult, result);
        
    }
    
    public void testParseDuration2() throws Exception {
        
        System.out.println("parseDuration");
        String time = "01:05";
        int expResult = 65;
        int result = TimeField.parseDuration(time);
        assertEquals(expResult, result);
        
    }

    
}
