var maxPage;
var checks;
var nowPage = 0;
var accessToken;

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var checksTable = document.querySelector("#table-list")

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
        console.log(result)
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

function makeBtn()
{
        let pageContainer = document.querySelector(".page-container");
        pageHump = Math.floor((nowPage) / 5);
        startPage = 1 + pageHump * 5;
        endPage = 5 + pageHump * 5;

        if(endPage > maxPage)
        {
            endPage = maxPage;
        }

        for(let i=startPage; i<= endPage; i++)
        {
            if(i == nowPage + 1)
            {
                pageContainer.innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
            }
            else
            {
                pageContainer.innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
            }
        }

        document.querySelector(".left-container").innerHTML = "";

        let leftPage = (pageHump - 1) * 5 + 1;

        if(startPage > 1)
        {
            document.querySelector(".left-container").innerHTML += `<span onclick="movePage(${leftPage})" style= "cursor: pointer"><</span>`;
        }

        document.querySelector(".right-container").innerHTML = "";

        let rightPage = (pageHump + 1) * 5 + 1;

        if(endPage < maxPage)
        {
            document.querySelector(".right-container").innerHTML += `<span onclick="movePage(${rightPage})" style="cursor: pointer">></span>`;
        }
    
}

function makeList(result)
{
    console.log(result)
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
            method: "POST",
            headers: { "Content-Type": "application/json", "Authorization" : accessToken},
            credentials: 'include',
            body: JSON.stringify({
                pageNum: nowPage
            }),
        };

        fetch("https://workingtime-be.kro.kr/mypage/mychecks", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    maxPage = data.maxPage;
                    checks = data.checks;

                    checksTable.innerHTML = "";
                    for(let i = 0; i < checks.length; i++)
                    {
                        let check = checks[i];
                        let j = i+1;
                                        
                        checksTable.innerHTML += `<tr>
                                                    <td>
                                                        <p class="fw-normal mb-1">${j}</p>
                                                    </td>
                                                    <td>
                                                        <p class="fw-normal mb-1">${check.startTime}</p>
                                                    </td>
                                                    <td>
                                                        <p class="fw-normal mb-1">${check.endTime}</p>
                                                    </td>
                                                    <td>
                                                        <p class="fw-normal mb-1">${check.workingTime}</p>
                                                    </td>
                                                    <td>
                                                        <p class="fw-normal mb-1">${check.companyName}</p>
                                                    </td>
                                                    <td>
                                                        <span>
                                                            <button onclick="deleteCheck(${check.id})" type="button" class="btn btn-link btn-sm btn-rounded">
                                                                Delete
                                                            </button>
                                                        </span>
                                                    </td>
                                                </tr>`
                    }
                    document.querySelector(".page-container").innerHTML = "";

                    makeBtn();
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

function movePage(page)
{
    nowPage = page - 1;

    tokencheck();
}

function deleteCheck(id)
{
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/check/deletecheck?checkid=" + id, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                movePage(nowPage);
            }
            else if(data.code == 400)
            {
                location.href = "/auth/login"
            }
        });
}