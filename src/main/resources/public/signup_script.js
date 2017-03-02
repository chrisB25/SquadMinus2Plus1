function signupHandler() {}

signupHandler.submitHandler = function( ) {

    var name = $$("name").getValue();
    var last = $$("last").getValue();
    var user = $$("user").getValue();
    var pass = $$("pass").getValue();
    var passConfirm = $$("passConfirm").getValue();
    var email = $$("email").getValue();

    // Check to make sure all of the required information is there
    if (!name || !last || !user || !pass || !passConfirm || !email ) {
        signupHandler.errorHandler("Signup Failed. Please fill all fields.");
    }

    // Check to make sure the password and confirm matches
    if (pass != passConfirm) {
        signupHandler.errorHandler("Signup Failed. Password does not match password confirm.");
    }

    // Check for valid email format
    if (!pageUtil.validateEmail(email)){
        signupHandler.errorHandler("Signup Failed. Invalid email.");
    }

    // Make sure username is valid format
    if( !pageUtil.validateAlphaNum(user)) {
        signupHandler.errorHandler("Signup Failed. Invalid username (only alphanumeric accepted).");
    }

    // Make sure name is valid format
    if( !pageUtil.validateAlphaNum(name)) {
        signupHandler.errorHandler("Signup Failed. Invalid name (only alphanumeric accepted).");
    }

    // Make sure last name is valid format
    if( !pageUtil.validateAlphaNum(last)) {
        signupHandler.errorHandler("Signup Failed. Invalid last name (only alphanumeric accepted).");
    }

    // Make sure password is valid format
    if( !pageUtil.validateAlphaNum(pass)) {
        signupHandler.errorHandler("Signup Failed. Invalid password (only alphanumeric accepted).");
    }

    /*
    webix.ajax().post("/signup", $$("signup_form").getValues(), {
        error:signupHandler.errorHandler,
        success:function(dataString){
            if(dataString === null) {
                signupHandler.errorHandler();
            }
            var data = JSON.parse(dataString);
            var userName = $$("user").getValue();
            if( data !== null && (data.userName === userName || data.email === userName) ) {
                $$("signup_form").hide();
                $$("signup_top_label").hide();
                $$("signup_result").css = "label_text";
                $$("signup_result").setValue("Signup Succeeded.");
                webix.alert("User Signup Successful.", signupHandler.signupSuccessHandler);
            } else {
                signupHandler.errorHandler();
            }
        }
    });
    */
};

signupHandler.errorHandler = function(message) {
    $$("signup_result").define("css","label_error");
    $$("signup_result").setValue(message);
};

signupHandler.homeHandler = function( ) {
    location.href = "/";
};

signupHandler.loginHandler = function( ) {
    location.href = "/login";
};

signupHandler.signupSuccessHandler = function( ) {
    location.href = "/";
    //location.href = "/profile";
};