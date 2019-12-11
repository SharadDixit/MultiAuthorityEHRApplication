var main = function(){

    var success = false;
    var EHRData = null;

    $("#patientSelection").submit(function (event) {

        event.preventDefault();
        $.ajax({
            type: "POST",
            url: "http://localhost:11001/FrontEndService/ehrSelection/",

            data: $("#patientSelection").serialize(),
            async:false,
            success: function(data)
            {
                if(data.length===0){
                    alert("UserName or Password Incorrect");
                }else {
                    success = true;
                    EHRData = data;
                }

            },
            error: function()
            {
                console.log('An error occurred.');
                alert("alert");
            }

        });
        if (success){
            document.body.innerHTML = "";
            $('body').load("patientEHRDetails.html");

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
        $.each(EHRData, function (EHRType, EHRArray) {
            if (EHRType==="PatientName"){
                addPatientName(EHRArray);
            }else if (EHRType==="DoctorName"){
                addDoctorName(EHRArray);
            }else if (EHRType==="Attributes"){
                addAttributes(EHRArray);
            } else {
                var template = $('#hidden-template').html();

                //Clone the template
                var item = $(template).clone();

                //Find the
                $(item).find('#ehrType').append(EHRType);

                var permission = EHRArray[0];
                if (permission ==="Read"){
                    applyPermission(item);
                }
                var EHRActualData = EHRArray[1];

                //Change 'bar' to '-Bar'
                setTimeout(function () {
                    // var iframe = document.querySelector('iframe');
                    // iframe.id = key;
                    // iframe.contentDocument.write(value);
                    var e = document.getElementById("ehrData");
                    e.id = EHRType;

                    var context = $('#'+EHRType)[0].contentWindow.document,
                        $body = $('body', context);
                    $body.html(EHRActualData);

                    // iframe.name = key;
                    // iframe.contentDocument.write(value);
                },50);

                // document.querySelector('iframe').contentDocument.write(value);

                //Append to the source
                $('#target').append(item);
            }
        });
    }
    function applyPermission(item) {
        $(item).find(".checkbox").addClass("collapse");
    }
    function addPatientName(value) {
        document.getElementById("patientName").append(value);
    }
    function addDoctorName(value) {
        document.getElementById("doctorName").append(value);
    }
    function addAttributes(value) {
        document.getElementById("certificate").append(value[0]);
        document.getElementById("specialCertificate").append(value[1]);
        document.getElementById("medicalIns").append(value[2]);
        document.getElementById("role").append(value[3]);
    }

};
$(document).ready(main);