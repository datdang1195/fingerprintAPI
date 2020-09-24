const path = window.location.protocol + "//" + window.location.host + '/'
const pathName = window.location.pathname;
const listRoutesRequired = ['main'];
const check = listRoutesRequired.find(item => pathName.indexOf(item) !== -1);

if(check && !localStorage.getItem('access_token')) {
    location.href = path;
}