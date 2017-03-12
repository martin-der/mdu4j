var global_themeConfigurator;

jQuery(function() {

    var defaultTooltipConfig = {
        theme: 'tooltipster-shadow',
        side: ['top', 'bottom'],
        animation: 'swing',
        interactive : true,
        updateAnimation : true,
        trigger: 'custom',
        triggerOpen: {
            mouseenter: true,
            touchstart: true
        },
        triggerClose: {
            mouseleave: true,
            originClick: true,
            touchleave: true
        }
    };

    var actionTooltipConfig = jQuery.extend(true, {}, defaultTooltipConfig, {
        theme: 'tooltipster-noir',
        side: 'right',
        animation: 'fall',
        content:  jQuery("div[id='any-admin-templates'] div[id='actiontHint-tooltip-content'] div").clone(),
        contentCloning: true,
        functionReady : function(instance, helper) {
            var action = jQuery(helper.origin);
            var quickInfo = action.attr("title") || action.parent("li").attr("title");
            var explanation = action.attr("aa:explanation");
            AnyAdmin.TemplateFiller.actionHint(instance.content(), quickInfo, explanation);
        }
    });

    var errorTooltipConfig = jQuery.extend(true, {}, defaultTooltipConfig, {
        theme: 'tooltipster-punk'
    });



    $("[title]").each(function(){
        var $this = $(this);
        var isAction = $this.hasClass("action") || $this.parent("li").hasClass("action");
        var isErrorInformational = $this.hasClass("error-informational");
        if (isErrorInformational) {
            $this.tooltipster(errorTooltipConfig);
        } else if (isAction) {
            if ($this.is('[aa\\:explanation]')) {
                $this.tooltipster(actionTooltipConfig);
            } else {
                //$this.tooltipster(actionTooltipConfig);
            }
        } else {
            $this.tooltipster(defaultTooltipConfig);
        }
    });

    AnyAdmin.setActionsCheckHandlers(function (response_callback, level, title, explanation) {
        response_callback(
            window.confirm("Action '"+level+"'\n"+title+"\nEtes vous sûr de vouloir effectuer cette action ?")
            ? AnyAdmin.Constante.DialogButton.POSITIVE
            : AnyAdmin.Constante.DialogButton.NEGATIVE
        );
    });

	var themeConfigurator = new Theme.Configurator({
		post_process : {
			done : function(theme, context) {
				if (context.trigger == 'setup') {
					if (document.referrer && document.referrer !== "") {
						jQuery.growl.notice({title: "Theme loaded", message: "Actual theme is '"+theme.label+"'"});
					}
				} else {
					jQuery.growl.notice({title: "Theme changed", message: "Theme now is '"+theme.label+"'"});
				}
			},
			fail : function(theme, context, failures) {
				var message;
				if (failures.length>0) {
					message = "";
					var i;
					for ( i=0 ; i<failures.length ; i++ )
						message = message + "<li>"+failures[i].url+":"+failures[i].error+"</li>";
					message = "<ul>"+message+"</ul>";
				}
				if (context.trigger == 'setup') {
					jQuery.growl.warning({title: "Cannot set theme '"+theme.label+"' ("+theme.name+")", message: message});
				} else {
					jQuery.growl.error({title: "Cannot set theme '"+theme.label+"' ("+theme.name+")", message: message});
				}
			}
		},
		default_theme : 'subtle',
		location : 'https://martin-der.github.io/-/theme/{0}',
		location_system : 'https://martin-der.github.io/-/theme',
		themes : [
			new Theme.Reference('country','Country'),
			new Theme.Reference('deepocean','Deep Ocean'),
			new Theme.Reference('essay','Essay'),
			new Theme.Reference('galactic','Galactic'),
			new Theme.Reference('google','Google'),
			new Theme.Reference('stoneage','Stone Age'),
			new Theme.Reference('subtle','Subtle'),
			new Theme.Reference('terminal','Terminal'),
			new Theme.Reference('CocoIsGone','Coco')
		],
	});

	jQuery(document).keypress(function(e) {
		alert("'"+e.which+"' was pressed!! "+(e.ctrlKey?'+ Ctrl':'')+(e.altlKey?'+ Alt':''));
    });

	try {
		themeConfigurator.setup();
	} catch (ex) {
		if (console && console.error) console.error("Cannot set theme", ex);
		jQuery.growl.warning({title: "Cannot set theme", message: ex});
	}

	global_themeConfigurator = themeConfigurator;
});

