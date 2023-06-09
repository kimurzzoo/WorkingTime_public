var maxPage;
var checks;
var nowPage;
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
                        body: JSON.stringify({
                            pageNum: nowPage
                        }),
                    };
            
                    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/mychecks", config)
                        .then((response) => response.json())
                        .then((data) => {
                            if(data.code == 200)
                            {
                                maxPage = data.maxPage;
                                checks = data.checks;
            
                                for(let check of checks)
                                {
                                    checksTable.innerHTML += `<tr>
                                                                <td>
                                                                    <p class="fw-normal mb-1">${check.id}</p>
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
            
                                pageHump = Math.floor((nowPage + 1) / 5);
                                startPage = 1 + pageHump * 5;
                                endPage = 5 + pageHump * 5;
            
                                if(startPage > 1)
                                {
                                    document.querySelector(".left-container").innerHTML += `<span onclick="leftClick(${pageHump})" style= "cursor: pointer"><</span>`;
                                }
            
                                if(endPage > maxPage)
                                {
                                    endPage = maxPage;
                                }
            
                                if(endPage < maxPage)
                                {
                                    document.querySelector(".right-container").innerHTML += `<span onclick="rightClick(${pageHump})" style="cursor: pointer">></span>`;
                                }
            
                                for(let i=startPage; i<= endPage; i++)
                                {
                                    if(i == nowPage + 1)
                                    {
                                        document.querySelector(".page-container").innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                                    }
                                    else
                                    {
                                        document.querySelector(".page-container").innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                                    }
                                }
            
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
        let config = {
            method: "POST",
            headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
            body: JSON.stringify({
                pageNum: nowPage
            }),
        };

        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/mychecks", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    console.log(data);
                    maxPage = data.maxPage;
                    checks = data.checks;

                    for(let check of checks)
                    {
                        checksTable.innerHTML += `<tr>
                                                    <td>
                                                        <p class="fw-normal mb-1">${check.id}</p>
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

                    pageHump = Math.floor((nowPage + 1) / 5);
                    startPage = 1 + pageHump * 5;
                    endPage = 5 + pageHump * 5;

                    if(startPage > 1)
                    {
                        document.querySelector(".left-container").innerHTML += `<span onclick="leftClick(${pageHump})" style= "cursor: pointer"><</span>`;
                    }

                    if(endPage > maxPage)
                    {
                        endPage = maxPage;
                    }

                    if(endPage < maxPage)
                    {
                        document.querySelector(".right-container").innerHTML += `<span onclick="rightClick(${pageHump})" style="cursor: pointer">></span>`;
                    }

                    for(let i=startPage; i<= endPage; i++)
                    {
                        if(i == nowPage + 1)
                        {
                            document.querySelector(".page-container").innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                        }
                        else
                        {
                            document.querySelector(".page-container").innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                        }
                    }

                }
                else
                {
                    alert(data.description);
                }
            });
    }
};

function movePage(page)
{
    nowPage = page - 1;

    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
        body: JSON.stringify({
            pageNum: nowPage
        }),
    };

    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/mychecks", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                maxPage = data.maxPage;
                checks = data.checks;

                checksTable.innerHTML = "";
                for(let check of checks)
                {
                    checksTable.innerHTML += `<tr>
                                                <td>
                                                    <p class="fw-normal mb-1">${check.id}</p>
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

                pageHump = Math.floor((nowPage) / 5);
                startPage = 1 + pageHump * 5;
                endPage = 5 + pageHump * 5;

                for(let i=startPage; i<= endPage; i++)
                {
                    if(i == nowPage + 1)
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                    else
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                }
            }
            else
            {
                alert(data.description);
            }
        });
}

function leftClick(hump)
{
    nowPage = (hump - 1) * 5;

    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
        body: JSON.stringify({
            pageNum: nowPage
        }),
    };

    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/mychecks", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                maxPage = data.maxPage;
                checks = data.checks;

                checksTable.innerHTML = "";
                for(let check of checks)
                {
                    checksTable.innerHTML += `<tr>
                                                <td>
                                                    <p class="fw-normal mb-1">${check.id}</p>
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

                

                pageHump = Math.floor((nowPage) / 5);
                startPage = 1 + pageHump * 5;
                endPage = 5 + pageHump * 5;

                for(let i=startPage; i<= endPage; i++)
                {
                    if(i == nowPage + 1)
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                    else
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                }

                document.querySelector(".left-container").innerHTML = "";

                if(startPage > 1)
                {
                    document.querySelector(".left-container").innerHTML += `<span onclick="leftClick(${pageHump})" style= "cursor: pointer"><</span>`;
                }

                document.querySelector(".right-container").innerHTML = "";

                if(endPage < maxPage)
                {
                    document.querySelector(".right-container").innerHTML += `<span onclick="rightClick(${pageHump})" style="cursor: pointer">></span>`;
                }
            }
            else
            {
                alert(data.description);
            }
        });
}

function rightClick(hump)
{
    nowPage = (hump + 1) * 5;

    let config = {
        method: "POST",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
        body: JSON.stringify({
            pageNum: nowPage
        }),
    };

    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/mypage/mychecks", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                maxPage = data.maxPage;
                checks = data.checks;

                checksTable.innerHTML = "";
                for(let check of checks)
                {
                    checksTable.innerHTML += `<tr>
                                                <td>
                                                    <p class="fw-normal mb-1">${check.id}</p>
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

                

                pageHump = Math.floor((nowPage) / 5);
                startPage = 1 + pageHump * 5;
                endPage = 5 + pageHump * 5;

                for(let i=startPage; i<= endPage; i++)
                {
                    if(i == nowPage + 1)
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2 text-primary fw-bold" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                    else
                    {
                        document.querySelector(".page-container").innerHTML += `<span class="m-2" onclick="movePage(${i})" style="cursor: pointer">${i}</span>`;
                    }
                }

                document.querySelector(".left-container").innerHTML = "";

                if(startPage > 1)
                {
                    document.querySelector(".left-container").innerHTML += `<span onclick="leftClick(${pageHump})" style= "cursor: pointer"><</span>`;
                }

                document.querySelector(".right-container").innerHTML = "";

                if(endPage < maxPage)
                {
                    document.querySelector(".right-container").innerHTML += `<span onclick="rightClick(${pageHump})" style="cursor: pointer">></span>`;
                }
            }
            else
            {
                alert(data.description);
            }
        });
}

function deleteCheck(id)
{
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
      };
    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/check/deletecheck?checkid=" + id, config)
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