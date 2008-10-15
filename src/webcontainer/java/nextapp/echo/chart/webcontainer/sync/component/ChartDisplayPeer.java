/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
package nextapp.echo.chart.webcontainer.sync.component;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import nextapp.echo.app.Component;
import nextapp.echo.app.Extent;
import nextapp.echo.app.text.TextComponent;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.update.ServerComponentUpdate;
import nextapp.echo.app.util.Context;
import nextapp.echo.chart.app.ChartDisplay;
import nextapp.echo.chart.webcontainer.service.ChartImageService;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;
import nextapp.echo.webcontainer.util.ArrayIterator;
import nextapp.echo.webcontainer.util.MultiIterator;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;

public class ChartDisplayPeer
        extends AbstractComponentSynchronizePeer {

	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 200;
	
    protected static final Service CHART_DISPLAY_SERVICE = JavaScriptService.forResource("Echo.Chart.ChartDisplay",
            "nextapp/echo/chart/webcontainer/resource/js/ChartDisplay.js");
    
    private static final String PROPERTY_URI = "uri";
    private static final String PROPERTY_IMAGE_MAP = "map";


    static {
        WebContainerServlet.getServiceRegistry().add(CHART_DISPLAY_SERVICE);
    }

    public ChartDisplayPeer() {
        super();
        addOutputProperty(PROPERTY_URI);
        addOutputProperty(PROPERTY_IMAGE_MAP);
        
        addEvent(new AbstractComponentSynchronizePeer.EventPeer("action", ChartDisplay.ACTION_LISTENERS_CHANGED_PROPERTY) {
            public boolean hasListeners(Context context, Component component) {
                return ((ChartDisplay) component).hasActionListeners();
            }
        });

    }

    public Class getComponentClass() {
        return ChartDisplay.class;
    }

    public String getClientComponentType(boolean shortType) {
        return shortType ? "ECD" : "EchoChartDisplay";
    }
    
    public Object getOutputProperty(Context context, Component component, String propertyName, int propertyIndex) {
        Object ret = null;
        
        ChartDisplay chartDisplay = (ChartDisplay) component;
        
        if (propertyName.equals(PROPERTY_URI)) {
            return ChartImageService.getInstance().createUri(WebContainerServlet.getActiveConnection().getUserInstance(), chartDisplay);
        }
        else if (propertyName.equals(PROPERTY_IMAGE_MAP)) {
        	return getImageMap(component);
        }
        else {
            ret = super.getOutputProperty(context, component, propertyName, propertyIndex);
        }
        
        return ret;
    }
    
    private String getImageMap(Component comp) {
    	StringBuffer sb = new StringBuffer();

    	ChartDisplay chartDisplay = (ChartDisplay) comp;
        
        Extent ewidth = (Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_WIDTH); 
        int width = ewidth != null ? ewidth.getValue() : ChartDisplayPeer.DEFAULT_WIDTH;
        
        Extent eheight = (Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_HEIGHT); 
        int height = ewidth != null ? eheight.getValue() : ChartDisplayPeer.DEFAULT_HEIGHT;
        
        JFreeChart chart =  chartDisplay.getChart();
        BufferedImage image;
        ChartRenderingInfo info = new ChartRenderingInfo();
        synchronized (chart) {
            image = chart.createBufferedImage(width, height, info);
        }
        EntityCollection coll = info.getEntityCollection();
//        debug("About to show entities");
//        for (int i = 0; i < coll.getEntityCount(); i++) {
//        	debug("Entity: " + coll.getEntity(i).getShapeCoords());
//        	debug("Entity: " + coll.getEntity(i).getShapeType());
//        	debug("Entity: " + coll.getEntity(i).getToolTipText());
//        }
        sb.append("[");
        for (int i = 0; i < coll.getEntityCount(); i++) {
        	if (i > 0) {
        		sb.append(", ");
        	}
        	sb.append("{ shapeType: '" + coll.getEntity(i).getShapeType() + "'");
        	sb.append(", shapeCoords: '" + coll.getEntity(i).getShapeCoords() + "'");
        	sb.append(", actionCommand: '" + chartDisplay.getActionCommands()[i] + "'");
        	sb.append(", toolTipText: '" + coll.getEntity(i).getToolTipText() + "'}");
        	
        }
        sb.append("]");
    	
    	return sb.toString();
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getInputPropertyClass(java.lang.String)
     */
    public Class getInputPropertyClass(String propertyName) {
        if (ChartDisplay.ACTION_COMMAND_PROPERTY.equals(propertyName)) {
            return String.class;
        }
        return null;
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getUpdatedOutputPropertyNames(
     *      nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component,
     *      nextapp.echo.app.update.ServerComponentUpdate)
     */
    public Iterator getUpdatedOutputPropertyNames(Context context, Component component, 
            ServerComponentUpdate update) {
        Iterator normalPropertyIterator = super.getUpdatedOutputPropertyNames(context, component, update);
        
        if (update.hasUpdatedProperty(ChartDisplay.PROPERTY_HEIGHT)
        		|| update.hasUpdatedProperty(ChartDisplay.PROPERTY_WIDTH)) {
            return new MultiIterator(
                    new Iterator[]{ normalPropertyIterator, new ArrayIterator(new String[]{PROPERTY_URI, PROPERTY_IMAGE_MAP}) });
        } else {
            return normalPropertyIterator;
        }
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(Context)
     */
    public void init(Context context, Component c) {
        super.init(context, c);
      ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
      serverMessage.addLibrary(CHART_DISPLAY_SERVICE.getId());
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#storeInputProperty(Context, Component, String, int, Object)
     */
    public void storeInputProperty(Context context, Component component, String propertyName, int propertyIndex, Object newValue) {
        if (propertyName.equals(ChartDisplay.ACTION_COMMAND_PROPERTY)) {
            ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
            clientUpdateManager.setComponentProperty(component, ChartDisplay.ACTION_COMMAND_PROPERTY, newValue);
        }
    }
}
