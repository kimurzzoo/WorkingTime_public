var nicknameForm = document.querySelector("#form3Example1c");
var emailForm = document.querySelector("#form3Example3c");
var passwordForm = document.querySelector("#form3Example4c");
var passwordConfirmForm = document.querySelector("#form3Example4cd");
var registerBtn = document.querySelector("#btn-register");

registerBtn.addEventListener("click", function() {

    if(document.querySelector("#form2Example3c").checked)
    {
        let config = {
            method: "POST",
            headers: { "Content-Type": "application/json"},
            credentials: 'include',
            body: JSON.stringify({
                nickname : nicknameForm.value,
                email : emailForm.value,
                password : passwordForm.value,
                passwordConfirm : passwordConfirmForm.value
            }),
          };
          fetch("https://workingtime-be.kro.kr/auth/register", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    location.href = "/auth/emailverification"
                }
            });
    }
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
            reissueinit();
        }
        else
        {
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
    else
    {
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
            if(data.code == 200)
            {
                location.href = "/check/check"
            }
            else
            {
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

var form3Example1c = document.querySelector("#form3Example1c");
activeInput(form3Example1c);

var form3Example3c = document.querySelector("#form3Example3c");
activeInput(form3Example3c);

var form3Example4c = document.querySelector("#form3Example4c");
activeInput(form3Example4c);

var form3Example4cd = document.querySelector("#form3Example4cd");
activeInput(form3Example4cd);