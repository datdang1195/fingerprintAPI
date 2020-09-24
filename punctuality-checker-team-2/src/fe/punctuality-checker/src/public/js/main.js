const data = {
    dataFinger : [
        {
            id:'LTMN',
            name : 'Le Thi Minh Nguyet',
            status : 'AP'
        },
        {
            id:'AV',
            name : 'Anh Vu',
            status : 'APA'
        },
        {
            id:'LNQT',
            name : 'Le Ngo Quang Truong',
            status : 'APB'
        },
        {
            id:'NTTT',
            name : 'Nguyen Thi Thu Thuan',
            status : 'APB'
        },
        {
            id:'BLLL',
            name : 'Banh Lau La Lieu',
            status : 'APB'
        },
        {
            id:'NVA',
            name : 'Nguyen Van A',
            status : 'APB'
        },
        {
            id:'NVB',
            name : 'Nguyen Van B',
            status : 'APB'
        },
        {
            id:'NVC',
            name : 'Nguyen Van C',
            status : 'APB'
        },
        {
            id:'NVD',
            name : 'Nguyen Van D',
            status : 'APB'
        },
        {
            id:'NVE',
            name : 'Nguyen Van E',
            status : 'APB'
        },
        {
            id:'BL',
            name : 'Banh Lau',
            status : 'APB'
        },
        {
            id:'LL',
            name : 'La Lieu',
            status : 'APB'
        },
        {
            id:'SN',
            name : 'Suu Nhi',
            status : 'APB'
        },
        {
            id:'TTB',
            name : 'Tran Thi Banh',
            status : 'APB'
        },
        {
            id:'NTK',
            name : 'Nguyen Thi Kieu',
            status : 'APB'
        },

    ]
}

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

const ITEM_PERPAGE = 25;
let dataStatus = [];
let dataSelect= [];
const loading = $('#loading');
loading.hide();
let searchValue = [];
let dateValue = [];
let statusValue = [];
let dateLatest  = '';


