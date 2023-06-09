var startBtn = document.querySelector("#btn-start")
var workingTimeText = document.querySelector("#working-time")

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var accessToken;

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

startBtn.addEventListener("click", function() {
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };

    if(startBtn.innerText == "START")
    {
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/startcheck", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                console.log(data.description)
                fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/nowcheck", config)
                    .then((response) => response.json())
                    .then((data) => {
                        if(data.code == 200)
                        {
                            if(data.nowCheck.endTime == null)
                            {
                                workingTimeText.innerText = data.nowCheck.startTime
                                workingTimeText.style.visibility = "visible"
                                startBtn.innerText = "END"
                            }
                        }
                        else if(data.code == 2051)
                        {
                            alert("no check error");
                        }
                        else
                        {
                            alert(data.description);
                        }
                    });
            }
            else
            {
                alert("start check error")
            }
        });
    }
    else if(startBtn.innerText == "END")
    {
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/endcheck", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                workingTimeText.innerText = ""
                workingTimeText.style.visibility = "hidden"
                startBtn.innerText = "START"
            }
            else
            {
                alert("end check error");
            }
        });
    }
    else
    {
        console.log("not checked")
    }
});

function get_cookie(name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
}

window.onload = function(){
    accessToken = get_cookie("Authorization");
    console.log("first accessToken : " + accessToken)
    if(accessToken != null)
    {
        accessToken = accessToken.replace('+', ' ');
    }
    else
    {
        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    alert("1 : " + data.description)
                    //location.href = "/auth/login"
                }
            });
    }
    console.log("accessToken : " + accessToken);
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
                console.log(data)
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                    let config = {
                        method: "GET",
                        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
                      };
            
                    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/nowcheck", config)
                        .then((response) => response.json())
                        .then((data) => {
                            if(data.code == 200)
                            {
                                console.log(data);
                                if(data.nowCheck.endTime == null)
                                {
                                    workingTimeText.innerText = data.nowCheck.startTime
                                    workingTimeText.style.visibility = "visible"
                                    startBtn.innerText = "END"
                                }
                            }
                            else if(data.code == 2051)
                            {
            
                            }
                            else
                            {
                                alert(data.description);
                            }
                        });
                }
                else
                {
                    alert("2 : " + data.description)
                    //location.href = "/auth/login"
                }
            });
    }
    else
    {
        let config = {
            method: "GET",
            headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
          };

        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/nowcheck", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    console.log(data)
                    if(data.nowCheck.endTime == null)
                    {
                        console.log("null")
                        workingTimeText.innerText = data.nowCheck.startTime
                        workingTimeText.style.visibility = "visible"
                        startBtn.innerText = "END"
                    }
                    else
                    {
                        console.log("not null")
                    }
                }
                else if(data.code == 2051)
                {

                }
                else
                {
                    alert(data.description);
                }
            });
    }
};