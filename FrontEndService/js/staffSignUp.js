var main = function(){
    $('.addMoreCertification').on("click", function () {

        var certificationSelector = document.getElementById("certification");

        clone(certificationSelector);

    });

    $('.addMoreSpecialization').on("click", function () {

        var specializationSelector = document.getElementById("specialization");

        clone(specializationSelector);
    });

};

function clone(htmlSelector){

    var clone = htmlSelector.cloneNode(true);

    clone.id = "";

    htmlSelector.parentNode.appendChild(clone);
}

$(document).ready(main);