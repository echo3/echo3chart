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

    componentType: "EchoChartDisplay"
});

/**
 * Component rendering peer: ChartDisplay
 */
Echo.Chart.Render.ChartDisplaySync = Core.extend(Echo.Render.ComponentSync, { 

    $load: function() {
        Echo.Render.registerPeer("EchoChartDisplay", this);
    },

	_containerElement: null,    
    _node: null,
    
    renderAdd: function(update, parentElement) {
    	this._containerElement = parentElement;
    	
        var width = this.component.render("width");
        var height = this.component.render("height");
        
        var img = document.createElement("img");
        this._node; = img;
        parentElement.appendChild(img);
        img.id = this.component.renderId;
        img.src = this.component.get("uri");
        img.style.width = width + "px";
        img.style.height = height + "px";
    },
    
    renderDispose: function(update) {
    },
    
    renderUpdate: function(update) {
        if (this._node) {
            this._node.parentNode.removeChild(this._node);
        }
        // Note: this.renderDispose() is not invoked (it does nothing).
        this.renderAdd(update, this._containerElement);
        return false; // Child elements not supported: safe to return false.
    }
});
