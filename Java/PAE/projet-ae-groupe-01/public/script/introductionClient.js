"use strict";
const API_NAME = "/client";
import {postData} from "./ApiUtils.js";
import {gererAutoCompletion , verifierMail} from "./genererIntroductionDevis.js";

$(document).ready(function() {

    // REGEX
    var mailExpr = '^[a-zA-Z0-9._]+@[a-zA-Z0-9.]+\.[a-z]+$';
    var mailRegex = new RegExp(mailExpr);

    // Listener sur le bouton
    $("#btn_creer_client").click(e => {
        // Verifie tous les champs: TODO
        if ($("#nom_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("nom du client manquant");
            $("#msg_error_creation_client").show();
        } 
        else if ($("#prenom_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("prenom du client manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#rue_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("rue manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#numero_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("numero manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#cp_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("code postal manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#ville_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("ville manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#email_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("email manquant");
            $("#msg_error_creation_client").show();
        }
        else if ($("#telephone_client_ajout_client").val() == "") {
            $("#msg_error_creation_client").css('color' , 'red');
            $("#msg_error_creation_client").text("telephone manquant");
            $("#msg_error_creation_client").show();
        }
        else {
            $("#msg_error_creation_client").hide();
            let boite = $("#boite_client_ajout_client").val();
            if(boite === ""){
                boite == null;
            }
            const data = {nom_client:$("#nom_client_ajout_client").val() , prenom_client:$("#prenom_client_ajout_client").val() ,
            rue:$("#rue_client_ajout_client").val() , numero:$("#numero_client_ajout_client").val() , code_postal:$("#cp_client_ajout_client").val() , 
            boite:$("#boite_client_ajout_client").val() , ville:$("#ville_client_ajout_client").val() , email:$("#email_client_ajout_client").val() , 
            telephone:$("#telephone_client_ajout_client").val()};
            console.log(data);
            postData(API_NAME , data , localStorage.getItem("token"), onCreationClient , onCreationClientError);
        }
    })
})

function onCreationClient(response){
    $("#msg_error_creation_client").css('color' , 'green');
    $("#msg_error_creation_client").text("Ajout reussi");
    $("#msg_error_creation_client").show();
    console.log("création du client réussi");
    gererAutoCompletion();
    verifierMail();
}

function onCreationClientError(response){
    $("#msg_error_creation_client").css('color' , 'red');
    $("#msg_error_creation_client").text(response.message);
    $("#msg_error_creation_client").show();
}
