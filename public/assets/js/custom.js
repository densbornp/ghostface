// Called when window loads
window.onload = function () {
  $("#btn-download").attr("disabled", true);
  $("#status-label").html("");
  $("#minNeighbors").attr("disabled", true);
  $("#scaleFactor").attr("disabled", true);
  $("#minNeighbors").val("3");
  $("#outputMinNeighbors").val("3");
  $("#scaleFactor").val("1.05");
  $("#outputScale").val("1.05");
  $("#options").attr("disabled", true);
  $("#cascade_options").attr("disabled", true);
  $("#btn-submit").attr("disabled", true);

  var cookie = Cookies.get("user_session");
  if (cookie === undefined) {
    setTimeout(function () {
      $("#cookieConsent").fadeIn(200);
    }, 200);
  }

  $("#closeCookieConsent, .cookieConsentOK").click(function () {
    $("#cookieConsent").fadeOut(200);
    $.ajax({
      type: "POST",
      url: "/cookie",
      dataType: "application/html",
      success: function (res) {
        console.log(res);
      },
    });
  });
};

// File appears on select
$("#inputFile").on("change", function () {
  var cookie = Cookies.get("user_session");
  if (cookie === undefined) {
    alert("Please accept Cookies to use the image convertion function.");
    return;
  }
  window.fileName = $(this).val().split("\\").pop();
  $(this)
    .siblings(".custom-file-label")
    .addClass("selected")
    .html(window.fileName);
  var form = $("#uploadForm");
  form.submit();
});

// Called when new image gets uploaded
$("form#uploadForm").submit(function (evt) {
  evt.preventDefault();
  let form = $(this);
  $("#btn-download").attr("disabled", true);
  $("#status-label").html("Image uploading...");
  $("#btn-submit").attr("disabled", true);
  $.ajax({
    url: form.attr("action"),
    type: form.attr("method"),
    data: new FormData(this),
    processData: false,
    contentType: false,
    success: function (response) {
      var cookieValue = Cookies.get("user_session");
      let original =
        "uploads/" +
        cookieValue +
        "/original." +
        window.fileName.split(".").pop();
      let converted =
        "uploads/" + cookieValue + "/tmp." + window.fileName.split(".").pop();
      $("#uploadedImage").attr("src", original + "?" + new Date().getTime());
      $("#convertedImage").attr("src", converted + "?" + new Date().getTime());
      $("#btn-download").attr("disabled", false);
      $("#status-label").html("Image successfully uploaded!");
      $("#minNeighbors").attr("disabled", false);
      $("#scaleFactor").attr("disabled", false);
      $("#minNeighbors").val("3");
      $("#outputMinNeighbors").val("3");
      $("#scaleFactor").val("1.05");
      $("#outputScale").val("1.05");
      $("#options").attr("disabled", false);
      $("#options").val("none");
      $("#cascade_options").attr("disabled", false);
      $("#cascade_options").val("haar");
      $("#btn-submit").attr("disabled", false);
    },
    error: function (response) {
      alert("Image upload failed!");
      $("#btn-download").attr("disabled", true);
      $("#status-label").html("An error occurred!");
    },
  });
  return false;
});

// Called when image gets converted
$("form#convertForm").submit(function (evt) {
  evt.preventDefault();
  let form = $(this);
  $(".imageName").val(window.fileName);
  $("#btn-download").attr("disabled", true);
  $("#status-label").html("Image converting...");
  $("#btn-submit").attr("disabled", true);
  $.ajax({
    url: form.attr("action"),
    type: form.attr("method"),
    data: new FormData(this),
    processData: false,
    contentType: false,
    success: function (resp) {
      var cookieValue = Cookies.get("user_session");
      let converted =
        "uploads/" + cookieValue + "/tmp." + window.fileName.split(".").pop();
      $("#convertedImage").attr("src", converted + "?" + new Date().getTime());
      $("#btn-download").attr("disabled", false);
      $("#status-label").html("Image successfully converted!");
      $("#btn-submit").attr("disabled", false);
    },
    error: function (resp) {
      alert("Image convertion failed!");
      $("#btn-submit").attr("disabled", false);
      $("#status-label").html("An error occurred!");
    },
  });
});

function updateSensitivityText(val) {
  document.getElementById("outputMinNeighbors").value = val;
}

function updateScaleText(val) {
  document.getElementById("outputScale").value = val;
}
