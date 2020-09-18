function getData(url = "", token, onGet, onError) {
  let headers;
  
  if (token)
    headers = {
      "Content-Type": "application/json",
      Authorization: token
    }
  else
    headers = {
      "Content-Type": "application/json"
    };

  $.ajax({
  type: "get",
  url: url,
  headers: headers,
  dataType: "json",
  success: onGet,
  error: onError
});
}

function getDataWithParam(url = "",param, token, onGet, onError) {
	  let headers;
	  if (token)
	    headers = {
	      "Content-Type": "application/json",
	      Authorization: token,
	      parameter : param
	    }
	  else
	    headers = {
	      "Content-Type": "application/json"
	    };

	  $.ajax({
		  type: "get",
		  url: url,
		  headers: headers,
		  dataType: "json",
		  success: onGet,
		  error: onError
	});
	}

function postData(url = "", data = {}, token ,onPostMethode, onErrorMethode) {
    let headers;
    console.log("appel à postData pour l'url : " + url);
    if (token)
        headers = {
          "Content-Type": "application/json",
          Authorization: token
        }
    else
        headers = {
          "Content-Type": "application/json"
    };
    $.ajax({
        contentType: "json",
        type: "post",
        url: url,
        headers: headers,
        data: JSON.stringify(data),
        dataType: 'json', // format des données reçu
        success: onPostMethode,
        error: onErrorMethode
    });
}

function putData(url = "", data = {}, token ,onPutMethode, onErrorMethode) {
  let headers;
  console.log("appel à putData pour l'url : " + url);
  if (token)
      headers = {
        "Content-Type": "application/json",
        Authorization: token
      }
  else
      headers = {
        "Content-Type": "application/json"
  };
  $.ajax({
      contentType: "json",
      type: "put",
      url: url,
      headers: headers,
      data: JSON.stringify(data),
      dataType: 'json', // format des données reçu
      success: onPutMethode,
      error: onErrorMethode
  });
}



export {postData,getData, putData, getDataWithParam};
