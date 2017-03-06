

var AnyAdmin = AnyAdmin || {
    Constante : {
        DialogButton : {
            POSITIVE : 1,
            NEGATIVE : -1,
            NEUTRAL : 0,
            CANCEL : this.NEUTRAL
        }
    },
    showTechnicalContent : function () {
        jQuery('.technical-info').slideDown({
            easing : 'easeOutBounce',
            duration : 800,
            complete : function() {
                jQuery('.technical-accessor').css('cursor','auto');
            }
        })
    },
    prepareTechnicalAccessor : function () {
        if (jQuery(".technical-info").length > 0)
        {
            jQuery(".technical-accessor").each(function () {
                jQuery(this).css('cursor','pointer').bind('click', AnyAdmin.showTechnicalContent);
            });
        }
    },
    buildCheckValidateActionFunction : function (uiHandler, level, title, text) {
        return function (event) {
            uiHandler(function(response) {
                if (response !== AnyAdmin.Constante.DialogButton.POSITIVE) {
                    event.preventDefault();
                }
            },level, title, text);
        };
    },
    /**
     * @param checkValidateActionFunctionBuilder
     *      @return function(response_callback, level, title, text)
     *          prompt a UI asking if the user confirms the action
     *          @param response_call function()
     *              @return AnyAdmin.Constante.DialogButton.NEUTRAL ( or CANCEL ) if user cancel action
     *                      AnyAdmin.Constante.DialogButton.POSITIVE if user accepted
     *                      AnyAdmin.Constante.DialogButton.NEGATIVE if user refused
     */
    setActionsCheckHandlers : function (checkValidateActionFunctionBuilder) {
        jQuery('.action a,.quick-action a,a.action,a.quick-action').each(function(){
            var $this = $(this);
            if ($this.is('[aa\\:criticality]')) {
                $this.on('click', AnyAdmin.buildCheckValidateActionFunction(checkValidateActionFunctionBuilder, $this.attr('aa:criticality'), $this.attr('title'), $this.attr('aa:explanation')));
            }
        });
    }

};


AnyAdmin.TemplateBuilder = {
    actionHint : function () {
        return "<div class='action-hint'>"
            +"<div class='head'>"
            +"<span class='icon'></span>"
            +"<span class='title'></span>"
            +"<span class='more'><i class=\"fa fa-window-maximize\" aria-hidden=\"true\"></i></span>"
            +"</div>"
            +"<div class='action-content' style='display:none;'>"
            +"<hr/>"
            +"<div class='action-explanation'></div>"
            +"</div>"
            +"</div>";
    }
};
AnyAdmin.TemplateFiller = {
    actionHint : function (actionHintElement, title, explanation) {
        var $action = jQuery(actionHintElement);
        $action.find("span.icon").html(title);
        $action.find("span.title").html(title);
        $action.find("div.action-explanation").html(explanation);
        $action.find("span.more").css('cursor','pointer').bind('click', function() {
            $(this).css('cursor','default');
            $action.find("div.action-content").show();
        });
    }
};


var DynamicForm = DynamicForm || {
    Constante : {
        ATTRIBUTE_PREFIX : 'df'
    },
    bindValidators : function(){
        var $this = $(this);
        var regexString = $this.attr(DynamicForm.Constante.ATTRIBUTE_PREFIX+':regex');
        var regex = new RegExp(regexString);
        JQueryUtil.bindOnChange(this, function(){
            var visual = $this.next();
            var $visual = jQuery(visual);
            if (regex.test($this.val())) {
                $visual.removeClass('invalid').addClass('valid');
            } else {
                $visual.removeClass('valid').addClass('invalid');
            }
        });
    },
    validationInitializer : function () {
        var $input = jQuery(this);
        if ($input.is('['+DynamicForm.Constante.ATTRIBUTE_PREFIX+'\\:regex]')) {
            $input.after("&#160;<img src='"+AnyAdmin_staticResources_URL_prefix+"/image/icon/validation-ok.png' class='icon visual-validation invalid' />");
            DynamicForm.bindValidators.call($input);
        }
    },
    setupValidations : function (parentSelector) {
        if (parentSelector) {
            jQuery(parentSelector).find('input').each(DynamicForm.validationInitializer);
        } else {
            jQuery('input').each(DynamicForm.validationInitializer);
        }
    }
};


jQuery(function() {

    $("body").append("<div id='any-admin-templates' style='display:none;'>");

    $("div[id='any-admin-templates']").append("<div id='actiontHint-tooltip-content'>");

    $("div[id='any-admin-templates'] div[id='actiontHint-tooltip-content']").append(AnyAdmin.TemplateBuilder.actionHint());

    AnyAdmin.prepareTechnicalAccessor();
    DynamicForm.setupValidations();
});