$(document).ready(function() {
    const dailyExport = $('#js-daily-export');

    // datepicker
    const dailyButton = $("#js-daily-button");
    let dateFormatDaily = '';

    dailyButton.click(function (e) {
        e.preventDefault();
        loading.show();
        getDataByDate(dateFormatDaily);
    });

    function initDatepicker() {
        let dayNewLatest = new Date(dateLatest);
        $("#datepicker").datepicker({
            autoclose: true,
            format: 'dd-M-yyyy',
            todayHighlight: true,
        }).on('changeDate', function(date) {
            $('.daily-search').val('');
            let getDate = date.date;
            let getDateFormat = formatDateString(getDate);
            dateValue.push(getDateFormat);
            // call API with this date
            dateFormatDaily = getDateFormat;
            // loading data;
        }).datepicker('setDate', dayNewLatest);
    }

    // Export file daily page
    function getUrlExportDaily() {
        // get last value of search
        let value = searchValue.slice(-1).pop();
        let searchName = (typeof value === 'undefined') ? '' : value;
        let status = statusValue.slice(-1).pop();
        let statusName = (typeof status === 'undefined' || status === 'ALL' ) ? '' : status;
        let dateFilter = dateValue.slice(-1).pop();
        let dateFilterName = (typeof dateFilter === 'undefined') ? '' : dateFilter;
        return `${URL_API}/exportdaily?dateFilter=${dateFilterName}&status=${statusName}&name=${searchName}`;

    }

    dailyExport.on('click', function(e) {
        e.preventDefault();
        document.location.href = getUrlExportDaily();
    });
    //format date
    function formatDateString(date) {
        let dd = date.getDate();
        let mm = date.getMonth() + 1;
        let yyyy = date.getFullYear();
        if (dd < 10) {
            dd = `0${dd}`;
        }
        if (mm < 10) {
            mm = `0${mm}`
        }
        return `${yyyy}-${mm}-${dd}`;
    }

    // render data list Daily page
    function renderDataListDaily(dataFingerDaily, currentPage){
        let tmpDataListDaily = '';
        for(let i = 0; i< dataFingerDaily.length; i++){
            let data = dataFingerDaily[i];
            tmpDataListDaily += `<tr>
                                    <td scope="row" class="daily daily_no"><div>${(currentPage - 1) * 25 + i + 1}</div></td>
                                    <td scope="row" class="daily daily_name"><div>${data.userName}</div></td>
                                    <td class="daily daily_status"><div data-toggle="tooltip" class="daily_tooltip" title="${data.description}">${data.status}</div></td>
                                </tr>`;
        }
        $('.js-tbody-daily').html(tmpDataListDaily);
    }

    //search name
    function searchDailyName(){

        $(document).ready(function () {
            const searchClear = $('span#searchclear');
            $(document).on('keyup', '.daily-search', function () {
                searchClear.css('opacity', '1');
                let searchField = $(this).val();
                searchValue.push(searchField);
                const regex = new RegExp(searchField, 'i');
                let dataSearch = dataSelect.filter(item => {
                    return item.userName.match(regex);
                });

                if(dataSearch.length === 0){
                    $('.js-tbody-daily').html('');
                }
                renderDataListDaily(dataSearch, ITEM_PERPAGE);
                initPaginationDaily(dataSearch)
            })
            searchClear.click(function () {
                let searchField = '';
                searchValue.push(searchField);
                searchClear.css('opacity', '0');
                $(this).siblings('.daily-search').val('');
                renderDataListDaily(dataStatus, ITEM_PERPAGE);
                initPaginationDaily(dataStatus)
            })
        })
    }

    //filter status
    function selectStatus(){
        // select status for desktop
        $(document).on('change', '#table_select_option', function () {
            $('.js-tbody-daily').html('');
            const textOption = $('#table_select_option option:selected').val();
            statusValue.push(textOption);
            if (textOption) {
                $('.daily-search').val('');
            }
            // find name based on status
            if (textOption === 'ALL'){
                dataSelect = dataStatus;
            } else {
                dataSelect = dataStatus.filter((status) => {
                    return status.status === textOption;
                });
            }
            initPaginationDaily(dataSelect);
        })
        // select status for mobile
        $(document).on('change', '#table_select_option_mobile', function () {
            $('.js-tbody-daily').html('');
            const textOption = $('#table_select_option_mobile option:selected').val();
            // find name based on status
            if (textOption === 'ALL'){
                dataSelect = dataStatus;
            } else {
                dataSelect = dataStatus.filter((status) => {
                    return status.status === textOption;
                });
            }
            initPaginationDaily(dataSelect);
        })
    }

    //pagination for data list Daily page
    function initPaginationDaily(data, itemPerPage = ITEM_PERPAGE){
        const $paginationWrapper = $('#pagination__wrapper__daily');
        if ($paginationWrapper.data("twbs-pagination")){
            $paginationWrapper.twbsPagination('destroy');
        }

        if (data && data.length >= 1) {
            let numberPage = data.length / itemPerPage;
            if (data.length % itemPerPage) {
                numberPage += 1;
            }
            const firstTwentyFiveitems = data.slice(0, 26);
            renderDataListDaily(firstTwentyFiveitems);

            $paginationWrapper.twbsPagination({
                nextClass: 'page-items next',
                prevClass: 'page-items prev',
                lastClass: 'page-items last',
                firstClass: 'page-items first',
                first: '&#171;',
                prev: '&#8249;',
                next: '&#8250;',
                last: '&#187;',
                currentPage: 1,
                totalPages: numberPage,
                visiblePages: 3,
                onPageClick: function(_, page) {
                    const startIndex = (page - 1) * 25;
                    const endIndex = page * 25;
                    const listShowOnePage = data.slice(
                        startIndex,
                        endIndex
                    );
                    renderDataListDaily(listShowOnePage, page);
                },
            });
        } else {
            renderDataListDaily([]);
            let tmpDataListDailyNodata = '';
            tmpDataListDailyNodata += `<tr>
                                    <td scope="row" class="daily daily_no" colspan="3"><div>NO DATA</div></td>
                                </tr>`;
            $('.js-tbody-daily').html(tmpDataListDailyNodata)
        }
    }

    // get data chose day
    function getDataByDate(date) {
        $.ajax({
            url: `${URL_API}/dailystatus`,
            contentType: 'application/json',
            type: 'POST',
            headers: {
                'Authorization': 'Bearer ' + ACCESS_TOKEN,
                'Access-Control-Allow-Credentials' : 'true',
                'Access-Control-Allow-Origin': '*'
            },
            data: JSON.stringify(
                {
                    "fromDate": date
                }),
            dataType: 'json',
            success: function(data) {
                const dataFingerDaily = data.statusList;
                dataStatus = dataFingerDaily;
                dataSelect = dataFingerDaily;

                // render data daily page
                initPaginationDaily(dataFingerDaily);
                loading.hide();
            }
        });
    }


    // get data lastest day
    function getDataByDateLatest() {
        $.ajax({
            url: `${URL_API}/dailystatuslatest`,
            type: "GET",
            dataType: "json",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + ACCESS_TOKEN,
            },
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                const dataFingerDaily = data.statusList;
                let lastestDate = data.statusList[0].recordDate;
                dateLatest = lastestDate;
                initDatepicker();
                dataStatus = dataFingerDaily;
                dataSelect = dataFingerDaily;

                // render data daily page
                initPaginationDaily(dataFingerDaily);
            }
        });
    }

    $(document).on('click','#signOut' ,function(){
        const path = window.location.protocol + "//" + window.location.host + "/";
        location.href = path;
        localStorage.clear();
    });

    function initation() {
        getDataByDateLatest();
        selectStatus();
        searchDailyName();
    }

    initation();
});



