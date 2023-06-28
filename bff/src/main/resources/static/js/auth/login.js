var accessToken;

var emailForm = document.querySelector("#form2Example1");
var passwordForm = document.querySelector("#form2Example2");
var loginBtn = document.querySelector("#btn-login");

loginBtn.addEventListener("click", function () {
	let config = {
		method: "POST",
		headers: { "Content-Type": "application/json"},
		credentials : 'include',
		body: JSON.stringify({
			email: emailForm.value,
			password: passwordForm.value
		}),
	};
	console.log(emailForm.value)
	console.log(passwordForm.value)
	fetch("https://workingtime-be.kro.kr/auth/login", config)
		.then((response) => response.json())
		.then((data) => {
			if (data.code == 200) {
				location.href = "/check/check"
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
		console.log(result.role)
		console.log(Date.now())
		var nowDate = new Date(result.exp * 1000)
		console.log(nowDate)
		console.log(new Date(Date.now()))
		if (new Date(Date.now()) > nowDate) {
			console.log("expired")
			reissueinit();
		}
		else {
			console.log("not expired")
			if(result.role == "ROLE_USER")
			{
				location.href = "/auth/emailverification";
			}
			else if(result.role == "ROLE_BANNEDUSER")
			{
				location.href = "/banned/banned";
			}
			else if(result.role == "ROLE_VERIFIEDUSER" || result.role == "ROLE_ADMIN" || result.role == "ROLE_SUPERADMIN")
			{
				location.href = "/check/check";
			}
			else
			{
				deleteCookie("Authorization")
				
				location.href = "/error";
			}
		}
	}
	else {
		reissueinit();
	}

};

function reissueinit()
{
	let config = {
		method: "GET",
		headers: { "Content-Type": "application/json"},
		credentials: 'include'
	};
	fetch("https://workingtime-be.kro.kr/auth/reissue", config)
		.then((response) => response.json())
		.then((data) => {
			if (data.code == 200) {
				location.href = "/check/check"
			}
			else {
				deleteCookie("Authorization")
				
			}
		});
}

function activeInput(inputQuery)
{
    inputQuery.addEventListener("input",()=> {
        if(inputQuery.value == "")
        {
            inputQuery.classList.remove("active");
        }
        else
        {
            inputQuery.classList.add("active");
        }
    });
}

var form2Example1 = document.querySelector("#form2Example1");
activeInput(form2Example1);

var form2Example2 = document.querySelector("#form2Example2");
activeInput(form2Example2);