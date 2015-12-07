//application.js
$(document).ready(function () {
	$('.show-all').on('click', function() {
		if(!$(this).hasClass('link')) {
			$(this).toggleClass('closed');
		}
		if($(this).hasClass('closed')) {
			$(this).text("OPEN");
		}
		else {
			$(this).text("CLOSE");
		}
		var body = $(this).closest('body');
		var nav = body.find('nav');
		var main = body.find('main');
		var footer = body.find('footer');
		nav.toggleClass('hidden');
		main.toggleClass('hidden');
		footer.toggleClass('hidden');
	});
});