let dataRange = [];
let dataSearchNew = [];
let searchValueRange = []
let fromDateValue = [];
let toDateValue = [];

$(document).ready(function() {
    const rangeExport = $('#js-range-export');
    // daterange picker
    let start = moment().startOf('month');
    let end = moment().endOf('month');

    $('#js-range-button').click( function(){
        loading.show();
        getDataRangeByDate(start._d, end._d);
    })
    displayChoseDay(start, end);

    $('#reportrange').daterangepicker({
        startDate: start,
        endDate: end,
        ranges: {
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    }, function(startChange, endChange) {
        displayChoseDay(startChange, endChange);
        start = startChange;
        end = endChange;
        $('.search-range').val('');
    })

    getDataRangeByDate(start._d, end._d);

    function displayChoseDay(start, end) {
        $('#reportrange span').html(start.format('DD-MMM-YYYY') + ' - ' + end.format('DD-MMM-YYYY'));
    }

    function formatDateString(date) {
        let dd = date.getDate();
        let mm = date.getMonth() + 1;
        let yyyy = date.getFullYear();
        if (dd < 10) {
            dd = `0${dd}`;
        }
        if (mm < 10) {
            mm = `0${mm}`
        }
        return `${yyyy}-${mm}-${dd}`;
    }

    function getDataRangeByDate(fromDate, toDate) {
        let startDate = formatDateString(fromDate);
        let endDate = formatDateString(toDate);
        fromDateValue.push(startDate);
        toDateValue.push(endDate);
        // call ajax range page
        $.ajax({
            url: `${URL_API}/rangestatus`,
            contentType: 'application/json',
            type: 'POST',
            headers: {
                'Authorization': 'Bearer ' + ACCESS_TOKEN,
                'Access-Control-Allow-Credentials' : 'true',
                'Access-Control-Allow-Origin': '*'
            },
            data: JSON.stringify(
                {
                    "fromDate":`${startDate}`,
                    "toDate":`${endDate}`,
                    "range":true
                }),
            dataType: 'json',
            success: function(data){
                    const dataFingerRange = data.rangeStatusList;
                    if(!data.rangeStatusList){
                        $('.js-tbody-range').append(`<tr>test</tr>`)
                    }
                    dataRange = dataFingerRange;
                    // render data list Range page
                    initPaginationRange(dataRange);
                    loading.hide();
            }
        });
    }

    function renderDataListRange(dataRange, currentPage){
        let tmpDataListRange = '';
        let totalWorkingDate = '';
        for(let i = 0; i< dataRange.length; i++){
            let data = dataRange[i];
            let totalWorking = data.totalWorkingDate;
            totalWorkingDate = totalWorking;
            tmpDataListRange += `<tr>
                                <td scope="row" class="range range_no"><div>${(currentPage - 1) * 25 + i + 1}</div></td>
                                <td scope="row" class="range range_name"><div>${data.userName}</div></td>
                                <td class="range range_present"><div>${data.workingDay}</div></td>
                                <td class="range range_dayoff"><div>${data.offDay}</div></td>
                                <td class="range range_late"><div>${data.lateSession}</div></td>
                                <td class="range range_absent"><div>${data.absentSession}</div></td>
                            </tr>`;
        }
        let tmptotalWorkingDate = `<div class="js-range_work">TOTAL WORKING DAYS: ${totalWorkingDate}</div>`;
        $('.js-tbody-range').html(tmpDataListRange);
        $('.range_work').html(tmptotalWorkingDate);
    }

    //search name
    function searchRangeName(){
        $(document).ready(function () {
            const searchClear = $('span#searchclear-range');
            $(document).on('keyup', '.search-range', function () {
                searchClear.css('opacity', '1');
                const searchField = $(this).val();
                searchValueRange.push(searchField);
                const regex = new RegExp(searchField, 'i');
                let dataSearch = dataRange.filter(item => {
                    return item.userName.match(regex);
                });
                if(dataSearch.length === 0){
                    $('.js-tbody-range').html('');
                }
                renderDataListRange(dataSearch);
                initPaginationRange(dataSearch);
                dataSearchNew = [...dataSearch];
                searchClear.click(function () {
                    let searchField = '';
                    searchValueRange.push(searchField);
                    searchClear.css('opacity', '0');
                    $(this).siblings('.search-range').val('');
                    renderDataListRange(dataRange, ITEM_PERPAGE);
                    initPaginationRange(dataRange);
                })
            })
        })
    }

    //pagination for data list range page
    function initPaginationRange(data, itemPerPage = ITEM_PERPAGE){
        const $paginationWrapper = $('#pagination__wrapper__range');
        if ($paginationWrapper.data("twbs-pagination")){
            $paginationWrapper.twbsPagination('destroy');
        }

        if (data && data.length >= 1) {
            let numberPage = data.length / itemPerPage;
            if (data.length % itemPerPage) {
                numberPage += 1;
            }
            const firstTwentyFiveitems = data.slice(0, 26);
            renderDataListRange(firstTwentyFiveitems);

            $paginationWrapper.twbsPagination({
                nextClass: 'page-items next',
                prevClass: 'page-items prev',
                lastClass: 'page-items last',
                firstClass: 'page-items first',
                first: '&#171;',
                prev: '&#8249;',
                next: '&#8250;',
                last: '&#187;',
                currentPage: 1,
                totalPages: numberPage,
                visiblePages: 3,
                onPageClick: function(_, page) {
                    const startIndex = (page - 1) * 25;
                    const endIndex = page * 25;
                    const listShowOnePage = data.slice(
                        startIndex,
                        endIndex
                    );
                    renderDataListRange(listShowOnePage, page);
                },
            });
        }else{
            renderDataListRange([]);
            let tmpDataListRangeNodata = '';
            tmpDataListRangeNodata += `<tr>
                                    <td scope="row" class="daily daily_no" colspan="6"><div>NO DATA</div></td>
                                </tr>`;
            $('.js-tbody-range').html(tmpDataListRangeNodata)
        }
    }

    // Export file range page
    function getUrlExportRange() {
        // get last value of search
        let value = searchValueRange.slice(-1).pop();
        let searchName = (typeof value === 'undefined') ? '' : value;
        let fromDate = fromDateValue.slice(-1).pop();
        let fromDateVal = (typeof fromDate === 'undefined') ? '' : fromDate;
        let toDate = toDateValue.slice(-1).pop();
        let toDateVal = (typeof toDate === 'undefined') ? '' : toDate;
        return `${URL_API}/exportrange?fromDate=${fromDateVal}&toDate=${toDateVal}&name=${searchName}`;

    }

    // Export file range page
    rangeExport.on('click', function(e) {
        e.preventDefault();
        document.location.href = getUrlExportRange();
    });



    function initation() {
        searchRangeName();
    }

    initation();
})



let dataEmployees = [];
let dataSearchEmployees = [];
let dataCheckedLastWorking = [];
let searchValueEmployees = [];

$(document).ready(function () {

    const employeesExport = $('#js-employees-export');
    const employeesSave = $('#js-employees-save');
    // render data list employees page
    function formatDateLastWorking(date) {
        const d = new Date(date)
        const ye = new Intl.DateTimeFormat('en', { year: 'numeric' }).format(d)
        const mo = new Intl.DateTimeFormat('en', { month: 'short' }).format(d)
        const da = new Intl.DateTimeFormat('en', { day: '2-digit' }).format(d)
        return `${da}-${mo}-${ye}`
    }
    function renderDataListEmployees(dataEmployees, currentPage){
        let tmpDataListEmployees = '';
        for(let i = 0; i< dataEmployees.length; i++){
            let data = dataEmployees[i];
            let checkedActive = '';
            let lastWorkingDate = '';
            const lastWorkingDateFormat = formatDateLastWorking(data.lastWorkingDate);
            data.lastWorkingDate === null
            ? lastWorkingDate = `<div class="employees_lastworkingdate"></div>`
            :lastWorkingDate = `<div class="employees_lastworkingdate">${lastWorkingDateFormat}</div>`
            data.active === true
            ?checkedActive = `<input class="employees_active_checked" type="checkbox" name="check_employees" value="employees" checked/>`
            :checkedActive = `<input class="employees_active_checked" type="checkbox" name="check_employees" value="employees"/>`

            tmpDataListEmployees += `<tr>
                                        <td scope="row" class="employees employees_no"><div>${(currentPage - 1) * 25 + i + 1}</div></td>
                                        <td scope="row" class="employees employees_id"><div>${data.id}</div></td>
                                        <td scope="row" class="employees employees_name"><div>${data.user.name}</div></td>
                                        <td class="employees employees_active"><div>${checkedActive}</div></td>
                                        <td class="employees employees_day">
                                            <div id="employees-lastworking" class="input-group date employees-lastworking" data-provide="datepicker">
                                                <input type="text" class="employees-lastworking-form form-control">
                                                <div class="input-group-addon input-group-addon-lastworking-form">
                                                    <span class="glyphicon glyphicon-th"></span>
                                                </div>
                                            </div>
                                            ${lastWorkingDate}
                                        </td>
                                    </tr>`;
        }
        $('.js-tbody-employees').html(tmpDataListEmployees);
    }

    function callAPIUpdatedActive(id, active, lastWorkingDate) {
            $.ajax({
                url: `${URL_API}/updateactive`,
                contentType: 'application/json',
                type: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + ACCESS_TOKEN,
                    'Access-Control-Allow-Credentials' : 'true',
                    'Access-Control-Allow-Origin': '*'
                },
                data: JSON.stringify({
                    "employees": [
                        {
                            "id": id,
                            "active": active,
                            "lastWorkingDate":lastWorkingDate
                        }
                    ]
                }),
                dataType: 'json',
                success: function(data){
                    const dataFingerEmployees = data.employees;
                    dataEmployees = dataFingerEmployees;
                    // render data list Employees page
                    initPaginationEmployees(dataEmployees);
                    loading.hide();
            }})
    }
    // save edit active
    employeesSave.click(function () {
        $('.search-employees').val('');
        const listEdited = dataEmployees.filter(elm => elm.isEdit);
        const checkUserWithoutSetDate = listEdited.filter(elm => !elm.active && !elm.lastWorkingDate)

        if(checkUserWithoutSetDate.length !== 0) {
            alert(`The Last Working Day must be completed before SAVING`);
        } else {
            loading.show();
            // Submit len server
            listEdited.forEach(elm => {
                const IDEmployeesEdited = elm.id;
                const activeEdited = elm.active;
                const lastWorkingDateEdited = elm.lastWorkingDate;
                callAPIUpdatedActive(IDEmployeesEdited, activeEdited, lastWorkingDateEdited)
                $("#js-employees-save").attr("disabled", true);
            })
        }

    })


    function checkActive() {
        const checkActive = ('.employees_active_checked');
        $(document).on('change', checkActive , function () {
            $("#js-employees-save").attr("disabled", false);
            const employeesActive = $(this).parents('.employees_active').siblings('.employees_day').find('.employees-lastworking');
            const employeesDatepicker = $(this).parents('.employees_active').siblings('.employees_day').find('#employees-lastworking');
            const employeesLastWorking = $(this).parents('.employees_active').siblings('.employees_day').find('.employees_lastworkingdate');
            const employeesID = $(this).parents('.employees_active').siblings('.employees_id').children('div').text();
            employeesActive.css('display', this.checked ? 'none' : 'block');
            employeesLastWorking.css('margin-top', this.checked ? '0px' : '-20px');
            employeesLastWorking.css('display', this.checked ? 'none' : 'none');
            if(this.checked) {
                checkedValue = this.checked;
                // active = true
                dataEmployees = dataEmployees.map(elm => {
                    if(employeesID == elm.id) {
                        return {
                            ...elm,
                            isEdit: true,
                            active: true,
                            lastWorkingDate: null,
                        }
                    }
                    return elm;
                })
            } else {
                // active = false
                dataEmployees = dataEmployees.map(elm => {
                    const IDCurrent = elm.id;
                    if(employeesID == IDCurrent) {
                        return {
                            ...elm,
                            active: false,
                            isEdit: true
                        }
                    }
                    return elm;
                })

                employeesDatepicker.datepicker({
                    autoclose: true,
                    format: 'dd-M-yyyy',
                    todayHighlight: true,
                }).on('changeDate', function(date) {
                    let getDate = date.date;
                    const getDatelastWorkingFormat =  formatDateString(getDate);
                    dataEmployees = dataEmployees.map(elm => {
                        const IDCurrent = elm.id;
                        if(employeesID == IDCurrent) {
                            return {
                                ...elm,
                                isEdit: true,
                                lastWorkingDate : getDatelastWorkingFormat,
                            }
                        }
                        return elm;
                    })
                })
            }
        })
    }
    // Export file Employees page
    function getUrlExportEmloyees() {
        // get last value of search
        let value = searchValueEmployees.slice(-1).pop();
        if(!value){
            return `${URL_API}/exportemployee?name=`;
        }else{
            let valueSearch = value.toLowerCase();
            return `${URL_API}/exportemployee?name=${valueSearch}`;
        }

    }
    employeesExport.on('click', function(e) {
        e.preventDefault();
        document.location.href = getUrlExportEmloyees();
    });

     //format date
     function formatDateString(date) {
        let dd = date.getDate();
        let mm = date.getMonth() + 1;
        let yyyy = date.getFullYear();
        if (dd < 10) {
            dd = `0${dd}`;
        }
        if (mm < 10) {
            mm = `0${mm}`
        }
        return `${yyyy}-${mm}-${dd}`;
    }

    //search name Employees
    function searchEmployeesName(){
        $(document).ready(function () {
            const searchClear = $('span#searchclear-employees');
            $(document).on('keyup', '.search-employees', function () {
                searchClear.css('opacity', '1');
                const searchField = $(this).val();
                searchValueEmployees.push(searchField);
                const regex = new RegExp(searchField, 'i');
                let dataSearch = dataEmployees.filter(item => {
                    return item.user.name.match(regex);
                });

                if(dataSearch.length === 0){
                    $('.js-tbody-employees').html('');
                }

                renderDataListEmployees(dataSearch, ITEM_PERPAGE);
                initPaginationEmployees(dataSearch);
                dataSearchEmployees = [...dataSearch];
                searchClear.click(function () {
                    let searchField = '';
                    searchValueEmployees.push(searchField);
                    searchClear.css('opacity', '0');
                    $(this).siblings('.search-employees').val('');
                    renderDataListEmployees(dataEmployees, ITEM_PERPAGE);
                    initPaginationEmployees(dataEmployees);
                })
            })
        })
    }

    //pagination for data list employees page
    function initPaginationEmployees(data, itemPerPage = ITEM_PERPAGE){
        const $paginationWrapper = $('#pagination__wrapper__employees');
        if ($paginationWrapper.data("twbs-pagination")){
            $paginationWrapper.twbsPagination('destroy');
        }

        if (data&& data.length >= 1) {
            let numberPage = data.length / itemPerPage;
            if (data.length % itemPerPage) {
                numberPage += 1;
            }
            const firstTwentyFiveitems = data.slice(0, 26);
            renderDataListEmployees(firstTwentyFiveitems);

            $paginationWrapper.twbsPagination({
                nextClass: 'page-items next',
                prevClass: 'page-items prev',
                lastClass: 'page-items last',
                firstClass: 'page-items first',
                first: '&#171;',
                prev: '&#8249;',
                next: '&#8250;',
                last: '&#187;',
                currentPage: 1,
                totalPages: numberPage,
                visiblePages: 3,
                onPageClick: function(_, page) {
                    const startIndex = (page - 1) * 25;
                    const endIndex = page * 25;
                    const listShowOnePage = data.slice(
                        startIndex,
                        endIndex
                    );
                    renderDataListEmployees(listShowOnePage, page);
                    loading.hide();
                },
            });
        }else{
            renderDataListEmployees([]);
            let tmpDataListEmployeesNodata = '';
            tmpDataListEmployeesNodata += `<tr>
                                    <td scope="row" class="daily daily_no" colspan="4"><div>NO DATA</div></td>
                                </tr>`;
            $('.js-tbody-employees').html(tmpDataListEmployeesNodata)
        }
    }

    // call ajax employees page
    function callEmployeesAPI() {
        $.ajax({
            url: `${URL_API}/employees`,
            contentType: 'application/json',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + ACCESS_TOKEN,
                'Access-Control-Allow-Credentials' : 'true',
                'Access-Control-Allow-Origin': '*'
            },
            dataType: 'json',
            success: function(data){
                const dataFingerEmployees = data.employees;
                dataEmployees = dataFingerEmployees;
                // render data list Employees page
                initPaginationEmployees(dataEmployees);
                loading.hide();
            }
        });
    }

    function initation() {
        checkActive();
        callEmployeesAPI();
        searchEmployeesName();
    }
    initation();
})