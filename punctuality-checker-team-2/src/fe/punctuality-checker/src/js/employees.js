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