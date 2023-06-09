var nicknameForm = document.querySelector("#form3Example1c");
var emailForm = document.querySelector("#form3Example3c");
var passwordForm = document.querySelector("#form3Example4c");
var passwordConfirmForm = document.querySelector("#form3Example4cd");
var registerBtn = document.querySelector("#btn-register");

registerBtn.addEventListener("click", function() {
    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nickname : nicknameForm.value,
            email : emailForm.value,
            password : passwordForm.value,
            passwordConfirm : passwordConfirmForm.value
        }),
      };
      fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/register", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/auth/emailverification"
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


window.onload = function(){
    accessToken = get_cookie("Authorization");
    if(accessToken != null)
    {
        accessToken = accessToken.replace('+', ' ');
        var base64Payload = accessToken.replace('Bearer ', '').split('.')[1]; //value 0 -> header, 1 -> payload, 2 -> VERIFY SIGNATURE
		var payload = atob(base64Payload); 
		var result = JSON.parse(payload.toString())
		console.log(payload)
		var nowDate = new Date(result.exp * 1000)
        console.log(nowDate)
        if(Date.now() > nowDate)
        {
            fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
                .then((response) => response.json())
                .then((data) => {
                    if(data.code == 200)
                    {
                        location.href = "/check/check"
                    }
                    else
                    {
                        deleteCookie("Authorization")
                        deleteCookie("refreshtoken")
                    }
                });
        }
        else
        {
            location.href = "/check/check"
        }
    }
    else
    {
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    location.href = "/check/check"
                }
                else
                {
                    deleteCookie("Authorization")
                    deleteCookie("refreshtoken")
                }
            });
    }
    
};