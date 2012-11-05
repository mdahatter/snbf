AUI().ready(

/*
 * This function gets loaded when all the HTML, not including the portlets, is
 * loaded.
 */

function() {
});

Liferay.Portlet.ready(

/*
 * This function gets loaded after each and every portlet on the page.
 * 
 * portletId: the current portlet's id node: the Alloy Node object of the
 * current portlet
 */

function(portletId, node) {
});

Liferay.on('allPortletsReady',

/*
 * This function gets loaded when everything, including the portlets, is on the
 * page.
 */

function() {
});

function plegardesplegar(identificacion) {
	var elemento = document.getElementById(identificacion);
	if (elemento.className == "visible") {
		elemento.className = "invisible";
		setCookie("verLayer", "false", 1);
	} else {
		elemento.className = "visible";
	}
}

function getCookie(c_name) {
	var i, x, y, ARRcookies = document.cookie.split(";");
	for (i = 0; i < ARRcookies.length; i++) {
		x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
		y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);

		if (x == c_name) {
			return y;
		}
	}
}

function setCookie(c_name, value, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = value
			+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = c_name + "=" + c_value;
}