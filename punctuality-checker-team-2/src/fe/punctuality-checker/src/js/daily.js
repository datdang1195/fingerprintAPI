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


