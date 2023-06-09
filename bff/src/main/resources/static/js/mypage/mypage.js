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

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

menuBtn.addEventListener("click", function(){
    if(isOpen)
    {
        sideBar.style.display = "none";
        isOpen = false;
    }
    else
    {
        sideBar.style.display = "";
        isOpen = true;
    }
});

logoutBtn.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/logout", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200 || data.code == 400)
            {
                location.href = "/auth/login"
            }
            else
            {
                alert(data.description);
            }
        });
});


function get_cookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

btnChangeNick.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/changenickname?newnickname=" + typeNickname.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/mypage/mypage"
            }
            else if(data.code == 400)
            {
                location.href = "/auth/login"
            }
        });
});

btnChangeCompany.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/changecompany?companyname=" + typeCompany.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/mypage/mypage"
            }
            else if(data.code == 400)
            {
                location.href = "/auth/login"
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
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/changepassword", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/auth/login"
            }
            else if(data.code == 400)
            {
                alert("change password failed")
                location.href = "/auth/login"
            }
            else
            {
                alert(data.description)
                location.href = "/mypage/mypage"
            }
        });
});

btnWithdrawal.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/withdrawal", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/auth/login"
            }
            else
            {
                alert("withdrawal failed")
                location.href = "/auth/login"
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
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    location.href = "/auth/login"
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
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');

                    let config = {
                        method: "GET",
                        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
                      };
            
                    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/info", config)
                        .then((response) => response.json())
                        .then((data) => {
                            if(data.code == 200)
                            {
                                typeNickname.value = data.nickname;
                                typeCompany.value = data.companyName;
                            }
                            else
                            {
                                location.href = "/auth/login"
                            }
                        });
                }
                else
                {
                    location.href = "/auth/login"
                }
            });
    }
    else
    {
        let config = {
            method: "GET",
            headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
          };

        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/info", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    typeNickname.value = data.nickname;
                    typeCompany.value = data.companyName;
                }
                else
                {
                    location.href = "/auth/login"
                }
            });
    }
};

document.querySelector("#typeNewPassword").addEventListener("input",()=> {
    if(document.querySelector("#typeNewPassword").value == "")
    {
        document.querySelector('#typeNewPassword').classList.remove("active");
    }
    else
    {
        document.querySelector('#typeNewPassword').classList.add("active");
    }
});

document.querySelector("#typeNickname").addEventListener("input",()=> {
    if(document.querySelector("#typeNickname").value == "")
    {
        document.querySelector('#typeNickname').classList.remove("active");
    }
    else
    {
        document.querySelector('#typeNickname').classList.add("active");
    }
});

document.querySelector("#typeCompany").addEventListener("input",()=> {
    if(document.querySelector("#typeCompany").value == "")
    {
        document.querySelector('#typeCompany').classList.remove("active");
    }
    else
    {
        document.querySelector('#typeCompany').classList.add("active");
    }
});

document.querySelector("#typeCurPassword").addEventListener("input",()=> {
    if(document.querySelector("#typeCurPassword").value == "")
    {
        document.querySelector('#typeCurPassword').classList.remove("active");
    }
    else
    {
        document.querySelector('#typeCurPassword').classList.add("active");
    }
});

document.querySelector("#typeNewPasswordConfirm").addEventListener("input",()=> {
    if(document.querySelector("#typeNewPasswordConfirm").value == "")
    {
        document.querySelector('#typeNewPasswordConfirm').classList.remove("active");
    }
    else
    {
        document.querySelector('#typeNewPasswordConfirm').classList.add("active");
    }
});