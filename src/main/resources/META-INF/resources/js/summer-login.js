$(document).ready(function () {
    jsf.ajax.addOnEvent(handleAjaxEvents);
});

function handleAjaxEvents(data) {
    superHandleAjaxEvents(data);
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
    if ($("[id*="+containsId+"]").length == 1) {
        $("[id*="+containsId+"]").click();
    }
}