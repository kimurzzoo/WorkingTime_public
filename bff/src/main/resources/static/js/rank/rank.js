var accessToken;
var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var myTimeText = document.querySelector(".my-time-text")

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
                        method: "POST",
                        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
                    };
                    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/rank/avgmine", config)
                        .then((response) => response.json())
                        .then((data) => {
                            if(data.code == 200)
                            {
                                myTimeText.innerHTML = "My Working time : " + data.avgTime;
                            }
                            else
                            {
                                alert(data.description);
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
        console.log("access token : " + accessToken)
        let config = {
            method: "POST",
            headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
            body: JSON.stringify({
            }),
        };
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/rank/avgmine", config)
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                if(data.code == 200)
                {
                    myTimeText.innerHTML = "My Working time : " + data.avgTime;
                }
                else
                {
                    alert(data.description);
                }
            });
    }
};