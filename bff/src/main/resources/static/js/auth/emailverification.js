var accessToken;

var isOpen = false;

var menuBtn = document.querySelector("#menu-btn");

var redeemBtn = document.querySelector("#btn-redeem");
var submitBtn = document.querySelector("#btn-submit");

var inputTypeRedeemCode = document.querySelector("#typeRedeemcode");

var Timer=document.querySelector('#time-remain'); //스코어 기록창-분

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

redeemBtn.addEventListener("click", function (){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/auth/sendverificationemail", config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                timerOn();
            }
            else
            {
                alert(data.description);
                location.href = "/auth/login";
            }
        });
});

submitBtn.addEventListener("click", function(){
    let config = {
        method: "GET",
        headers: { "Content-Type": "application/json", "Authorization" : accessToken},
        credentials: 'include'
      };
    fetch("https://workingtime-be.kro.kr/auth/emailverification?redeemcode=" + inputTypeRedeemCode.value, config)
        .then((response) => response.json())
        .then((data) => {
            if(data.code == 200)
            {
                inputTypeRedeemCode.value = "";
                redeemBtn.style.display = "block";
                submitBtn.style.display = "none";
                Timer.style.display = "none";
                reissueinit();
            }
        });
});

function timerOn()
{
    redeemBtn.style.display = "none";
    submitBtn.style.display = "block";
    Timer.style.display = "block";
    let startTime = 5 * 60 * 1000;
    let min=5;
    let sec=60;
    let time= startTime;

    Timer.innerHTML=min+":"+'00'; 

    PlAYTIME=setInterval(function(){
        time=time-1000; //1초씩 줄어듦
        min=time/(60*1000); //초를 분으로 나눠준다.

        if(sec>0){ //sec=60 에서 1씩 빼서 출력해준다.
            sec=sec-1;
            Timer.innerHTML=Math.floor(min)+':'+sec; //실수로 계산되기 때문에 소숫점 아래를 버리고 출력해준다.
        
        }
        else if(sec===0){
            // 0에서 -1을 하면 -59가 출력된다.
            // 그래서 0이 되면 바로 sec을 60으로 돌려주고 value에는 0을 출력하도록 해준다.
            sec=60;
            Timer.innerHTML=Math.floor(min)+':'+'00'
        }     

    },1000); //1초마다

    setTimeout(function(){
        clearInterval(PlAYTIME);
        inputTypeRedeemCode.value = "";
        redeemBtn.style.display = "block";
        submitBtn.style.display = "none";
        Timer.style.display = "none";
    }, startTime);//5분이 되면 타이머를 삭제한다.
}

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
        emailinit(result);
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

function emailinit(result)
{
    if(result.role == "ROLE_USER")
    {

    }
    else if(result.role == "ROLE_BANNEDUSER")
    {
        location.href = "/banned/banned";
    }
    else if(result.role == "ROLE_VERIFIEDUSER" || result.role == "ROLE_ADMIN" || result.role == "ROLE_SUPERADMIN")
    {
        location.href = "/mypage";
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

activeInput(inputTypeRedeemCode);