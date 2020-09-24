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


