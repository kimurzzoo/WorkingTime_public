var accessToken;

var typeNickname = document.querySelector("#typeNickname")
var typeCompany = document.querySelector("#typeCompany")
var typeCurPassword = document.querySelector("#typeCurPassword")
var typeNewPassword = document.querySelector("#typeNewPassword")
var typeNewPasswordConfirm = document.querySelector("#typeNewPasswordConfirm")

var btnChangeNick = document.querySelector("#btn-change-nick")
var btnChangeCompany = document.querySelector("#btn-change-company")
var btnChangePassword = document.querySelector("#btn-change-password")
var btnWithdrawal = document.querySelector("#btn-withdrawal")

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
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/auth/logout", config)
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

function deleteCookie(name) {
	document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function get_cookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

btnChangeNick.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/mypage/changenickname?newnickname=" + typeNickname.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/mypage"
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
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/mypage/changecompany?companyname=" + typeCompany.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                location.href = "/mypage"
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
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include',
        body: JSON.stringify({
          password : typeCurPassword.value,
          newPassword : typeNewPassword.value,
          newPasswordConfirm : typeNewPasswordConfirm.value
        }),
      };
    fetch("https://workingtime-be.kro.kr/auth/changepassword", config)
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
                location.href = "/mypage"
            }
        });
});

btnWithdrawal.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/auth/withdrawal", config)
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
        tokeninit();
    }
    else
    {
        reissueinit();
    }
};

function tokeninit()
{
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
        myinfoinit(result);
    }
}

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
                accessToken = get_cookie("Authorization").replace('+', ' ');
                tokeninit();
            }
            else
            {
                location.href = "/auth/login"
            }
        });
}

function myinfoinit(result)
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
        let config = {
            method: "GET",
            headers: { "Content-Type": "application/json", "Authorization" : accessToken},
            credentials: 'include'
          };
    
        fetch("https://workingtime-be.kro.kr/mypage/info", config)
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
        deleteCookie("Authorization")
		
        location.href = "/error";
    }
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

var inputTypeNickname = document.querySelector("#typeNickname");
activeInput(inputTypeNickname);

var inputTypeCompany = document.querySelector("#typeCompany");
activeInput(inputTypeCompany);

var inputTypeCurPassword = document.querySelector("#typeCurPassword");
activeInput(inputTypeCurPassword);

var inputTypeNewPassword = document.querySelector("#typeNewPassword");
activeInput(inputTypeNewPassword);

var inputTypeNewPasswordConfirm = document.querySelector("#typeNewPasswordConfirm");
activeInput(inputTypeNewPasswordConfirm);