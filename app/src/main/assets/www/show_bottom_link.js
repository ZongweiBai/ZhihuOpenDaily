var $theme;

function show(info) {
	if (!info) return;
	info = JSON.parse(info);
	if (info.theme_id && info.theme_name && info.theme_image) {
		$theme = $('<div class="origin-source-wrap" onclick="openTheme(' + info.theme_id + ')"><a class="origin-source with-link" ><img src="' + info.theme_image + '" class="source-logo"><span class="text">本文来自：' + info.theme_name + '</span></a><a class="focus-link" href="zhihu-theme-subscribe"><span class="btn-label">关注</span></a></div>').appendTo($(".question").last());

        if (info.theme_subscribed === true) {
            $theme.removeClass('unfocused');
        } else if (info.theme_subscribed === false) {
            $theme.addClass('unfocused');
        }

	} else if (info.section_id && info.section_name && info.section_thumbnail) {
		$(".question").last().append('<div class="origin-source-wrap" onclick=openSection(' + info.section_id + ')><a class="origin-source with-link" ><img src="' + info.section_thumbnail + '" class="source-logo"><span class="text">本文来自：' + info.section_name.replace(/#/g, "") + ' · 合集</span></a></div>');
	}
}

function openTheme(theme_id) {
	
	if(window.injectedObject){
		injectedObject.openTheme(theme_id);
	}
}

function openSection(section_id) {
	
	if(window.injectedObject){
		injectedObject.openSection(section_id);
	}
}