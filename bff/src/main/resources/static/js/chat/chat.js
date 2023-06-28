var startBtn = document.querySelector("#btn-start")

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var chatBox = document.querySelector(".chat-box");

var endPoint = "https://workingtime-be.kro.kr/chatting";
var stompClient;
var headers;
var companyId;
var userId;

var accessToken;

var sendBtn = document.querySelector(".message-type-btn");

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

sendBtn.addEventListener("click", function(){
    var chat = JSON.stringify({
        message: document.querySelector("#typeMessage").value,
    });
    console.log("send message : " + chat)
    stompClient.send("/pub/message/" + companyId, {Authorization : accessToken}, chat);

    document.querySelector("#typeMessage").value = "";
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
    if(accessToken != null)
    {
        accessToken = accessToken.replace('+', ' ');
        tokencheck();
    }
    else
    {
        reissueinit();
    }
}

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
        chatinit(result);
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
            {
                location.href = "/auth/login"
            }
        }
    });
}

function stompstart()
{
    headers = {Authorization : accessToken}

    stompClient = Stomp.over(new SockJS(endPoint));
    stompClient.connect(headers, function (frame) {
        console.log("connected: " + frame);
        stompClient.subscribe("/sub/message/" + companyId, function (response) {
            let isBottom = true;

            if(chatBox.scrollTop + chatBox.offsetHeight >= chatBox.scrollHeight)
            {
                isBottom = true;
            }
            else
            {
                isBottom = false;
            }

            var datum = JSON.parse(response.body);
            console.log(datum)

            var str = "<div class='col-6'>";

            if(datum.userId == userId)
            {
                str += "<div class='alert alert-secondary' style='float: right; background-color: yellow'>";
                str += "<b>" + datum.message + "</b>";
            }
            else
            {
                str += "<div class='alert alert-secondary' style='float: left'>";
                str += "<b>" + datum.userId + " : " + datum.message + "</b>";
            }
            str += "</div></div>";
            chatBox.innerHTML += str;

            if(isBottom)
            {
                chatBox.scrollTop = chatBox.scrollHeight;
            }
        }, headers);
    })
}

function chatinit(result)
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
        fetch("https://workingtime-be.kro.kr/chatroom", config)
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    console.log(data)
                    document.querySelector(".company-name").innerHTML = data.chatroom.roomName;
                    companyId = data.chatroom.roomId;
                    userId = data.chatroom.userId;

                    stompstart();

                    setInterval(tokencheck, 5 * 60 * 1000);
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