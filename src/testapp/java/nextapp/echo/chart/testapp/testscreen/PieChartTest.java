/* 
 * This file is part of the Echo2 Chart Library.
 * Copyright (C) 2005 NextApp, Inc.
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

package nextapp.echo.chart.testapp.testscreen;

import nextapp.echo.app.Extent;
import nextapp.echo.app.SplitPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.chart.app.ChartDisplay;
import nextapp.echo.chart.testapp.ButtonColumn;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Interactive Test Module for testing <code>ChartDisplay</code> component with pie charts.
 */
public class PieChartTest extends SplitPane {

    public PieChartTest() {
        super(SplitPane.ORIENTATION_HORIZONTAL, new Extent(250, Extent.PX));
        setStyleName("DefaultResizable");
        
        ButtonColumn controlsColumn = new ButtonColumn();
        controlsColumn.setStyleName("TestControlsColumn");
        add(controlsColumn);
        
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("Widgets", 500.2);
        values.addValue("Cubits", 216.0);
        values.addValue("Zonkits", 125.9);
        
        final DefaultPieDataset pieDataset = new DefaultPieDataset(new DefaultPieDataset(values));
        PiePlot piePlot = new PiePlot(pieDataset);
        
        final ChartDisplay chartDisplay = new ChartDisplay(piePlot);
        add(chartDisplay);
        
        controlsColumn.addButton("Set Width = 800px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(chartDisplay) {
                    chartDisplay.setWidth(new Extent(800));
                }
            }
        });
        
        controlsColumn.addButton("Set Width = null", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(chartDisplay) {
                    chartDisplay.setWidth(null);
                }
            }
        });
        
        controlsColumn.addButton("Set Height = 600px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(chartDisplay) {
                    chartDisplay.setHeight(new Extent(600));
                }
            }
        });
        
        controlsColumn.addButton("Set Height = null", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(chartDisplay) {
                    chartDisplay.setHeight(null);
                }
            }
        });
        
        controlsColumn.addButton("Update a Value", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(chartDisplay) {
                    pieDataset.setValue("Cubits", Math.random() * 500);
                }
            }
        });
    }
}
