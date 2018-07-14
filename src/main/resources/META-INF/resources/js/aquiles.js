$(document).ready(function () {
    initMasks();
    autoCloseAlertMessage();
    autoScrollToAlertMessage();
});

function initMasks() {
    //mascaras
    $('.cnpj-mask').mask("00.000.000/0000-00");
    $('.cpf-mask').mask("000.000.000-00");
    $('.data-mask').mask("00/00/0000");
    $('.datetime-mask').mask('00/00/0000 00:00:00');
    $('.onlynumber-mask').mask('0#');
    $('.cep-mask').mask("00000-000");
    $('.phone-mask').mask('(00)00000-0000');
    $('.phone-fixo-mask').mask('(00)0000-0000');
    $('.money-mask').mask("#.##0,00", {reverse: true});
    $('.phone-mask-noddd').mask('00000-0000');
    $('.ddd-mask').mask('00');
    $('.time-mask').mask('00:00');

    $('.upper-mask').keyup(function (event) {
        console.warn("Avoid using this javascript mask, it will be removed on future. Use CSS Text-Transform with JSF Converter instead");
        if (event.keyCode != '13' && event.keyCode != '27' && event.keyCode != '32'
            && event.keyCode != '16' && event.keyCode != '37' && event.keyCode != '36'
            && !(event.ctrlKey && event.keyCode == 65)) {
            var position = this.selectionStart;
            this.value = this.value.toUpperCase();
            this.setSelectionRange(position, position);
        }
    });

    if (podeUsarComponente('.datepicker')) {
        $('.datepicker').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true"
        });
    }

    if (podeUsarComponente('.datepicker-altfield')) {
        $('.datepicker-altfield').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true"
        }).on('changeDate', function (e) {
            var atuTargetData = $('.datepicker-altfield-target').val();
            var novaTargetData = e.format('dd/mm/yyyy');
            if(atuTargetData==''){
                $('.datepicker-altfield-target').val(novaTargetData);
                $('.datepicker-altfield-target').datepicker('update',novaTargetData);
            }
        });
    }

    if (podeUsarComponente('.datepicker-altfield-target')) {
        $('.datepicker-altfield-target').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true"
        });
    }

    if (podeUsarComponente('.datepicker-startdate-today')) {
        $('.datepicker-startdate-today').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true",
            startDate: new Date()
        });
    }

    if (podeUsarComponente('.datepicker-enddate-today')) {
        $('.datepicker-enddate-today').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true",
            endDate: new Date()
        });
    }

    if (podeUsarComponente('.datepicker-no-weekend')) {
        $('.datepicker-no-weekend').datepicker({
            language: "pt-BR",
            orientation: "bottom",
            autoclose: "true",
            daysOfWeekDisabled: "0,6"
        });
    }

    if (podeUsarComponente('.datetimepicker')) {
        $(".datetimepicker").datetimepicker({
            format: 'DD/MM/YYYY HH:mm'
        });
    }

    initCpfCnpjMask();
}

function initCpfCnpjMask() {
    if ($(".cpfcnpj-mask").length > 0) {
        $(".cpfcnpj-mask").each(function () {
            var query = $(this) != null && $(this).attr('value') ? $(this).attr('value').replace(/[^a-zA-Z 0-9]+/g, '') : ($(this) != null && $(this).text() ? $(this).text().replace(/[^a-zA-Z 0-9]+/g, '') : "");
            if (query.length <= 11) {
                $(this).mask("000.000.000-00?000");
            }

            if (query.length >= 14) {
                $(this).mask("00.000.000/0000-00");
            }
        });
    }

    $(".cpfcnpj-mask").keyup(function () {

        var query = $(this).val().replace(/[^a-zA-Z 0-9]+/g, '');

        if (query.length == 11) {
            $(this).mask("000.000.000-00?000");
        }

        if (query.length == 14) {
            $(this).mask("00.000.000/0000-00");
        }
    });
}

function podeUsarComponente(classOrId) {
    return ($(classOrId).length > 0);
}

function autoCloseAlertMessage() {
    if ($("[id*=OUTPUTTEXT_AUTOCLOSEALERTMESSAGE_AQUILES]").length == 1) {
        if (JSON.parse($("[id*=OUTPUTTEXT_AUTOCLOSEALERTMESSAGE_AQUILES]").text())) {
            window.setTimeout(function () {
                $("#aquilesMessageContainer .alert").fadeTo(15000, 0).slideUp(500, function () {
                    $(this).remove();
                });
            }, 5000);
        }
    }
}

function autoScrollToAlertMessage() {
    window.setTimeout(function () {
        if ($("[id*=panelGroupAquilesMessageContainer] #aquilesMessageContainer .alert").length == 1) {
            var id = $("[id*=panelGroupAquilesMessageContainer]").attr("id");
            window.location.hash = "";
            window.location.hash = id;
        }
    }, 500);
}

/**
 * Close open messages from jsf MESSAGES
 * */
function closeOpenAlertMessages(){
    //removido temporariamente para ajustes
    // $("#aquilesMessageContainer .alert").fadeTo(2000, 0).slideUp(500, function () {
    //     $(this).remove();
    // });
}

function superHandleAjaxEvents(data) {
    initMasks();
    autoCloseAlertMessage();
    autoScrollToAlertMessage();
    if (data.status==='begin') {
        closeOpenAlertMessages();
    }
}


function callLoading(data) {
    if (data.status == "begin") {
        $('body').loading({message: 'Processando....'});
    } else if (data.status == "complete") {
        $('body').loading('stop');
    }
}

/**
 *#################################################################
 *   SECTION TO MESSAGES IN JQUERY-CONFIRM PLUGIN STYLED TO AQUILES
 * ########################################################### ####
 */

var themeDefault = 'material';

/**
 * Confirm message with Yes or No option
 * @param title Window title
 * @param content Window content, should be the message
 * @param actionYes function() to be called when Yes button is clicked
 * @param actionNo function() to be called when No button is clicked
 * */

function confirmYesOrNo(title, content, actionYes, actionNo) {
    $.confirm({
        theme: themeDefault,
        title: title,
        content: content,
        buttons: {
            sim: {
                text: 'Sim',
                action: actionYes,
            },
            nao: {
                text: 'Não',
                action: actionNo
            }
        }
    });
}


/**
 * Alert message with OK option
 * @param title Window title
 * @param content Window content, should be the message
 * @param action function() to be called when OK button is clicked
 * */
function alertOK(title, content, action) {
    $.alert({
        theme: themeDefault,
        title: title,
        content: content,
        buttons: {
            ok: {
                text: 'OK',
                action: action,
            }
        }
    });
}