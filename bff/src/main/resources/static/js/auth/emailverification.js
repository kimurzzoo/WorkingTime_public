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
                }
                else
                {
                    location.href = "/auth/login"
                }
            });
    }
};