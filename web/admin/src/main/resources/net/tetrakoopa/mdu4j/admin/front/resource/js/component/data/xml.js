


function initxmlTree(xmlText) {

    var xmlBuilder = JsTreeHelper.data.XmlBuilder({
        //decorator : decorator
    });
    var dataTree = xmlBuilder.createData(xmlText);

    var imagesFolderRelativeRootDir = "../../image/component/data";
    var types = JsTreeHelper.type.getDefaultTypes(imagesFolderRelativeRootDir);

    var xmlTree = jQuery("div#data-xmlTree").jstree({
        'core' : {
            'themes': {
                'theme': 'default',
                'responsive': true
            },
            'search' : {
                'fuzzy' : false,
                'show_only_matches': true,
                'close_opened_onclear' : true
            },// so that create works
            'check_callback' : true,
            'data': dataTree,

        },
        'types' : types,
        //'plugins' : [ 'themes', 'json_data', 'types', 'ui', 'search' ]
        'plugins' : [ 'themes', 'json_data', 'types', 'search' ]
    });

    var to = false;
    jQuery('input#dataSearch').keyup(function () {
        var searchBox = jQuery(this);
        if(to) { clearTimeout(to); }
        to = setTimeout(function () {
            var v = searchBox.val();
            xmlTree.jstree(true).search(v);
        }, 250);
    });

}

function xmlTreeSetTransformedInputAndDecoration(xmlText, xslt, decoration) {
    var transformedXml;
    //var cleanObjectXml = magicXML.transform(xmlText, xslt);
    //transformedXml = $('<div>').append($(cleanObjectXml).clone()).html();
    jQuery("body").append("<div id='xml-transformResult'/>");
    jQuery("div[id='xml-transformResult']").xslt(xmlText, xslt);
    transformedXml = jQuery("div[id='xml-transformResult']").html();
    jQuery("div[id='xml-transformResult']").remove();
    xmlTreeSetInputAndDecoration(transformedXml, decoration)
}


function xmlTreeSetInputAndDecoration(xmlText, decoration) {

    var xmlTree = jQuery("div#data-xmlTree").jstree(true);

    var xmlBuilder = JsTreeHelper.data.XmlBuilder({
        //decorator : decorator
    });
    var dataTree = xmlBuilder.createData(xmlText);

    xmlTree.settings.core.data = dataTree;
    xmlTree.refresh();
}

