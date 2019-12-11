var main = function(){

    var success = false;
    var patientList = null;

    var username = null;

        $("#staffLogin").submit(function (event) {
            username = document.getElementById("username").value;
        // Important line below, otherwise the ajax always go to error, however data goes to the backend
        event.preventDefault(); // Prevent the form from submitting via the browser
        $.ajax({
            type: "POST",
            url: "http://localhost:11001/FrontEndService/staffLogin/",

            data: $("#staffLogin").serialize(),
            async:false,
            success: function(data)
            {
                console.log('Submission was successful.');
                if(data.length===0){
                    alert("UserName or Password Incorrect");
                }else {
                    success = true;
                    patientList = data;
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
            error: function()
            {
                console.log('An error occurred.');
                alert("alert");
            }

        });

        if (success){
            // var x = window.open("trial.html");
            // x.document.addEventListener('readystatechange', function () {
            //     if (x.document.readyState === "complete") {
            //         x.document.getElementById("trial").append("sdada");
            //     }
            // });
            // var x = window.location.replace("trial.html");
            // $(location).attr("href","trial.html",patientList);
            document.body.innerHTML = "";
            $('body').load("patientSelection.html");

            // This works but we see a delay
            // setTimeout(function(){document.getElementById("check").append("das");}, 1000);

                // $("#check").append("dasda");
            // Best solution for safari browser
            if(/Safari/i.test(navigator.userAgent)){ //Test for Safari
                var _timer=setInterval(function(){
                        if(/loaded|complete/.test(document.readyState)){
                            clearInterval(_timer);
                            addUserName();
                            insertPatientList();
                        }}
                    , 10)
            }
            // XXXXXXXXXX  http://www.javascriptkit.com/dhtmltutors/domready.shtml XXXXXXXXXX Very important link


            // window.onload=function () {
            //
            //         setTimeout(function (){document.getElementById("check").append("das")}, 1000)
            //
            // }
            // setTimeout(arguments.callee, 100);
            //
            // window.addEventListener("load", () => {
            //     // Fully loaded!
            //
            // });


            // a.append("dasd");

        }
    });
    function addUserName() {
        document.getElementById("icon").append(username);
    }
    function insertPatientList() {
        $.each(patientList, function(patient, hospitalDoctorNameArray) {
            // document.getElementById("check").append(key+"="+value);
            var template = $('#hidden-template').html();

            //Clone the template
            var item = $(template).clone();

            //Find the
            $(item).find('#patientName').append(patient);

            //Change 'bar' to '-Bar'
            $(item).find('#medName').append(hospitalDoctorNameArray[0]);

            $(item).find('#docName').append(hospitalDoctorNameArray[1]);

            $(item).find('#inputVal').val(patient);

            $(item).find('#doctorName').val(username);
            //Append to the source
            $('#target').append(item);

        });
    }

};

$(document).ready(main);