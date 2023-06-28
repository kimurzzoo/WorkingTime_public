var startBtn = document.querySelector("#btn-start")
var workingTimeText = document.querySelector("#working-time")

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var accessToken;

let timerId;
let time;
const stopwatch = document.querySelector("#working-time");
let  hour, min, sec;


function printTime() {
    time++;
    stopwatch.innerText = getTimeFormatString();
}

//시계 시작 - 재귀호출로 반복실행
function startClock() {
    printTime();
    stopClock();
    timerId = setTimeout(startClock, 1000);
}

//시계 중지
function stopClock() {
    if (timerId != null) {
        clearTimeout(timerId);
    }
}

// 시간(int)을 시, 분, 초 문자열로 변환
function getTimeFormatString() {
    hour = parseInt(String(time / (60 * 60)));
    min = parseInt(String((time - (hour * 60 * 60)) / 60));
    sec = time % 60;

    return String(hour).padStart(2, '0') + ":" + String(min).padStart(2, '0') + ":" + String(sec).padStart(2, '0');
}

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

startBtn.addEventListener("click", function() {
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };

    if(startBtn.innerText == "START")
    {
        fetch("https://workingtime-be.kro.kr/check/startcheck", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                console.log(data.description)
                fetch("https://workingtime-be.kro.kr/check/nowcheck", config)
                    .then((response) => response.json())
                    .then((data) => {
                        if(data.code == 200)
                        {
                            if(data.nowCheck.endTime == null)
                            {
                                time = Math.floor((Date.now() - new Date(data.nowCheck.startTime).getTime()) / 1000)
                                workingTimeText.style.visibility = "visible"
                                startBtn.innerText = "END"
                                startClock();
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
        fetch("https://workingtime-be.kro.kr/check/endcheck", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                workingTimeText.innerText = ""
                workingTimeText.style.visibility = "hidden"
                startBtn.innerText = "START"
                stopClock();
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

function deleteCookie(name) {
	document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

window.onload = function(){
    accessToken = get_cookie("Authorization");
    console.log("first accessToken : " + accessToken)
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
    console.log(result.role)
    var nowDate = new Date(result.exp * 1000)
    console.log(nowDate)
    if(Date.now() > nowDate)
    {
        reissueinit();
    }
    else
    {

        nowcheckinit(result);
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
            console.log(data)
            if(data.code == 200)
            {
                accessToken = get_cookie("Authorization").replace('+', ' ');
                tokeninit();
            }
            else
            {
                alert(data.description)
                location.href = "/auth/login"
            }
        });
}

function nowcheckinit(result)
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
    
        fetch("https://workingtime-be.kro.kr/check/nowcheck", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    console.log(data);
                    if(data.nowCheck.endTime == null)
                    {
                        time = Math.floor((Date.now() - new Date(data.nowCheck.startTime).getTime()) / 1000)
                        workingTimeText.style.visibility = "visible"
                        startBtn.innerText = "END"
                        startClock();
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
        deleteCookie("Authorization")
		
        location.href = "/error";
    }
}