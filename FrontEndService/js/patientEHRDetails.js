var main = function() {

    var iframeID = null;

    $(document).on("click", ".checkbox", function () {


        iframeID = $(this).closest('div.checkbox').next('div.panel-body').children('iframe').attr("id");

        var iframeData = $("#"+iframeID).contents().find("body").html();

        var inputModal = $(".modal-body #inputEdit");

        inputModal.val( iframeData);
        // As pointed out in comments,
        // it is unnecessary to have to manually call the modal.
        // $('#addBookDialog').modal('show');


    });

    $(document).on("click", "#saveEHRChanges", function () {
        var dataAppendIframe = document.getElementById("inputEdit").value;

        document.getElementsByTagName('button')[0].click();

        var patientName = document.getElementById("patientName").innerHTML;

        var map = {};
        map[iframeID] = dataAppendIframe;
        map["PatientName"]= patientName;

        $.ajax({
            type: "POST",
            url: "http://localhost:11001/FrontEndService/saveEHRChanges/",
            data: map,
            success: function () {
                alert("Data Successfully Saved");
                var context = $('#'+iframeID)[0].contentWindow.document,
                    $body = $('body', context);
                $body.html(dataAppendIframe);
            },
            error: function () {
                alert("Data Not saved");
            }
        });
    });


};
$(document).ready(main);