/**
 * Namespace declarations.
 */
Echo.Chart = {};
Echo.Chart.Render = {};

/**
 * Component implementation for chart display.
 */
Echo.Chart.ChartDisplay = Core.extend(Echo.Component, {
	
    $load: function() {
        Echo.ComponentFactory.registerType("EchoChartDisplay", this);
        Echo.ComponentFactory.registerType("ECD", this);
    },
        
    /**
     * Programmatically performs a button action.
     */
    doAction: function() {
        this.fireEvent({type: "action", source: this});
    },

    componentType: "EchoChartDisplay"
});

/**
 * Component rendering peer: ChartDisplay
 */
Echo.Chart.Render.ChartDisplaySync = Core.extend(Echo.Render.ComponentSync, { 

    $load: function() {
        Echo.Render.registerPeer("EchoChartDisplay", this);
    },
    
    $static: {
    	handleClick: function(renderId, actionCommand) {
    		Echo.Chart.Render.ChartDisplaySync.instances.map[renderId].doHandleClick(actionCommand);
    	},
    	instances: new Core.Arrays.LargeMap()
    },

	_containerElement: null,
    _node: null,
    _mapNode: null,
    
    renderAdd: function(update, parentElement) {
    	Echo.Chart.Render.ChartDisplaySync.instances.map[this.component.renderId] = this;
    	this._containerElement = parentElement;
    	
        var width = this.component.render("width");
        var height = this.component.render("height");
        
        var img = document.createElement("img");
        this._node = img;
        parentElement.appendChild(img);
        img.id = this.component.renderId;
        img.src = this.component.get("uri");
        
        var map = eval(this.component.get("map"));
        if (map != null) {
        	img.useMap = "#" + this.component.renderId;
        	var mapE = document.createElement("map");
        	this._mapNode = mapE;
        	parentElement.appendChild(mapE);
        	mapE.name = this.component.renderId;
	        for (var i = 0; i < map.length; i++) {
	        	var entry = map[i];
	        	var areaE = document.createElement("area");
	        	mapE.appendChild(areaE);
	        	areaE.href = "javascript: Echo.Chart.Render.ChartDisplaySync.handleClick('" + this.component.renderId + "', '" + entry.actionCommand + "');";
	        	areaE.title = entry.toolTipText;
	        	areaE.shape = entry.shapeType;
	        	areaE.coords = entry.shapeCoords;
	        }
        }
        
    },
    
    doHandleClick: function(actionCommand) {
    	this.component.set("actionCommand", actionCommand);
    	this.component.doAction();
    },
    
    renderDispose: function(update) {
    	Echo.Chart.Render.ChartDisplaySync.instances.remove(this.component.renderId);
    },
    
    renderUpdate: function(update) {
        if (this._node) {
            this._node.parentNode.removeChild(this._node);
        }
        if (this._mapNode) {
            this._mapNode.parentNode.removeChild(this._mapNode);
        }
        // Note: this.renderDispose() is not invoked (it does nothing).
        this.renderAdd(update, this._containerElement);
        return false; // Child elements not supported: safe to return false.
    }
});
