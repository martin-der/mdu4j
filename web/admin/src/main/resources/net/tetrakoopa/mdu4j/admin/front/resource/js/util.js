
var ObjectUtil = {}

ObjectUtil.safeFreezeObject = function (object) {
	if ('function' == typeof Object.freeze )
		Object.freeze(object);
}
ObjectUtil.safeAttributeAppend = function (object, attributeName, value) {
	if (object[attributeName])
		object[attributeName] = object[attributeName] + value;
	else
		object[attributeName] = value;
}


var ConverterUtil = {}

ConverterUtil.escapeHtml = function (s) {
	return s
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;");
}

function updateQueryStringParameter(uri, key, value) {
    var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
    var separator = uri.indexOf('?') !== -1 ? "&" : "?";
    if (uri.match(re)) {
        return uri.replace(re, '$1' + key + "=" + value + '$2');
    }
    else {
        return uri + separator + key + "=" + value;
    }
}
