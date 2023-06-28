//

var accessToken;
var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var myTimeText = document.querySelector(".my-time-text")
var companyTimeText = document.querySelector(".company-time-text")
var allTimeText = document.querySelector(".all-time-text")

var btnAsc = document.querySelector("#btn-asc");
var btnDesc = document.querySelector("#btn-desc");

var rankList = document.querySelector("#rank-list");

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

btnAsc.addEventListener("click", function(){
    rank("100", "week", "short");
    btnAsc.classList.add("bg-primary");
    btnDesc.classList.remove("bg-primary");
});

btnDesc.addEventListener("click", function(){
    rank("100", "week", "long");
    btnDesc.classList.add("bg-primary");
    btnAsc.classList.remove("bg-primary");
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
        tokencheck();
    }
    else
    {
        reissueinit();
    }
};

function tokencheck()
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
        makeList(result);
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
                tokencheck();
            }
            else
            {
                location.href = "/auth/login"
            }
        });
}

function makeList(result)
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
        myTime();
        companyTime("week");
        allTime("week");
        rank("100", "week", "short");
    }
    else
    {
        deleteCookie("Authorization")
		
        location.href = "/error";
    }
}

function myTime()
{
    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include',
        body : JSON.stringify({
            companyName : null,
            startDate : null,
            endDate : null
		})
    };
    fetch("https://workingtime-be.kro.kr/rank/avgmine", config)
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

function companyTime(duration)
{
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
    };
    fetch("https://workingtime-be.kro.kr/rank/avgcompany?duration=" + duration, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                companyTimeText.innerHTML = "Working time of my company : " + data.avgTime;
            }
            else
            {
                alert(data.description);
            }
        });
}

function allTime(duration)
{
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
    };
    fetch("https://workingtime-be.kro.kr/rank/avgallcompany?duration=" + duration, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                allTimeText.innerHTML = "Average Working time of all company : " + data.avgTime;
            }
            else
            {
                alert(data.description);
            }
        });
}

function rank(range, duration, order)
{
    rankList.innerHTML = "";
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
    };
    fetch("https://workingtime-be.kro.kr/rank/avgrank?range=" + range + "&duration=" + duration + "&order=" + order, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                for(let i = 0; i < data.ranks.length; i++)
                {
                    let j = i + 1;
                    companyRank = data.ranks[i];
                    rankList.innerHTML += `<tr>
                                            <td>
                                                <p class="fw-normal mb-1">${j}</p>
                                            </td>
                                            <td>
                                                <p class="fw-normal mb-1">${companyRank.avgTime}</p>
                                            </td>
                                            <td>
                                                <p class="fw-normal mb-1">${companyRank.companyName}</p>
                                            </td>
                                        </tr>`
                }
            }
            else
            {
                alert(data.description);
            }
        });
}