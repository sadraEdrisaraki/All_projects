"use strict";
import {postData, getData} from "./ApiUtils.js";

const API_NAME = "/image";
const API_GETAMENAGEMENT = "/amenagement";
const API_GETTOUTDEVIS = "/devis";
let token=undefined;

$(document).ready(function(){
  console.log("Starting envoyer image.js");
  token = localStorage.getItem("token");

  $("#btn_envoyer_image"). click(e =>{
    e.preventDefault();
    console.log("Sending image");
    var result = $("#image_envoyer").attr("src");
    let date = $("#date_photo").val();
    var localDate = new Date(date)
    console.log(localDate);
    let idtypeAmenagement = $("#typeAmenagement_select option:selected").val();
    let idDevis = $("#listeDevis_select option:selected").val();
    console.log("Type Amenagement" + idtypeAmenagement);
    console.log("Id Devis" + idDevis);
    const data = {image:result,date_image:date,id_amenagement:idtypeAmenagement,id_devis:idDevis,token:token};
    postData(API_NAME,data,token,onGetTypeAmenagement,onError);
  })

  //getData(API_GETAMENAGEMENT,token,onGetTypeAmenagement,onError);
  //getData(API_GETTOUTDEVIS,token,onGetDevis,onError);
});


function onImageSaved(response) {
    console.log("Image saved");
};

function onGetTypeAmenagement(response) {
    var select = $("#typeAmenagement_select");
    $.each(response,function(){
      select.append($("<option />").val(this.id_amenagement).text(this.nom))
    })
};

function onGetDevis(response) {
    var select = $("#listeDevis_select");
    console.log(response);
    $.each(response,function(){
      select.append($("<option />").val(this.idDevis).text(this.idDevis));
    })
};

function onError(response){
  console.log("Error");
  console.log(response);
}
