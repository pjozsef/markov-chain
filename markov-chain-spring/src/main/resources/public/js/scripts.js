function generate() {
    var payload = {
        constraints: {}
    }
    extract('words', payload, splitBy('\n'))
    extract('order', payload)
    extract('seed', payload)
    extract('minLength', payload.constraints)
    extract('maxLength', payload.constraints)
    extract('startsWith', payload.constraints)
    extract('endsWith', payload.constraints)
    extract('contains', payload.constraints, splitBy(','))
    extract('notContains', payload.constraints, splitBy(','))

    send(payload)
}

function extract(key, object, f) {
    var value = document.getElementById(key).value
    if(value) {
        if(f) {
            object[key] = f(value)
        } else {
            object[key] = value
        }
    }
}

function splitBy(separator) {
    return function(value) {
        return value.split(separator).map(function(item){
            return item.trim()
        }).filter(Boolean)
    }
}

function send(payload) {
    var xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        if (this.status == 200) {
            console.log(xhttp.response)
            console.log(typeof xhttp.response)
            var response = JSON.parse(xhttp.response)
            var resultContainer = document.getElementById("result")
            if(response.length == 0){
                resultContainer.innerHTML = '<li>No results</li>'
            } else {
               resultContainer.innerHTML = response.map(function(item){
                return '<li>'+item+'</li>';
               }).join('')
            }
        } else {
            console.log(xhttp.responseText)
        }
    };
    xhttp.open('POST', '/api/generate', true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(JSON.stringify(payload));
}

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})
