"use strict"
const API_NAME = "/utilisateur";
import {postData} from "./ApiUtils.js";
import {revenirPageLogin} from "./loginPage.js";


$(document).ready( function(){
    $("#msg_error_inscription").hide();
    $("#btn_inscription_to_login").click(e=>{
        revenirPageLogin();
    })
    $("#btn_inscription").click(e =>{

        if($("#pseudo_inscription").val() == ""){
            $("#msg_error_inscription").text("pseudo manquant");
            $("#msg_error_inscription").show();
        }
        else if( $("#mot_de_passe_inscription").val() == "" ||  $("#mot_de_passe_inscription_bis").val() == ""){
            $("#msg_error_inscription").text("mots de passes manquant");
            $("#msg_error_inscription").show();
        }
        else if( $("#mot_de_passe_inscription").val() != $("#mot_de_passe_inscription_bis").val() ){
            $("#msg_error_inscription").text("mots de passes différents");
            $("#msg_error_inscription").show();
        }
        else if($("#nom_inscription").val() == ""){
            $("#msg_error_inscription").text("nom manquant");
            $("#msg_error_inscription").show();
        }
        else if($("#prenom_inscription").val() == ""){
            $("#msg_error_inscription").text("prenom manquant");
            $("#msg_error_inscription").show();
        }
        else if($("#ville_inscription").val() ==""){
            $("#msg_error_inscription").text("ville manquant");
            $("#msg_error_inscription").show();
        }
        else if($("#email_inscription").val() ==""){
            $("#msg_error_inscription").text("email manquant");
            $("#msg_error_inscription").show();
        }
        else{
            $("#msg_error_inscription").hide();
            const data = {pseudo : $("#pseudo_inscription").val() , mot_de_passe:$("#mot_de_passe_inscription").val() ,
                          nom:$("#nom_inscription").val() , prenom:$("#prenom_inscription").val() , ville:$("#ville_inscription").val(),
                          email: $("#email_inscription").val()};
            postData(API_NAME , data , null, onInscription , onInscriptionError);
        }
        
    })
})

function onInscription(response){
    $("#msg_error_inscription").hide();
    console.log("inscription reussi");
    $("#msg_reussite_inscription").show();

}

function onInscriptionError(response){
    console.log(response.responseJSON.message);
    $("#msg_error_inscription").text(response.responseJSON.message);
    $("#msg_error_inscription").show();
}
