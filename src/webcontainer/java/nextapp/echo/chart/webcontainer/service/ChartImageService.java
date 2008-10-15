package nextapp.echo.chart.webcontainer.service;

/* 
 * This file is part of the Echo3 Chart Library.
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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo.app.Extent;
import nextapp.echo.chart.app.ChartDisplay;
import nextapp.echo.chart.webcontainer.sync.component.ChartDisplayPeer;
import nextapp.echo.webcontainer.Connection;
import nextapp.echo.webcontainer.ContentType;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.UserInstance;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.util.PngEncoder;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;

/**
 * <code>Service</code> to render chart images.
 */
public class ChartImageService 
implements Service {
	/**
	 * The service id.
	 */
	private static final String SERVICE_ID = "Echo.RemoteClient.Chart.ChartImageService";
    /**
     * Parameter keys used in generating service URI.
     */
    private static final String[] URI_PARAMETER_KEYS = {"chartId", "v"};
    /**
     * A version property which is ignored on the server but which forces the browser to
     * reload the image.
     */
    private int version = 0;
    
    /**
     * Returns the appropriate URI to display a chart for a specific 
     * <code>ChartDisplay</code> component.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param chartDisplay the rendering <code>ChartDisplay</code>
     * @param version the version number of the chart 
     *        (incremented when chart is re-rendered due to an update)
     * @return the URI
     */
    public String createUri(UserInstance userInstance, ChartDisplay chartDisplay) {
        return userInstance.getServiceUri(INSTANCE, URI_PARAMETER_KEYS, new String[]{chartDisplay.getRenderId(), String.valueOf(++version)});
    }
    
    /**
     * Singleton instance.
     */
    private static final ChartImageService INSTANCE = new ChartImageService();
    
    static {
        WebContainerServlet.getServiceRegistry().add(INSTANCE);
    }
    
    /** Self-instantiated singleton. */
    private ChartImageService() { }
    
    public String getId() {
        return SERVICE_ID;
    }

    public int getVersion() {
        return DO_NOT_CACHE;
    }

    public void service(Connection conn) throws IOException {
        try {
            UserInstance userInstance = (UserInstance) conn.getUserInstance();
            if (userInstance == null) {
                serviceBadRequest(conn, "No container available.");
                return;
            }

            HttpServletRequest request = conn.getRequest();
            String chartId = request.getParameter("chartId");
            ChartDisplay chartDisplay = (ChartDisplay) userInstance.getApplicationInstance().getComponentByRenderId(chartId);
            synchronized (chartDisplay) {
                if (chartDisplay == null || !chartDisplay.isRenderVisible()) {
                    throw new IllegalArgumentException("Invalid chart id.");
                }
                
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
//                EntityCollection coll = info.getEntityCollection();
//                debug("About to show entities");
//                for (int i = 0; i < coll.getEntityCount(); i++) {
//                	debug("Entity: " + coll.getEntity(i).getShapeCoords());
//                	debug("Entity: " + coll.getEntity(i).getShapeType());
//                	debug("Entity: " + coll.getEntity(i).getToolTipText());
//                }
                
                PngEncoder encoder = new PngEncoder(image, true, null, 3);
                conn.setContentType(ContentType.IMAGE_PNG);
                OutputStream out = conn.getOutputStream();
                encoder.encode(out);
            }
        } catch (IOException ex) {
            // Internet Explorer appears to enjoy making half-hearted requests for images, wherein it resets the connection
            // leaving us with an IOException.  This exception is silently eaten.
            ex.printStackTrace();
        }
    }
    
    private void debug(String s) {
    	System.out.println(s);
    }

	/**
	 * Sets the response status indicating that a bad request
	 * was made to this service.
	 * @param conn the connection.
	 * @param message the error message.
	 */
	private void serviceBadRequest(Connection conn, String message) {
		conn.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
		conn.setContentType(ContentType.TEXT_PLAIN);
		conn.getWriter().write(message);
	}
	
	/**
	 * Returns an instance for public use.
	 * @return an instance for public use.
	 */
	public static ChartImageService getInstance() {
		return INSTANCE;
	}

}
