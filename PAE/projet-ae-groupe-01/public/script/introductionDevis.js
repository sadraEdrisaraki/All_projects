"use strict";
var ajoutManuel = false;

const API_NAME = "/devis";
const API_IMAGE = "/image";
import {nbr_liste_choisi , mailClient , recupererListeAmenagement} from "./genererIntroductionDevis.js";
import {postData} from "./ApiUtils.js";
const API_NAME_AJOUTAMENAGEMENT="/devis/ajoutAmenagement"
var date_devis;

$(document).ready(function(){


  var reader = new FileReader(); //Reader pour transformer l'image

  reader.onloadend = function() {
    add_image_preview(reader.result);
    //$("#image_envoyer").attr("src",reader.result);
  }

    // Rajoute le menu d'ajout manuel de client
    $("#btn_introduire_devis_ajout_manuel").click(e => {
        ajoutManuel = true;
        $("#form_introduire_client").show();
    })


    // Met à jour le champ nom, pour chaque entrée saisie
    const nom = document.getElementById("nom_client_ajout_client");
    nom.oninput = function(){
        let nom = $("#nom_client_ajout_client").val();
        $("#introduction_devis_nom").val(nom);
    };

    $("#ajout_image_devis").change(function(){
            console.log("OnChange");
        	  var file = $("#ajout_image_devis").get()[0].files[0];
            console.log(file);

        	  reader.readAsDataURL(file);
    })

    $("#btn_creer_devis").click(e =>{

        if(!checkInputs()){
          console.log("données manquante")
          return false;
        }
        console.log("création du devis");
        var devis = new Array();
        // Rajout du devis avec client existant
        let tab_liste_amenagement = new Array();

        // On récupère la liste des aménagements sélectionné
        for (let index = 0; index < nbr_liste_choisi; index++) {
            let liste_amenagement = document.getElementById('select_type_amenagement_' + (index+1));
            if( (liste_amenagement.options[liste_amenagement.selectedIndex].value) != 0){
                tab_liste_amenagement[index] = liste_amenagement.options[liste_amenagement.selectedIndex].value;
            }
        }
        date_devis = $("#date_introduire_devis").val();
        devis = {nom:$("#introduction_devis_nom").val() , email:mailClient , date_devis:$("#date_introduire_devis").val(),
                    montant_total_devis : $("#introduction_devis_montant_total").val() , duree_travaux : $("#introduction_devis_duree_travaux").val()
                , liste_amenagement:tab_liste_amenagement , ajout:"manuel"};
        console.log(devis);
        postData(API_NAME , devis , localStorage.getItem("token") , onCreationDevis , onCreationDevisError);
    })
	// $("#btn_nouveau_amenagement").click(e =>{
    //     $("#ajout_de_text").append("<input type='text' id='input_nom_amenagement' />");
    //     let test = document.getElementById("input_nom_amenagement");
    //     test.addEventListener('change',envoitNouveauAmenagement);
	// })

});
// function envoitNouveauAmenagement(){
//     let element = document.getElementById("input_nom_amenagement").value;
//     console.log(element);

//     let data ={nom:element};
//     postData(API_NAME_AJOUTAMENAGEMENT,data,localStorage.getItem("token"),onEnvoitNouveauAmenagement,onErrorEnvoitNouveauAmenagement);
//     $("#ajout_de_text").empty();
// }
// function onEnvoitNouveauAmenagement(){
//     console.log("envoit réussit");
// }
// function onErrorEnvoitNouveauAmenagement(){
//     console.log("envoit aménagment rater");
// }

function onCreationDevis(response){

    let idtypeAmenagement = $("#select_type_amenagement_1 option:selected").val();
    //Une fois le devis créé on peut lui ajouter les photos avant aménagement
    //let date_photo = new Date()
    $("#images_preview > img").each((index,element) => {
        const data = {image:element.src,date_image:date_devis,id_amenagement:idtypeAmenagement,id_devis:response.id_devis,token:localStorage.getItem("token")};
        postData(API_IMAGE,data,localStorage.getItem("token"),onImageAdded,onImageNotAdded);
    });

    console.log("Devis crée");
    $("#msg_introduction_devis").show();
    $("#msg_introduction_devis").css('color' , 'green');
    $("#msg_introduction_devis").text(response.message);
    rafraichirInput();
}

function onCreationDevisError(response){
    console.log("erreur création devis");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").text(response.message);
    $("#msg_introduction_devis").show();

    
}

function add_image_preview(image_src){
  $("#images_preview").append("<img class=\"images_preview\" src=\""+ image_src +"\" >")
}

function onImageAdded(response){
  console.log("Image ajoutée");
}

function onImageNotAdded(response){
  console.log("Image non ajoutée");
}

function checkInputs(){
  if($("#introduction_devis_nom").val() == ""){
    $("#msg_introduction_devis").text("Veuillez entrer le nom du client ou en créer un !");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").show();
    return false;
  }
  else if($("#date_introduire_devis").val() == ""){
    $("#msg_introduction_devis").text("Veuillez entrer une date !");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").show();
    return false;
  }
  else if($("#introduction_devis_montant_total").val() == ""){
    $("#msg_introduction_devis").text("Veuillez entrer  un montant total !");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").show();
    return false;
  }
  else if($("#introduction_devis_duree_travaux").val() == ""){
    $("#msg_introduction_devis").text("Veuillez entrer une durée des travaux !");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").show();
    return false;
  }
  else if(nbr_liste_choisi == 1){
    $("#msg_introduction_devis").text("Veuillez entrer au moins un type d'aménagement !");
    $("#msg_introduction_devis").css('color' , 'red');
    $("#msg_introduction_devis").show();
    return false;
  }
  $("#msg_introduction_devis").hide();
  $("#msg_introduction_devis").css('color' , 'green');
  return true
}

function rafraichirInput(){

  $("#introduction_devis_nom").val('');
  $("#date_introduire_devis").val('');
  $("#introduction_devis_montant_total").val('');
  $("#introduction_devis_duree_travaux").val('');

}