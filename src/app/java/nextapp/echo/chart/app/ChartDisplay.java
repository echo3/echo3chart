package nextapp.echo.chart.app;

/* 
 * This file is part of the Echo3 Chart Library.
 * Copyright (C) 2008 NextApp, Inc.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

import java.util.EventListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.Plot;

import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

/**
 * A component which displays a <code>JFreeChart</code>.
 * @author sgodden
 */
public class ChartDisplay extends Component {
	
    public static final String INPUT_ACTION = "action";
    
    public static final String ACTION_LISTENERS_CHANGED_PROPERTY = "actionListeners";
    public static final String ACTION_COMMAND_PROPERTY = "actionCommand";
    public static final String CHART_CHANGED_PROPERTY = "chart";
    public static final String CHART_CONTENT_CHANGED_PROPERTY = "chartContent";
    public static final String PROPERTY_HEIGHT = "height";
    public static final String PROPERTY_WIDTH = "width";
    
    /**
     * The array of action commands, in the same
     * sequence as the series added to the chart.
     */
    private String[] actionCommands;
    
    /**
     * The action command that the client has just triggered.
     */
    private String triggeredActionCommand;
    
    /**
     * The displayed <code>JFreeChart</code>.
     */
    private JFreeChart chart;
    
    /**
     * <code>ChartChangeListener</code> to monitor the <code>JFreeChart</code>
     * for changes such that the display will be updated to reflect them.
     */
    private ChartChangeListener chartChangeListener = new ChartChangeListener() {
    
        /**
         * @see org.jfree.chart.event.ChartChangeListener#chartChanged(org.jfree.chart.event.ChartChangeEvent)
         */
        public void chartChanged(ChartChangeEvent e) {
            firePropertyChange(CHART_CONTENT_CHANGED_PROPERTY, null, null);
        }
    };
    
    /**
     * Creates a new empty <code>ChartDisplay</code> that is initially not
     * displaying a <code>JFreeChart</code>.
     */
    public ChartDisplay() {
        super();
    }
    
    /**
     * Creates a new <code>ChartDisplay</code> component that is initially displaying
     * a specific <code>JFreeChart</code>, with a default width and height of 400 pixels.
     */
    public ChartDisplay(JFreeChart chart) {
        super();
        setWidth(new Extent(400));
        setHeight(new Extent(400));
        setChart(chart);
    }

    /**
     * Creates a new <code>ChartDisplay</code> component that is initially displaying
     * a new <code>JFreeChart</code> displaying the specified <code>Plot</code>.
     */
    public ChartDisplay(Plot plot) {
        this(new JFreeChart(plot));
    }
    
    /**
     * Adds an <code>ActionListener</code> to receive notification of user
     * actions clicks on a particular chart series.
     * 
     * @param l the listener to add
     */
    public void addActionListener(ActionListener l) {
        getEventListenerList().addListener(ActionListener.class, l);
        firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, null, l);
    }

    /**
     * Notifies all listeners that have registered for this event type.
     * 
     * @param e the <code>ActionEvent</code> to send
     */
    public void fireActionPerformed(ActionEvent e) {
        if (!hasEventListenerList()) {
            return;
        }
        EventListener[] listeners = getEventListenerList().getListeners(ActionListener.class);
        for (int index = 0; index < listeners.length; ++index) {
            ((ActionListener) listeners[index]).actionPerformed(e);
        }
    }
    
    /**
     * Returns the action commands relating to the
     * series in the chart.
     * @return the action commands.
     * @see #setActionCommands(String[])
     */
    public String[] getActionCommands() {
    	return actionCommands;
    }
    
    /**
     * Returns the displayed <code>JFreeChart</code>.
     * 
     * @return the displayed <code>JFreeChart</code> 
     */
    public JFreeChart getChart() {
        return chart;
    }
    
    /**
     * Returns the height of the chart.
     * Units must be in pixels values.
     * 
     * @return the height
     */
    public Extent getHeight() {
        return (Extent) get(PROPERTY_HEIGHT);
    }
    
    /**
     * Returns the width of the chart.
     * Units must be in pixels values.
     * 
     * @return the width
     */
    public Extent getWidth() {
        return (Extent) get(PROPERTY_WIDTH);
    }

    /**
     * Determines if the button has any <code>ActionListener</code>s 
     * registered.
     * 
     * @return true if any action listeners are registered
     */
    public boolean hasActionListeners() {
        return hasEventListenerList() && getEventListenerList().getListenerCount(ActionListener.class) != 0;
    }

    /**
     * @see nextapp.echo.app.Component#processInput(java.lang.String, java.lang.Object)
     */
    public void processInput(String name, Object value) {
        super.processInput(name, value);
        if (ACTION_COMMAND_PROPERTY.equals(name)) {
        	this.triggeredActionCommand = (String) value;
        }
        if (INPUT_ACTION.equals(name)) {
            fireActionPerformed(new ActionEvent(this, triggeredActionCommand));
        }
    }

    /**
     * Removes an <code>ActionListener</code>.
     * 
     * @param l the listener to remove
     */
    public void removeActionListener(ActionListener l) {
        if (!hasEventListenerList()) {
            return;
        }
        getEventListenerList().removeListener(ActionListener.class, l);
        // Notification of action listener changes is provided due to 
        // existence of hasActionListeners() method. 
        firePropertyChange(ACTION_LISTENERS_CHANGED_PROPERTY, l, null);
    }
    
    /**
     * Sets the action commands that will be fired in action events
     * when the user clicks on a series in the chart.
     * <p>
     * These commands must be specified in the same sequence as the
     * sequence in which you add series to the chart.
     * </p>
     * @param actionCommands the action commands.
     */
    public void setActionCommands(String[] actionCommands) {
    	this.actionCommands = actionCommands;
    }
   
    /**
     * Sets the displayed <code>JFreeChart</code>.
     * 
     * param newValue the new <code>JFreeChart</code> to display 
     */
    public void setChart(JFreeChart newValue) {
        JFreeChart oldValue = chart;
        chart = newValue;
        if (oldValue != null) {
            oldValue.removeChangeListener(chartChangeListener);
        }
        if (newValue != null) {
            newValue.addChangeListener(chartChangeListener);
        }
        firePropertyChange(CHART_CHANGED_PROPERTY, oldValue, newValue);
    }
    
    /**
     * Sets the height of the chart.
     * Units must be in pixels values.
     * 
     * @param newValue the new height
     */
    public void setHeight(Extent newValue) {
        set(PROPERTY_HEIGHT, newValue);
    }
    
    /**
     * Sets the width of the chart.
     * Units must be in pixels values.
     * 
     * @param newValue the new width
     */
    public void setWidth(Extent newValue) {
        set(PROPERTY_WIDTH, newValue);
    }
}
