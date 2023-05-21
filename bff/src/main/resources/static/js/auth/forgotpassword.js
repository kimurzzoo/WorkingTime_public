function deleteCookie(name) {
	document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function get_cookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

window.onload = function () {
	accessToken = get_cookie("Authorization");
	if (accessToken != null) {
		accessToken = accessToken.replace('+', ' ');
		var base64Payload = accessToken.replace('Bearer ', '').split('.')[1]; //value 0 -> header, 1 -> payload, 2 -> VERIFY SIGNATURE
		var payload = atob(base64Payload); 
		var result = JSON.parse(payload.toString())
		console.log(payload)
		console.log(result.exp)
		console.log(Date.now())
		var nowDate = new Date(result.exp * 1000)
		console.log(nowDate)
		console.log(new Date(Date.now()))
		if (new Date(Date.now()) > nowDate) {
			console.log("expired")
			fetch("http://192.168.0.9:8080/auth/reissue")
				.then((response) => response.json())
				.then((data) => {
					if (data.code == 200) {
						location.href = "../../templates/check/check.html"
					}
					else {
						deleteCookie("Authorization")
						deleteCookie("refreshtoken")
					}
				});
		}
		else {
			console.log("not expired")
			location.href = "../../templates/check/check.html"
		}
	}
	else {
		fetch("http://192.168.0.9:8080/auth/reissue")
			.then((response) => response.json())
			.then((data) => {
				if (data.code == 200) {
					location.href = "../../templates/check/check.html"
				}
				else {
					deleteCookie("Authorization")
					deleteCookie("refreshtoken")
				}
			});
	}

};