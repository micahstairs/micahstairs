/*
 * Metro-tiles:  CSS3 Windows Metro Tiles
 * Author:       Tim Holman (Modified by Micah Stairs)
 */

function Tile( element ){
	
	// Tile element	
	var tile = element;

	// Declare css for when the tile is in its idle state.
	var idleCss = "perspective( 800px ) rotateX( 0deg ) rotateY( 0deg ) translateZ( 0px )";

	var initialize = function() {

		// Set transform origin to the center of the element.
		tile.style.webkitTransformOrigin = "50% 50%";
		tile.style.MozTransformOrigin = "50% 50%";
		tile.style.msTransformOrigin = "50% 50%";
		tile.style.oTransformOrigin = "50% 50%";
		tile.style.transformOrigin = "50% 50%";

		// Make sure the parent preserves the 3d perspective
		tile.parentElement.style.webkitTransformStyle = "preserve-3d";
		tile.parentElement.style.MozTransformStyle = "preserve-3d";
		tile.parentElement.style.msTransformStyle = "preserve-3d";
		tile.parentElement.style.oTransformStyle = "preserve-3d";
		tile.parentElement.style.transformStyle = "preserve-3d";

		// Set element transform times
		tile.style.webkitTransition = "-webkit-transform 0.08s";
		tile.style.MozTransition = "-moz-transform 0.08s";
		tile.style.msTransition = "-ms-transform 0.08s";
		tile.style.oTransition = "-o-transform 0.08s";
		tile.style.transition = "transform 0.08s";

		// This gives an antialiased effect for transforms in firefox.
		tile.style.outline = "1px solid transparent";

		// Font smoothing for webkit.
		tile.style.webkitFontSmoothing = "antialiased";

		// Listen to mouse events for the tile.
		tile.addEventListener('mousedown', MouseDown, false);
		
	}

	var pushTile = function(x, y) {

		// Get the elements width and height.
		var width = tile.offsetWidth;
		var height = tile.offsetHeight;

		var translateString = "perspective( 800px ) ";
		
		//  Tilt based on position clicked
		var percentageVertical = y / height;
		var percentageHorizontal = x / width;
		var v = (percentageVertical - 0.5) * 2.0;
		var h = (percentageHorizontal - 0.5) * 2.0;
		var rotateY = h * 5.0;
		var rotateX = v * -5.0;
		var translateZ = - (1.0 - (Math.abs(v) * Math.abs(h))) * 10;
		translateString += "rotateX( " + rotateX + "deg ) rotateY( " + rotateY + "deg ) translateZ( " + translateZ + "px )";

		// Apply transformation to tile.
		tile.style.webkitTransform = translateString;
		tile.style.MozTransform = translateString;
		tile.style.msTransform = translateString;
		tile.style.oTransform = translateString;
		tile.style.transform = translateString;

		document.addEventListener('mouseup', MouseUp, false);    

	};
	
	var MouseDown = function(event) {

		// Non offsetX browsers
		var tilePosition = elementPosition(tile);
		var x = event.pageX - tilePosition.x;
		var y = event.pageY - tilePosition.y;
		
		pushTile(x, y);
		
	};
	

	var MouseUp = function(event) {

		// Set the element to its idle state
		tile.style.webkitTransform = idleCss;
		tile.style.MozTransform = idleCss;
		tile.style.msTransform = idleCss;
		tile.style.oTransform = idleCss;
		tile.style.transform = idleCss;

		document.removeEventListener('mouseup', MouseUp, false);
	};

	// Element position finding for non webkit browsers.
	// How will this perform on mobile?
	var getNumericStyleProperty = function(style, prop) {
    return parseInt(style.getPropertyValue(prop),10) ;
	}

	var elementPosition = function(e) {
		var x = 0, y = 0;
	    var inner = true ;
	    do {
	        x += e.offsetLeft;
	        y += e.offsetTop;
	        var style = getComputedStyle(e,null) ;
	        var borderTop = getNumericStyleProperty(style,"border-top-width") ;
	        var borderLeft = getNumericStyleProperty(style,"border-left-width") ;
	        y += borderTop ;
	        x += borderLeft ;
	        if (inner){
	          var paddingTop = getNumericStyleProperty(style,"padding-top") ;
	          var paddingLeft = getNumericStyleProperty(style,"padding-left") ;
	          y += paddingTop ;
	          x += paddingLeft ;
	        }
	        inner = false ;
	    } while (e = e.offsetParent);
	    return { x: x, y: y };
	}
	
	// Initialize the tile.
	initialize();
}

// Find all tile elements
var tileElements = document.getElementsByClassName('metro-tile');
var i;

// Apply tile functions 
for (i = 0; i < tileElements.length; i++) {

	Tile(tileElements[i]);

}