$(document).ready(function () {
console.log("ok");
  $("form").submit(function (event) {
    var formData = {
      name: $("#name").val(),
      age: $("#age").val(),
    };

    console.log(formData);

    $.ajax({
      type: "POST",
      data: "{\"name\":\"lll\",  \"age\":\"25\""}",
      dataType: "json",
      contentType: "application/json",
      encoding: "true"
    }).done(function (data) {
      console.log(data);
    });

    event.preventDefault();
  });
})