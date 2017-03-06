
var JsTreeHelper = JsTreeHelper || {};

JsTreeHelper.data = {};
JsTreeHelper.type = {};

JsTreeHelper.data.getDecoratedData = function ( id, parentId, text, decorator ) {
	var data = { 'id' : id, 'parent' : parentId, 'text' : text };
	
	if (JsTreeHelper.config.defaults.attributes_css.li)
		data.li_attr = JsTreeHelper.config.defaults.attributes_css.li;
	if (JsTreeHelper.config.defaults.attributes_css.i)
		data.i_attr = JsTreeHelper.config.defaults.attributes_css.i;
	if (JsTreeHelper.config.defaults.attributes_css.a)
		data.a_attr = JsTreeHelper.config.defaults.attributes_css.a;
	
	if (decorator)
		decorator.decorate ( id, data);

	return data;
};

/**
 * Inheriting class must implement :
 *     createDataFromCustomInput : function ( input, decorator ) => []
 *         @param input, content to build tree
 *         @param decorator, a JsTreeHelper.Decorator
 *         @return [], a jsTree data object array
 */
JsTreeHelper.data.Builder = function(config) {
	var real_config = config || {};
	real_config = jQuery.extend ( false, config, JsTreeHelper.config.defaults );
	return {
		config : real_config,
		createData : function ( input ) {
			return this.createDataFromCustomInput ( input, this.config.decorator );
		}
	};
}

/**
 * for function createData
 *     @param input must be either a String either a Xml object
 */
JsTreeHelper.data.XmlBuilder = function(config) {
	var commonBuilder = new JsTreeHelper.data.Builder(config);
	var builder = {
		
		createDataFromCustomInput : function ( input, decorator ) {
			var data = [];
			var object = ('string' == typeof input) ? jQuery.parseXML(input).documentElement : input;
			var id = "//"+object.localName;
			data.push(JsTreeHelper.data.getDecoratedData( id, '#', object.localName, decorator ));
			this.addObjectContent (object, id, data, decorator );
			return data;
		},
		
		addAttributes : function ( object, parentPath, data, decorator ) {
			jQuery.each(object.attributes, function(i, attribute){
				var name = attribute.name;
				var value = attribute.value;
				var newData = JsTreeHelper.data.getDecoratedData( parentPath+"@"+name, parentPath, name+" = "+value, decorator );
				newData.type = 'xml.attribute';
				data.push(newData);
			});
		},
		addChildren : function ( object, parentPath, data, decorator ) {
			var children = jQuery(object).children();
			var path = parentPath;
			var possibleIndex = 0;
			var childIndex = 0;
			for ( ; childIndex < children.length ; childIndex ++ ) {
				
				var child = children[childIndex];	
				var name = child.localName;
				
				var hasSiblingWithSameName = false;
				var siblingWithSameNameCount = 0;
				var siblingIndex = 0;
				for ( ; siblingIndex < children.length ; siblingIndex ++ ) {
					if ( children[siblingIndex].localName == name ) {
						hasSiblingWithSameName = true;
						if (siblingIndex < childIndex) {
							siblingWithSameNameCount ++;
						} else {
							break;
						}
					}
				}

				var id = hasSiblingWithSameName ? name+"["+siblingWithSameNameCount+"]" : name;
				var path = parentPath+"/"+id;
				data.push(JsTreeHelper.data.getDecoratedData( path, parentPath, name, decorator ));
				this.addObjectContent (child, path, data, null );
			}
		},
		addTextContent : function ( text, parentPath, data, decorator ) {
			if ( text != "") {
				var newData = JsTreeHelper.data.getDecoratedData( parentPath+"/__text__", parentPath, text, decorator );
				newData.icon = 'xml-contentText';
				newData.state = "opened";
				ObjectUtil.safeAttributeAppend (newData , 'li_attr', "color:blue; font-style: italic; font-weight: bolder; width:0px;");
				data.push(newData);
			}
		},
		addObjectContent : function  ( object, parentPath, data, decorator ) {
			var path = parentPath+"/"+object.localName;
			
			this.addAttributes ( object, parentPath, data, decorator );
			
			var childrenCount = jQuery(object).children().length;
			
			if (childrenCount > 0) {
				this.addChildren ( object, parentPath, data, decorator );
			} else {
				this.addTextContent ( object.textContent, parentPath, data, decorator );
			}
		}
		
	};
	builder = jQuery.extend ( false, builder, commonBuilder );
	return builder;
};





JsTreeHelper.config = {};
JsTreeHelper.config.defaults = {};
JsTreeHelper.config.defaults.attributes_css = {
	li : "background-size: 100% 100%;",
	i : "background-size: 100% 100%;",
	a : "background-size: 100% 100%;"
};




JsTreeHelper.Decorator = function (config) {
	
	var real_config = config || {};

	function applyDecorationAttribute(source,destination,attribute) {
		if (source[attribute] != undefined) {
			destination[attribute] = source[attribute];
		}
	}
	
	return {
		
		ON_MATCH_RULE_BEHAVIOR : JsTreeHelper.Decorator_Constant.ON_MATCH_RULE_BEHAVIOR,
		
		decorations : real_config.decorations || [],
		
		addMatch : function ( criteria, onMatchRule, operation ) {
			if ( criteria == null ) 
				throw "Parameter 'criteria' must be non nul";
			if ( ! ( ('string' == typeof criteria) || (criteria instanceof RegExp) ) ) 
				throw "Parameter 'criteria' must be a regex or a string";
			if ( operation == null ) 
				throw "Parameter 'operation' must be non nul";
			if ( ! ( ('object' == typeof operation) || ('function' == typeof operation) ) )
				throw "Parameter 'operation' must be an object";
			this.decorations.push({ 'criteria' : criteria, 'onMatchRule' : onMatchRule, 'operation' : operation });
		},
		decorate : function ( path, data ) {
			if ( path == null )
				throw "Parameter 'path' must be non nul"
			if ( ! ('string' == typeof path) )
				throw "Parameter 'path' must be a string"
			
			var decorationsCount = this.decorations.length;
			var index = 0;
			while ( index < decorationsCount ) {
				var decoration = this.decorations[index];
				var matches = false

				if ( 'string' == typeof decoration.criteria) {
					if ( path == decoration.criteria ) {
						matches = true;
					}
				} else {
					if ( path.match(decoration.criteria) ) {
						matches = true;
					}
				}
				
				if ( matches ) {
					if ('object' == typeof decoration.operation) {
						applyDecorationAttribute(decoration.operation, data, "icon");
					} else if ('function' == typeof decoration.operation) {
						try {
							decoration.operation( path, data);
						} catch ( ex ) {
							console.warn("Error while decorating path '"+path+"' : "+ex);
						}
					}
					
					if ( decoration.onMatchRule == this.ON_MATCH_RULE_BEHAVIOR.STOP )
						break;
				}
				index ++;
			}

		}
	};
};
JsTreeHelper.Decorator_Constant = {
	ON_MATCH_RULE_BEHAVIOR : { 'STOP' : "stop", 'CONTINUE' : "continue" }
};
ObjectUtil.safeFreezeObject(JsTreeHelper.Decorator_Constant);

JsTreeHelper.type.getDefaultTypes = function ( rootImagesUrl ) {
	var types =  {
		'xml.attribute' : {
			'icon' : rootImagesUrl+'/object_dock.png'
		},
		'xml.contentText' : {
			'icon' : 'jstree-xml-contentText',
			'valid_children' : 'default'
		}
    };
	return types;
};


JsTreeHelper.init = function() {
}

JsTreeHelper.init();


