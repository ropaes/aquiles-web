//variaveis globais
var table, simpletable, pageNumberActual = 0, pageNumberActualSimpleDt = 0,
    dtSearchValuesArray = new Array(20);

$(document).ready(function () {

    initAutocomplete(false);

    $('.checkboxMarkAll').click(function () {
        $('.checkboxMark').click();
    });

    if (podeUsarComponente(".slimScrollMessages")) {
        $('.slimScrollMessages').slimScroll({
            height: '200px'
        });
    }

    // Pesquisa de item do menu
    initSearchMenu();

    //datatable
    initDataTable(false);
    initSimpleDataTable();

    //text resizer
    if (podeUsarComponente('#text-resizer-controls li a')) {
        $("#text-resizer-controls li a").textresizer({
            target: ".content-wrapper",
            type: "css",
            sizes: [
                // Small. Index 0
                {
                    "font-size": "10px",
                    "font-family": "Tahoma"
                },

                // Medium. Index 1
                {
                    "font-size": "14px",
                    "font-family": "Tahoma"
                },

                // Large. Index 2
                {
                    "font-size": "16px",
                    "font-family": "Tahoma"
                },

                // Larger. Index 3
                {
                    "font-size": "18px",
                    "font-family": "Tahoma"
                }],
            selectedIndex: 0
        });
    }

    disableAllInputsOnVisu();
    initSummernote();

    jsf.ajax.addOnEvent(handleAjaxEvents);
});

function initSimpleDataTable() {

    //SIMPLE DATATABLE
    if (podeUsarComponente('.simpleDataTableJquery')) {

        if (typeof simpletable != 'undefined') {
            simpletable.destroy();
        }

        simpletable = $('.simpleDataTableJquery').DataTable({
            "paging": true,
            "lengthChange": false,
            "searching": false,
            "ordering": false,
            "autoWidth": false,
            "info": false,
            "language": {
                "info": "Mostrando _START_ de _END_ - Total _TOTAL_ registros",
                "infoEmpty": "Sem registros disponíveis",
                "search": "Pesquisar: ",
                "zeroRecords": "Nenhum registro encontrado",
                "emptyTable": "Não ha registros a serem mostrados",
                "infoFiltered": "(filtrado de _MAX_ registros)",
                "paginate": {
                    "first": "Primeiro",
                    "last": "Último",
                    "next": "Proximo",
                    "previous": "Anterior"
                },
            }
        });

        simpletable.page(pageNumberActualSimpleDt).draw(false);

        $('.simpleDataTableJquery').on('page.dt', function () {
            var info = simpletable.page.info();
            pageNumberActualSimpleDt = info.page;
        });
    }
}

function initDataTable(destroy) {

    var classDataTableJquery = '.sDataTableJquery';
    if (podeUsarComponente(classDataTableJquery)) {

        if (typeof table != 'undefined') {
            table.destroy();
        }

        // Adicionar o campo de busca ao cabeçalho do datatable
        $(classDataTableJquery + ' thead th')
            .each(
                function () {
                    if ($(this).index() > 0) {
                        var styleClass = $(classDataTableJquery + ' thead th').eq(
                            $(this).index()).attr('class');
                        if (styleClass != 'not-desktop') {
                            var dataAquilesSearchLength = $(classDataTableJquery + ' thead th').eq(
                                $(this).index()).attr('data-aquiles-search-length');
                            if (!dataAquilesSearchLength) {
                                dataAquilesSearchLength = 99999;
                            }
                            var dataAquilesSearchMask = $(classDataTableJquery + ' thead th').eq(
                                $(this).index()).attr('data-aquiles-search-mask');
                            var dataAquilesSearchIgnore = $(classDataTableJquery + ' thead th').eq(
                                $(this).index()).attr('data-aquiles-search-ignore');
                            if (!dataAquilesSearchIgnore || dataAquilesSearchIgnore == "false" || dataAquilesSearchIgnore == false) {
                                var content = '<br><input type="text" class="' + dataAquilesSearchMask + '" style="width:95%;height:20px;font-size:12px;font-weight:normal;" maxlength="' + dataAquilesSearchLength + '">';
                                var contentPart = '<br><input';
                                var htmlContent = $(this).html();
                                if (htmlContent.indexOf(contentPart) == -1) {
                                    $(this).html($(this).html() + content);
                                }

                            }
                        }
                    }
                });

        table = $(classDataTableJquery).DataTable({
            "paging": true,
            "lengthChange": false,
            "searching": true,
            /* No ordering applied by DataTables during initialisation */
            "order": [],
            "info": true,
            "autoWidth": false,
            "dom": '<"top"i>rt<"bottom"lp><"clear">',
            "language": {
                "info": "Mostrando _START_ de _END_ - Total _TOTAL_ registros",
                "infoEmpty": "Sem registros disponíveis",
                "search": "Pesquisar: ",
                "zeroRecords": "Dados não encontrados",
                "emptyTable": "Dados não encontrados",
                "infoFiltered": "(filtrado de _MAX_ registros)",
                "paginate": {
                    "first": "Primeiro",
                    "last": "Último",
                    "next": "Proximo",
                    "previous": "Anterior"
                },
            }

        });

        // Aplica a busca ao datatable
        table.columns().every(function () {
            var that = this;
            if (this.index() > 0) {
                $('input', this.header()).bind('keyup', function () {
                    dtSearchValuesArray[that.index()] = this.value;
                    that.search(this.value).draw(false);
                });
            }
        });

        //preenche as buscas rapidas em caso de ajax, pois ele apaga os valores digitados
        table.columns().every(function () {
            if (this.index() > 0 && dtSearchValuesArray[this.index()] != null) {
                $('input', this.header()).val(dtSearchValuesArray[this.index()]);
                this.search(dtSearchValuesArray[this.index()]).draw(false);
            }
        });


        table.page(pageNumberActual).draw(false);

        $(classDataTableJquery).on('page.dt', function () {
            var info = table.page.info();
            pageNumberActual = info.page;
        });

        if ($(classDataTableJquery).DataTable().data().length < 2) {
            var leastOneColumnFilled = false;
            $(classDataTableJquery + ' tbody>tr>td').each(function (d) {
                if ($(this).text().length > 0) {
                    leastOneColumnFilled = true;
                }
            });

            //se não há nenhuma coluna preenchida
            if (!leastOneColumnFilled) {
                $(classDataTableJquery).DataTable().clear().draw();
            }
        }

        //força a inicialização das mascaras no datatable quick-search
        initMasks();
    }
}


