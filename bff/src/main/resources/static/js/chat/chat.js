var startBtn = document.querySelector("#btn-start")

var menuBtn = document.querySelector("#menu-btn")
var isOpen = false;

var sideBar = document.querySelector("#sidebarMenu")

var logoutBtn = document.querySelector("#logout-btn");

var endPoint = "https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/chatting";
var stompClient;
var headers;
var companyId;
var userId;

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

sendBtn.addEventListener("click", function(){
    var chat = JSON.stringify({
        message: document.querySelector("#typeMessage").value,
    });
    console.log(chat)
    stompClient.send("/pub/message", {Authorization : accessToken}, chat);
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
                        method: "GET",
                        headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
                      };
            
                    fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/chatroom", config)
                        .then((response) => response.json())
                        .then((data) => {
                            if(data.code == 200)
                            {
                                document.querySelector(".company-name").innerHTML = data.roomName;
                                companyId = data.roomId;
                                userId = data.userId;
            
                                headers = {Authorization : accessToken}
                                stompClient = Stomp.over(new SockJS(endPoint));
                                stompClient.connect(headers, function (frame) {
                                    console.log("connected: " + frame);
                                    stompClient.subscribe("/sub/message/" + companyId, function (response) {
                                        var data = JSON.parse(response.body);
                                        console.log(data)
                                        var str = "<div class='col-6'>";
                                            str += "<div class='alert alert-secondary'>";
                                            str += "<b>" + data.userId + " : " + data.message + "</b>";
                                            str += "</div></div>";
                                        document.querySelector(".chat-box").innerHTML += str;
                                    }, headers);
                                })
                            }
                            else
                            {
                                location.href = "/auth/login"
                            }
                        });
                {
                    location.href = "/auth/login"
                }
            }
        });
    }
    else
    {
        let config = {
            method: "GET",
            headers: { "Content-Type": "application/json", "credentials": "include", "Authorization" : accessToken},
          };

        fetch("https://workingtime-api-gateway-uoeqax7pxa-du.a.run.app/chatroom", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                document.querySelector(".company-name").innerHTML = data.roomName;
                companyId = data.roomId;
                userId = data.userId;

                headers = {Authorization : accessToken}
                stompClient = Stomp.over(new SockJS(endPoint));
                stompClient.connect(headers, function (frame) {
                    console.log("connected: " + frame);
                    stompClient.subscribe("/sub/message/" + companyId, function (response) {
                        var data = JSON.parse(response.body);
                        console.log(data)
                        var str = "<div class='col-6'>";
                            str += "<div class='alert alert-secondary'>";
                            str += "<b>" + data.userId + " : " + data.message + "</b>";
                            str += "</div></div>";
                        document.querySelector(".chat-box").innerHTML += str;
                    }, headers);
                })
            }
            else
            {
                location.href = "/auth/login"
            }
        });

    }
}