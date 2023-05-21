window.onload = function(){
    accessToken = get_cookie("Authorization");
    if(accessToken != null)
    {
        accessToken = accessToken.replace('+', ' ');
    }
    else
    {
        fetch("http://192.168.0.9:8080/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    location.href = "../../templates/auth/login.html"
                }
            });
    }
    var payload = jwt.decode(accessToken)
    console.log(payload)
    var nowDate = new Date(payload.exp)
    console.log(nowDate)
    if(Date.now() > nowDate)
    {
        fetch("http://192.168.0.9:8080/auth/reissue")
            .then((response) => response.json())
            .then((data) => {
                if(data.code == 200)
                {
                    accessToken = get_cookie("Authorization").replace('+', ' ');
                }
                else
                {
                    location.href = "../../templates/auth/login.html"
                }
            });
    }
};