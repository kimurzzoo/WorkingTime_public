var accessToken;

var typeNickname = document.querySelector("#typeNickname")
var typeCompany = document.querySelector("#typeCompany")
var typeCurPassword = document.querySelector("#typeCurPassword")
var typeNewPassword = document.querySelector("#typeNewPassword")
var typeNewPasswordConfirm = document.querySelector("#typeNewPasswordConfirm")

var btnChangeNick = document.querySelector("#btn-change-nick")
var btnChangeCompany = document.querySelector("#btn-change-company")
var btnChangePassword = document.querySelector("#btn-change-password")
var btnWithdrawal = document.querySelector("#btn-change-nick")


function get_cookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

btnChangeNick.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("http://192.168.0.9:8080/mypage/changenickname?newnickname=" + typeNickname.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "../../templates/mypage/mypage.html"
            }
            else if(data.code == 400)
            {
                location.href = "../../templates/auth/login.html"
            }
        });
});

btnChangeCompany.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("http://192.168.0.9:8080/mypage/changecompany?companyname=" + typeCompany.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "../../templates/mypage/mypage.html"
            }
            else if(data.code == 400)
            {
                location.href = "../../templates/auth/login.html"
            }
        });
});

btnChangePassword.addEventListener("click", function(){
    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
        body: JSON.stringify({
          password : typeCurPassword.value,
          newPassword : typeNewPassword.value,
          newPasswordConfirm : typeNewPasswordConfirm.value
        }),
      };
    fetch("http://192.168.0.9:8080/auth/changepassword", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "../../templates/auth/login.html"
            }
            else if(data.code == 400)
            {
                alert("change password failed")
                location.href = "../../templates/auth/login.html"
            }
            else
            {
                alert(data.description)
                location.href = "../../templates/mypage/mypage.html"
            }
        });
});

btnWithdrawal.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("http://192.168.0.9:8080/auth/withdrawal", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "../../templates/auth/login.html"
            }
            else
            {
                alert("withdrawal failed")
                location.href = "../../templates/auth/login.html"
            }
        });
});

window.onload = function(){
    accessToken = get_cookie("Authorization");
    if(accessToken != null)
    {
        accessToken = accessToken.replace('+', ' ');
    }
    else
    {
        fetch("http://192.168.0.9:8080/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    location.href = "../../templates/auth/login.html"
                }
            });
    }
    var base64Payload = accessToken.replace('Bearer ', '').split('.')[1]; //value 0 -> header, 1 -> payload, 2 -> VERIFY SIGNATURE
    var payload = atob(base64Payload); 
    var result = JSON.parse(payload.toString())
    console.log(payload)
    var nowDate = new Date(result.exp * 1000)
    console.log(nowDate)
    if(Date.now() > nowDate)
    {
        fetch("http://192.168.0.9:8080/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    location.href = "../../templates/auth/login.html"
                }
            });
    }
};