/*
 * Usado para realizar a pesquisa de um item no menu
 */
function initSearchMenu() {

    var config = {
        searchInput: ".sidebar-form .form-control",
        menuItem: "li.treeview"
    };

    $(config.searchInput).on("keyup", function () {
        if (this.value.length > 0) {
            $(config.menuItem).hide().filter(function () {
                return $(this).text().toLowerCase().indexOf($(config.searchInput).val().toLowerCase()) != -1;
            }).show();
        }
        else {
            $(config.menuItem).show();
        }
    });
}


/*
 * Custom Label formatter ----------------------
 */
function labelFormatter(label, series) {
    return '<div style="font-size:11px; text-align:center; padding:2px; color: #fff; font-weight: 600;">'
        + label + "<br>" + Math.round(series.percent) + "%</div>";
}

function handleAjaxEvents(data) {

    superHandleAjaxEvents(data);
    initDataTable(true);
    initSimpleDataTable();
    disableAllInputsOnVisu();
    initAutocomplete();
    initSummernote();
}

/**
 * Usado para desabilitar todos os campos input dentro do formulário
 * */
function disableAllInputsOnVisu() {
    if (podeUsarComponente('.panelGroupVisuExist')) {
        $(':input').each(function () {
            if (!$(this).attr('data-ignore-visu') &&
                $(this).attr('name') != 'javax.faces.ViewState' && $(this).attr('type') != 'hidden') {
                $(this).attr("disabled", "disabled");
            }
        });

        $('a.btn').each(function () {
            if (!$(this).attr('data-ignore-visu')) {
                $(this).addClass("disabled");
            }
        });

        $('.aquiles-visu-mode').each(function () {
            $(this).attr('style', 'display:none;');
        });
    }
}

function initAutocomplete() {
    if (podeUsarComponente('.autocomplete-search')) {
        var options = {
            callback: function (value) {
                if (value.length >= 2) {
                    $(this).nextAll("input[type='submit']").first().click();
                    $(this).nextAll("div").first().show(100);
                } else {
                    $(this).nextAll("div").first().hide(100);
                }
            },
            wait: 200,
            highlight: true,
            allowSubmit: false,
            captureLength: 2
        };

        //$('.autocomplete-search').off('keydown paste cut input');
        $('.autocomplete-search').unbind('keydown.typewatch paste.typewatch cut.typewatch input.typewatch');
        $('.autocomplete-search').typeWatch(options);
    }
}

function fireSearchAutoComplete(comp) {
    $(comp).parent().parent().find('[type=submit]').click();
    $(comp).parent().parent().find('.autocomplete-container').show(100);
}

function hideAutocompleteTable() {
    $('.autocomplete-container').hide(500);
}

function clickButtonOnEnter(e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        var idTarget = document.getElementById(e.target.id).getAttribute('data-clickonenter-id');
        if (idTarget) {
            document.getElementById(idTarget).click();
        }

        var containsIdTarget = document.getElementById(e.target.id).getAttribute('data-clickonenter-contains-id');
        if (containsIdTarget) {
            tryClickContainsId(containsIdTarget);
        }
    }
}

/**
 * Try to click in a HTML object using 'contains' with id passed as argument
 * */
function tryClickContainsId(containsId) {
    if ($("[id*=" + containsId + "]").length == 1) {
        $("[id*=" + containsId + "]").click();
    }
}

/**
 * Init the summernote component used to render a wysiwyg editor
 * */
function initSummernote() {
    $('.summernote_div').each(function () {
        $(this).summernote({
            callbacks: {
                onInit: function () {
                    if ($(this).hasClass('disabledSummernote')){
                        $(this).summernote('code', $(this).parent().next('input').val());
                        $(this).summernote('disable');
                    }else {
                        $(this).summernote('code', $(this).parent().next('input').val());
                    }
                },
                onChange: function (contents, $editable) {
                    $(this).parent().next('input').val(contents);
                }
            }
        });
    });
}

