
function updateTextAutomaticLineReturn(checkbox) {
    var $text = jQuery("pre.log");
    if (checkbox.checked) {
        $text.removeClass('unwrapped').addClass('wrapped');
    } else {
        $text.removeClass('wrapped').addClass('unwrapped');
    }
}


jQuery(function(){
    jQuery("input[id='admin:log:line-return'][type='checkbox']").on('change', function() {
        updateTextAutomaticLineReturn(this);
    });
});

