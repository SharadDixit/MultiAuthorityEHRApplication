var main = function(){

    var success = false;
    var EHRData = null;

    $("#patientLogin").submit(function (event) {
        // Important line below, otherwise the ajax always go to error, however data goes to the backend
        event.preventDefault(); // Prevent the form from submitting via the browser
        $.ajax({
            type: "POST",
            url: "http://localhost:11001/FrontEndService/patientLogin/",

            data: $("#patientLogin").serialize(),
            async: false,
            success: function (data) {
                console.log('Submission was successful.');
                if (data.length === 0) {
                    alert("UserName or Password Incorrect");
                } else {
                    success = true;
                    EHRData = data;
                    // window.location.href = ("trial.html");
                    // var trial = $("#trial");
                    // trial.append("das");

                    // var x=window.open("http://localhost:11001/FrontEndService/trial.html/");
                    // x.document.open();
                    // x.document.write('content');
                    // x.document.close();
                }

                // $.each(data, function(key, value) {
                //     alert(key+"="+value);
                // });
            },
            error: function () {
                console.log('An error occurred.');
                alert("alert");
            }

        });
        if (success){
            document.body.innerHTML = "";
            $('body').load("patientEHRView.html");

            if(/Safari/i.test(navigator.userAgent)){ //Test for Safari
                var _timer=setInterval(function(){
                        if(/loaded|complete/.test(document.readyState)){
                            clearInterval(_timer);
                            insertEHRData();

                            // setTimeout(function(){
                            //         document.querySelector('iframe').contentDocument.write("");
                            //     }
                            //     , 10000);

                        }}
                    , 10)
            }

        }

    });
    function insertEHRData() {
        $.each(EHRData, function (EHRType, EHRData) {
            if (EHRType==="PatientName"){
                addPatientName(EHRData);
            }else {
                var template = $('#hidden-template').html();

                //Clone the template
                var item = $(template).clone();

                //Find the
                $(item).find('#ehrType').append(EHRType);

                //Change 'bar' to '-Bar'
                setTimeout(function () {
                    // var iframe = document.querySelector('iframe');
                    // iframe.id = key;
                    // iframe.contentDocument.write(value);
                    var e = document.getElementById("ehrData");
                    e.id = EHRType;

                    var context = $('#'+EHRType)[0].contentWindow.document,
                        $body = $('body', context);
                    $body.html(EHRData);

                    // iframe.name = key;
                    // iframe.contentDocument.write(value);
                },50);

                // document.querySelector('iframe').contentDocument.write(value);

                //Append to the source
                $('#target').append(item);
            }
        });
    }
    function addPatientName(value) {
        document.getElementById("patientName").append(value);
    }
};
$(document).ready(main);