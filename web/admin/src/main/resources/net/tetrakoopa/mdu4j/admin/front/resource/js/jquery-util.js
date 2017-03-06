
/**
 * Utilitaires pour JQuery
 *
 * JQueryUtil.bindOnChange ( element, callback )
 * 		Permet de lier tout changement d'un champ input ( saisie clavier, collage, programmatiquement, ctrl+Z,... ) à une callback
 * 		@param element : String(jquery selector), domElement or jQueryObject
 * 		@param callback : function(event, values),
 * 			@param event : same event usued in JQuery.bind() callback
 * 			@param values : object with 'original' ( when binding was set ), 'previous' and 'neew' ( i.e. new )
 */

var JQueryUtil = JQueryUtil || {};

JQueryUtil.bindOnChange = function ( element, callback ) {
    var thisJQueryUtil = this;
    var jqElement = jQuery(element);
    var value = jqElement.val();
    jqElement.data('prevVal', value);
    jqElement.data('origVal', value);
    jqElement.bind("propertychange change click keyup input paste", function(event) {
        var value = jqElement.val();
        var prevValue = jqElement.data('prevVal');
        if (prevValue != value) {
            callback(event, thisJQueryUtil.getInputValues(jqElement));
            jqElement.data('prevVal', value);
        }
    });

}

/**
 * @return object containing values for this element :
 * 		original : linked data field named 'origVal' ( which is value this element had when is was binded  )
 * 		previous : linked data field named 'prevVal' ( which is the previous value of this element )
 * 		current : the current value
 * 		neew : 'neew' is an alias for 'current'
 */
JQueryUtil.getInputValues = function ( element ) {
    var jqElement = jQuery(element);
    var value = jqElement.val();
    return {
        original : jqElement.data('origVal'),
        previous : jqElement.data('prevVal'),
        current : value,
        neew : value
    };
}

JQueryUtil.state = JQueryUtil.state || {};
JQueryUtil.state.controlIsPressed = false;
JQueryUtil.stateChecking = JQueryUtil.stateChecking || {};
JQueryUtil.stateChecking.controlIsPressed = false;
JQueryUtil.keyCode = {
    CONTROL : "17"
};

JQueryUtil.enableControlStateChecking = function() {

    if (JQueryUtil.stateChecking.controlIsPressed) {
        return;
    }

    JQueryUtil.stateChecking.controlIsPressed = true;

    jQuery(document).keydown(function(event){
        if(event.which == JQueryUtil.keyCode.CONTROL) {
            JQueryUtil.state.controlIsPressed = true;
        }
    });

    jQuery(document).keyup(function(event){
        if(event.which == JQueryUtil.keyCode.CONTROL) {
            JQueryUtil.state.controlIsPressed = false;
        }
    });
}

/**
 * Modifie le style d'éléments à la volée.
 * à utiliser en dernier recours ( Il n'est pas conseillé de modifier le CSS de cette manière )
 * @param css_override : un tableau d'object 'Override'
 * 	         'css' est une chaine de style CSS classique
 *           'clazz' est une liste de classe CSS séparées pas des espaces
 * @param parent[optionel] : un élement du dom contenant les élements à modifier, si nul tout le document est parcouru
 *
 * @define Override :
 * 		selector(String) : un selecteur jQuery, utiliser 'this' pour ajouter l'objet parent passé en paramètre
 * 		css(String) : style CSS à ajouter aux éléments
 * 		clazz(String) : classe à ajouter aux éléments
 */
function applyCssOverride(overrides, parent) {
    var REGEX_KEY_VALUE = /\s*(.+)\s*:(.*)/
    var REGEX_IMPORTANT = /(.*)!\s*important\s*/
    var REGEX_CONTAINS_THIS = /,?\s*this\s*,?/
    var i = 0;
    var l = overrides.length;
    for ( i = 0 ; i < l ; i ++) {
        var override = overrides[i];
        var elements = parent ? jQuery(parent).find(override.selector) : jQuery(override.selector);

        if (override.selector.match(REGEX_CONTAINS_THIS)) {
            if (parent) {
                elements.push(parent);
            } else {
                if (console && console.warn)
                    console.warn("Use of 'this' pseudo selector with no parent defined");
            }
        }

        if (elements.length<1) {
            continue;
        }

        if (override.css) {
            var css_overrides = override.css.split(";");
            var ico = 0;
            var lco = css_overrides.length;
            for ( ico = 0 ; ico < lco ; ico ++) {
                var css_override = css_overrides[ico];
                if (jQuery.trim(css_override) == "")
                    continue;
                if ((result = REGEX_KEY_VALUE.exec(css_override))) {
                    var css_key = result[1];
                    var css_value = result[2];
                    var important = false;
                    if ((result = REGEX_IMPORTANT.exec(css_value))) {
                        css_value = result[1];
                        important = true
                    }
                    elements.each(function() {
                        if (important) {
                            if (typeof this.style.setProperty == 'function')
                                this.style.setProperty( css_key, css_value, 'important' );
                        } else {
                            jQuery(this).css(css_key,css_value);
                        }
                    });
                } else {
                    if (console && console.warn)
                        console.warn("Malformed css : '"+css_override+"'");
                }
            }
        }
        if (override.clazz) {
            var clazz_overrides = override.clazz.split(" ");
            var ico = 0;
            var lco = clazz_overrides.length;
            for ( ico = 0 ; ico < lco ; ico ++) {
                var clazz = clazz_overrides[ico];
                if (jQuery.trim(clazz) == "")
                    continue;
                elements.each(function() {
                    jQuery(this).addClass(clazz);
                });
            }
        }
    }
}
