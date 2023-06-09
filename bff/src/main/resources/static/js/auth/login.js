var emailForm = document.querySelector("#form2Example1");
var passwordForm = document.querySelector("#form2Example2");
var loginBtn = document.querySelector("#btn-login");

loginBtn.addEventListener("click", function () {
	let config = {
		method: "POST",
		headers: { "Content-Type": "application/json", "credentials": "include" },
		body: JSON.stringify({
			email: emailForm.value,
			password: passwordForm.value
		}),
	};
	console.log(emailForm.value)
	console.log(passwordForm.value)
	fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/login", config)
		.then((response) => response.json())
		.then((data) => {
			if (data.code == 200) {
				//location.href = "/check/check"
				console.log("access token : " + get_cookie("Authorization"));
			}
		});
});

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
			fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
				.then((response) => response.json())
				.then((data) => {
					if (data.code == 200) {
						location.href = "/check/check"
					}
					else {
						deleteCookie("Authorization")
						deleteCookie("refreshtoken")
					}
				});
		}
		else {
			console.log("not expired")
			location.href = "/check/check"
		}
	}
	else {
		fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
			.then((response) => response.json())
			.then((data) => {
				if (data.code == 200) {
					location.href = "/check/check"
				}
				else {
					deleteCookie("Authorization")
					deleteCookie("refreshtoken")
				}
			});
	}

};