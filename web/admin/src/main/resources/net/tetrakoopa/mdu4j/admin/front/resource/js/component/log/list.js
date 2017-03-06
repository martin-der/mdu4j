

jQuery(function(){
    jQuery("select[id='admin:log:retrieval-mode']").on('change', function(){
        window.location.href = updateQueryStringParameter ( window.location.href, 'mode', this.value);
    });
});

