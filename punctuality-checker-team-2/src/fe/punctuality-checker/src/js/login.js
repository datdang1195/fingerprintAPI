const URL_API = 'https://api.t2.pc.ekinoffy.com';
const ACCESS_TOKEN = localStorage.getItem('access_token');

// render funtion of Google
function onSuccess(googleUser) {
    const profile = googleUser.getBasicProfile();
    const getEmail = profile.getEmail();
    localStorage.setItem('getEmail', getEmail);
    const id_token = googleUser.getAuthResponse().id_token;
    if(id_token) {
        $.ajax({
            url: `${URL_API}/login`,
            type: 'GET',
            dataType: 'json',
            headers: {
                'access_token': id_token
            },
            contentType: 'application/json; charset=utf-8',
            success: function (result) {
                $('span.abcRioButtonContents').text("SIGNED IN WITH GOOGLE");
                $('.abcRioButtonContentWrapper span').css({"margin-top": "20px", "line-height": "70px" });
                const auth2 = gapi.auth2.getAuthInstance();
                const access_token = result.accessToken;
                localStorage.setItem('access_token', access_token);
                const path = window.location.protocol + "//" + window.location.host + "/main.html";
                if(access_token) location.href = path;
                auth2.signOut().then(function () {});
                auth2.disconnect();
            },
            error: function (error) {
                const auth2 = gapi.auth2.getAuthInstance();
                auth2.disconnect();
                if(error.status === 400){
                    $('span.abcRioButtonContents').text('SIGN IN WITH GOOGLE');
                    $('span.abcRioButtonContents').css('line-height', '68px');
                    $('.container-main100-not-access').append('<div class="not_access">Sorry! Your google account is not permission to access!</div>')
                    $('.not_access').css({'color':'red', 'font-size':'18px'});
                    setTimeout(() =>{
                        auth2.signOut().then(function () {
                            $('.not_access').remove();                            
                        });
                        auth2.disconnect();
                    }, 3000)
                }
                if(error.status === 500){                    
                    $('.container-main100-not-access').append('<div class="not_access">Sorry, Server is overloaded please try again later!</div>')
                    $('.not_access').css({'color':'red', 'font-size':'18px'});
                    setTimeout(() =>{
                        auth2.signOut().then(function () {
                            $('.not_access').remove();
                            $('span.abcRioButtonContents').text('SIGN IN WITH GOOGLE');
                            $('span.abcRioButtonContents').css('line-height', '68px');
                        });
                        auth2.disconnect();
                    },4000)
                }
            }
        });
    }
}
function onFailure() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
    });
    auth2.disconnect();
}
function renderButton() {
    gapi.signin2.render('my-signin2', {
    'scope': 'profile email',
    'width': 240,
    'height': 50,
    'longtitle': true,
    'theme': 'dark',
    'onsuccess': onSuccess,
    'onfailure': onFailure
    });
}


$(document).ready(function() {
    $(function() {
        $('input[name="daterange"]').daterangepicker({
        opens: 'left'
        }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
        });
    });

    function renderSignOut(){
        let tmpSignOut = '';
        tmpSignOut += `<a class="main100-form-signout" id="signOut">${localStorage.getItem('getEmail')}&nbsp;<i class="fa fa-sign-out"></i></a>`;
        $('.js-main100-form-signout').html(tmpSignOut);
    }
    const elmToggle = $('.main100-form-openmenu');
    const showMenu = $('#showMenu');
    const hideMenu = $('#closeMenu');
    showMenu.on('click', function(){
        elmToggle.css('display', 'block');
        showMenu.css('opacity', '0');
    })

    hideMenu.on('click', function(){
        elmToggle.css('display', 'none');
        showMenu.css('opacity', '1');
    })

    renderSignOut();

})
